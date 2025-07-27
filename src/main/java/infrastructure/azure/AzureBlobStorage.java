package infrastructure.azure;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class AzureBlobStorage {
    @Value("${azure.connectionString}")
    private String connection;
    @Value("${azure.sasToken}")
    private String sasToken;

    private String containerName = "chemlab-profile-images";

    public String saveImage(String userId, String filename, InputStream img) {
        /* Create a new BlobServiceClient with a SAS Token */
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(connection)
                .sasToken(sasToken)
                .buildClient();

        /* Create a new container client */
        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint(connection)
                .sasToken(sasToken)
                .containerName(containerName)
                .buildClient();

        try {
            blobContainerClient = blobServiceClient.createBlobContainer(containerName);
        } catch (BlobStorageException ex) {
            log.error(ex.getMessage());
            // The container may already exist, so don't throw an error
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
        }
        try {
            /* Upload the file to the container */
            String blobName = userId + "/" + filename;
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(img);
            return blobName;
        } catch (BlobStorageException ex) {
            log.error(ex.getMessage());
            // throw if the blob already exists
            if (ex.getErrorCode().equals(BlobErrorCode.BLOB_ALREADY_EXISTS)) {
                throw ex;
            }
            throw ex;
        }
    }

    public byte[] getImage(String blobName) {
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(connection)
                .sasToken(sasToken)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            blobClient.download(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
