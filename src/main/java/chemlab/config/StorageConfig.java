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

    @Autowired
    private AzureBlobStorage azureBlobStorage;

    @Bean
    @Profile("dev")
    public ImageStorageService localFileSystemStorage(
            @Value("${storage.local.path:storage/uploads}") String storagePath
    ) throws IOException {
        return new LocalFileSystemStorage(storagePath);
    }

    @Bean
    @Profile("!dev")
    public ImageStorageService azureStorageService() {
        return azureBlobStorage;
    }
}
