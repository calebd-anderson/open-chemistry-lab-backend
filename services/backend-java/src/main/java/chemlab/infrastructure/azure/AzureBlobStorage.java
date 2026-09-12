package chemlab.infrastructure.azure;

import chemlab.infrastructure.storage.ImageStorageService;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Profile("prod")
public class AzureBlobStorage implements ImageStorageService {
    private final BlobServiceClient blobServiceClient;

    @Value("${azure.containerName}")
    private String containerName;

    public AzureBlobStorage(@Value("${azure.storageAccountName}") String storageAccountName) {
        DefaultAzureCredential credential =
                new DefaultAzureCredentialBuilder().build();
        String accountEndpoint = String.format("https://%1$s.blob.core.windows.net/", storageAccountName);
        this.blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(accountEndpoint)
                .credential(credential)
                .buildClient();
    }

    @Override
    public String saveImage(String userId, String filename, InputStream img) {
        BlobContainerClient container =
                blobServiceClient.getBlobContainerClient(containerName);

        container.createIfNotExists();

        String blobName = userId + "/" + filename;
        BlobClient blobClient = container.getBlobClient(blobName);

        try {
            byte[] bytes = img.readAllBytes();
            long length = bytes.length;
            try (ByteArrayInputStream dataStream =
                         new ByteArrayInputStream(bytes)) {
                blobClient.upload(dataStream, length, true);
            }
            return blobName;
        } catch (IOException e) {
            log.error("Failed to read image data for user {} and filename {}: {}", userId, filename, e.getMessage());
            throw new RuntimeException("Failed to read image data", e);
        } catch(BlobStorageException e) {
            log.error("Failed to upload image to Azure Blob Storage for user {} and filename {}: {}", userId, filename, e.getMessage());
            throw new RuntimeException("Failed to upload image to Azure Blob Storage", e);
        }
    }

    @Override
    public byte[] getImage(String blobName) {
        BlobContainerClient container =
                blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = container.getBlobClient(blobName);

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {
            blobClient.downloadStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to download image for blob {}: {}", blobName, e.getMessage());
            throw new RuntimeException("Failed to download image", e);
        } catch (BlobStorageException e) {
            log.error("Failed to download image from Azure Blob Storage for blob {}: {}", blobName, e.getMessage());
            throw new RuntimeException("Failed to download image from Azure Blob Storage", e);
        }
    }

    @Override
    public void deleteImage(String storagePath) {
        throw new NotImplementedException();
    }
}
