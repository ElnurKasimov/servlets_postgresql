package model.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesConfig {
    public Properties loadProperties(String filePath) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(filePath);) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
