package io.jenkins.plugins.storage;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class WriteUtil {

    private static final Logger LOGGER = Logger.getLogger(WriteUtil.class.getName());
    public static final String UTF_8 = "UTF-8";

    public static void writeProjectPropertie(File storeFile, String propertyName, String propertyValue) {
        StringBuilder fileContent = new StringBuilder();
        System.out.println("Escribiendo en fichero properties");
        try {
            fileContent.insert(0,propertyName).append("=")
                    .append(propertyValue);
            Files.write(fileContent.toString(), storeFile, Charset.forName(UTF_8));
        } catch (IOException e) {
            LOGGER.warning(String.format("store build messages error : %s", e.getMessage()));
        }
    }

}
