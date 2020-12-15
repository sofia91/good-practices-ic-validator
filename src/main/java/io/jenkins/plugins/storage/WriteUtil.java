package io.jenkins.plugins.storage;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class WriteUtil {

    private static final Logger LOGGER = Logger.getLogger(WriteUtil.class.getName());
    public static final String UTF_8 = "UTF-8";

    public static void writeProjectPropertie(File storeFile, String propertyValue1, String propertyValue2, String timeRepairBuilds, String nRepairBuilds) {
        StringBuilder fileContent = new StringBuilder();
        System.out.println("Escribiendo en fichero properties");
        try {
            fileContent.insert(0,Constants.MAX_TIME_TO_BUILD).append("=")
                    .append(propertyValue1).append("\n").append(Constants.LAST_N_BUILDS)
                    .append("=").append(propertyValue2).append("\n").append(Constants.TIME_REPAIR_BUILDS)
                    .append("=").append(timeRepairBuilds).append("\n").append(Constants.N_REPAIR_BUILDS)
                    .append("=").append(nRepairBuilds);
            Files.write(fileContent.toString(), storeFile, Charset.forName(UTF_8));
        } catch (IOException e) {
            LOGGER.warning(String.format("store build messages error : %s", e.getMessage()));
        }
    }

}
