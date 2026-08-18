package chemlab.config;

import chemlab.infrastructure.azure.AzureBlobStorage;
import chemlab.infrastructure.storage.ImageStorageService;
import chemlab.infrastructure.storage.LocalFileSystemStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
public class StorageConfig {

    @Autowired(required = false)
    private AzureBlobStorage azureBlobStorage;

    @Bean(name = "imageStorageService")
    @Profile("!prod")
    public ImageStorageService localImageStorage(
            @Value("${storage.local.path:storage/uploads}") String storagePath
    ) throws IOException {
        return new LocalFileSystemStorage(storagePath);
    }

    @Bean(name = "imageStorageService")
    @Profile("prod")
    public ImageStorageService imageStorage() {
        if (azureBlobStorage != null) {
            return azureBlobStorage;
        }
        // Fallback to local storage if Azure isn't configured
        try {
            return new LocalFileSystemStorage("storage/uploads");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create local fallback storage: " + e.getMessage(), e);
        }
    }
}
