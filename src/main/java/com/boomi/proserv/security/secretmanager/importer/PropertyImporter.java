package com.boomi.proserv.security.secretmanager.importer;

import com.boomi.proserv.security.secretmanager.SecretKV;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyImporter implements SecretImporter{
    @Override
    public List<SecretKV> getSecret(String... params) throws Exception {
        String inputOutputfile = params[0];
        Properties properties = new Properties();
        properties.load(new FileInputStream(inputOutputfile));

        List<SecretKV> list = new ArrayList<SecretKV>();
        for (Map.Entry entry : properties.entrySet()) {
            list.add(new SecretKV(entry.getKey().toString(), entry.getValue().toString()));
        }
        return list;
    }
}
