package com.boomi.proserv.security.secretmanager.exporter;

import com.boomi.proserv.security.secretmanager.SecretKV;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

public class PropertyExporter implements SecretExporter {

    @Override
    public void export(List<SecretKV> list, String... args) throws Exception {
        String inputOutputfile = args[0];
        Properties properties = new Properties();
        properties.load(new FileInputStream(inputOutputfile));

        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i).getKey();
            if(key.startsWith("Connection|") || key.startsWith("ProcessProperty|")) {
                properties.put(key, list.get(i).getValue());
            }
        }

        properties.store(new FileOutputStream(inputOutputfile), null);
    }
}
