package io.jenkins.plugins.storage;


import hudson.model.Job;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ReadUtil {

    private static final Logger LOGGER = Logger.getLogger(ReadUtil.class.getName());

    public static Properties getJobProperties(Job job, String nameFile) {
        FileInputStream fs = null;
        try {
            File rootDir = job.getRootDir();
            File file = new File(rootDir.getAbsolutePath() + File.separator + nameFile);


            fs = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fs);
            return properties;
        } catch (IOException e) {
            LOGGER.warning(String.format("get property file error : %s", e.getMessage()));
            return null;
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    LOGGER.warning(String.format("error while closing the stream: %s", e.getMessage()));
                }
            }
        }
    }

}
