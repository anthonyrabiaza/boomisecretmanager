package com.boomi.proserv.security.secretmanager.exporter;

import com.boomi.proserv.security.secretmanager.SecretKV;

import java.util.List;

public interface SecretExporter {
    public void export(List<SecretKV> list, String ... args) throws Exception;
}
