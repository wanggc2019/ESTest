package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 编写elasticsearch的配置文件加载类
 */
public class XmlProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlProperties.class);

    private XmlProperties() {
    }

    public static Map<String, String> loadFromXml(String xmlPropertiesPath) {
        try {
            Object in = XmlProperties.class.getClassLoader().getResourceAsStream(xmlPropertiesPath);
            if(in != null) {
                LOGGER.info("Found the xml properties [{}] in class path,use it", xmlPropertiesPath);
                Map<String, String> e1 = loadFromXml((InputStream)in);
                return e1;
            }
            Map<String, String> resMap = null;

            File e = new File(xmlPropertiesPath);
            if(!e.isFile()) {
                return resMap;
            }

            LOGGER.info("Found the xml properties [{}] in file path,use it", xmlPropertiesPath);
            in = new FileInputStream(new File(xmlPropertiesPath));
            resMap = loadFromXml((InputStream)in);
            ((InputStream) in).close();
            return resMap;
        } catch (Exception var7) {
            LOGGER.error("Load xml properties [" + xmlPropertiesPath + "] error.", var7);
        }
        return null;
    }

    public static Map<String, String> loadFromXml(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.loadFromXML(in);
        Map<String, String> map = new HashMap<String, String>();
        Set entries = properties.entrySet();
        Iterator iter = entries.iterator();

        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            map.put((String)entry.getKey(), (String)entry.getValue());
        }
        return map;
    }

}
