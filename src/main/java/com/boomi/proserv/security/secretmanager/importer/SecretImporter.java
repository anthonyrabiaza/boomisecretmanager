package com.boomi.proserv.security.secretmanager.importer;

import com.boomi.proserv.security.secretmanager.SecretKV;

import java.util.List;

public interface SecretImporter {
    public List<SecretKV> getSecret(String ... params) throws Exception;
}
