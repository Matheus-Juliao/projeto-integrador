package com.unasp.taskmanagement.config.component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

@Component
public class MessageProperty {
    private Properties properties;

    public MessageProperty() {
        Resource resource = new ClassPathResource("/messages.properties");
        try {
            this.properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException ex) {
            Logger.getLogger(MessageProperty.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getProperty(String propertyName, Object ... arguments) {
        return MessageFormat.format(this.properties.getProperty(propertyName), arguments);
    }

}