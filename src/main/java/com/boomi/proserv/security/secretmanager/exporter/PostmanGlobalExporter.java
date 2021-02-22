package com.boomi.proserv.security.secretmanager.exporter;

import com.boomi.proserv.security.secretmanager.SecretKV;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostmanGlobalExporter implements SecretExporter {
    @Override
    public void export(List<SecretKV> list, String... args) throws Exception {
        String outputfile = args[0];
        ObjectMapper objectMapper = new ObjectMapper();
        PostmanGlobal postmanGlobal = new PostmanGlobal();
        List<Value> values = new ArrayList<Value>();

        postmanGlobal.id        = UUID.randomUUID().toString();
        postmanGlobal.name      = "My Workspace Globals";
        postmanGlobal.values    = values;

        for (int i = 0; i < list.size(); i++) {
            Value value = new Value();
            value.key       = list.get(i).getKey();
            value.value     = list.get(i).getValue();
            value.enabled   = true;
            values.add(value);
        }

        postmanGlobal._postman_variable_scope   = "globals";
        postmanGlobal._postman_exported_at      = "2021-01-21T10:57:06.233Z";
        postmanGlobal._postman_exported_using   = "Postman/7.36.1";
        objectMapper.writeValue(new File(outputfile), postmanGlobal);
    }

    public class PostmanGlobal {
        public String id;
        public List<Value> values;
        public String name;
        public String _postman_variable_scope;
        public String _postman_exported_at;
        public String _postman_exported_using;
    }

    public class Value {
        public String key;
        public String value;
        public boolean enabled;
    }
}
