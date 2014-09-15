package com.oracle.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ReadProperties {
    Properties prop;
    
    public static void main (String[] args){
        
    }
    
    public Properties ReadProperties() throws IOException {
        return ReadProperties(null);
    }
    
    public Properties ReadProperties (String propertiesFileName) throws IOException {
        if (propertiesFileName==null){
            propertiesFileName="config.properties";
    }
        prop = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
        prop.load(inputStream);
        
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propertiesFileName + "' not found in the classpath");
        }
        
        return prop;
    }
        
}
