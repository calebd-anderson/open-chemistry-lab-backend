package chemlab.infrastructure.robohash;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class RoboHashServiceImpl implements RoboHashService {

    public static final String TEMP_PROFILE_IMAGE_BASE_URL = "https://robohash.org/";

    public byte[] getProfileImage(String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (InputStream inputStream = url.openStream()) {
                int bytesRead;
                byte[] chunk = new byte[1024];
                while ((bytesRead = inputStream.read(chunk)) > 0) {
                    byteArrayOutputStream.write(chunk, 0, bytesRead);
                }
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
