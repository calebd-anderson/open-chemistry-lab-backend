package chemlab.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageService {
    String saveImage(String userId, String filename, InputStream img) throws IOException;
    byte[] getImage(String storagePath) throws IOException;
}
