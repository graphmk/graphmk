// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.graphmk;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class Configuration
{
    public Properties getPropValues() throws IOException {
        final Properties prop = new Properties();
        final String propFileName = "config.properties";
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
            return prop;
        }
        throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
    }
}
