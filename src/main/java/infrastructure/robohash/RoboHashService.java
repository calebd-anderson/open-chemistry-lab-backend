package infrastructure.robohash;

import java.io.IOException;

public interface RoboHashService {
    byte[] getProfileImage(String username) throws IOException;
}
