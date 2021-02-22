package com.boomi.proserv.security.secretmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecretKV {
    String key;
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SecretKV(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static List<SecretKV> getList(String secret) throws Exception {
        List<SecretKV> list = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
            Map<String, String> map = objectMapper.readValue(secret, typeRef);
            list = map.entrySet().stream().map(entry -> new SecretKV(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            return list;
        } catch (Exception e) {
            throw e;
        }
    }
}
