package com.boomi.proserv.security.secretmanager.exporter;

import com.boomi.proserv.security.secretmanager.SecretKV;

import java.util.List;

public class DisplayExporter implements SecretExporter {
    @Override
    public void export(List<SecretKV> list, String... args) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getKey()+"="+list.get(i).getValue());
        }
    }
}
