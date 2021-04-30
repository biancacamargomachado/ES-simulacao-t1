package br.com.pucrs.src;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class PropertiesLoader {

    public static Map<String, Object> loadProperties(String resourceFileName) {

        InputStream inputStream = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);

        Map<String, Object> data = new Yaml().load(inputStream);

        return data;
    }
}