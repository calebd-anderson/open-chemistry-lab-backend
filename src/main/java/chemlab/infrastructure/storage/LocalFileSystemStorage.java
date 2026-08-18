package chemlab.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class LocalFileSystemStorage implements ImageStorageService {

    private final Path storageLocation;

    public LocalFileSystemStorage(String storagePath) throws IOException {
        this.storageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(this.storageLocation);
        log.info("Local storage initialized at: {}", this.storageLocation);
    }

    @Override
    public String saveImage(String userId, String filename, InputStream img) throws IOException {
        String blobName = userId + "/" + filename;
        Path targetLocation = this.storageLocation.resolve(blobName);
        Files.createDirectories(targetLocation.getParent());
        Files.copy(img, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        log.info("Image saved to local storage: {}", blobName);
        return blobName;
    }

    @Override
    public byte[] getImage(String storagePath) {
        try {
            Path filePath = this.storageLocation.resolve(storagePath);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error reading image from local storage: {}", e.getMessage());
            throw new RuntimeException("Could not read image from local storage", e);
        }
    }
}
