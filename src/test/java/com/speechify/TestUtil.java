package com.speechify;

import com.speechify.model.Client;
import com.speechify.model.User;
import com.speechify.model.UserDetails;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

public class TestUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TestUtil() {
        // util class
    }

    public static String getTempFile() throws IOException {
        final var tempFilePath = Files.createTempFile(UUID.randomUUID().toString(), ".json");
        tempFilePath.toFile().deleteOnExit();
        final var resourceUrl = TestUtil.class.getClassLoader().getResource("test.db.json");
        if (resourceUrl == null) {
            throw new IllegalStateException();
        }
        final var jsonFile = new File(resourceUrl.getFile());
        Files.copy(jsonFile.toPath(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath.toAbsolutePath().toString();
    }

    public static User getUser(final String id) {
        return new User(id, new Client("bad", "bad"), new UserDetails(
                LocalDate.EPOCH,
                "hi",
                "hi",
                "hi"),
                true,
                10000.0);
    }
}
