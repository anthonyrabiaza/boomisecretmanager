package com.boomi.proserv.security.secretmanager.exporter;

import com.boomi.proserv.security.secretmanager.SecretKV;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class BoomiExtensionJSONExporter implements SecretExporter {

    private static String VALUE_FIELD       = "value";
    private static String VALUE_FROM_FIELD  = "valueFrom";
    @Override
    public void export(List<SecretKV> list, String... args) throws Exception {
        String inputOutputfile = "";
        for(int i=0; i<args.length; i++) {
            if(i!=0) {
                inputOutputfile += " ";
            }
            inputOutputfile += args[i];
        }
        DocumentContext document = JsonPath.parse(new FileInputStream(inputOutputfile));

        for (int i = 0; i < list.size(); i++) {
            String key      = list.get(i).getKey();
            String value    = list.get(i).getValue();
            if(key.startsWith("Connection.")) {
                String[] keyArray       = key.split("\\.");
                String componentId      = keyArray[1];
                String fieldType        = keyArray[2];
                /*
                    KV:     Connection.0947470a-1646-4f52-8dc0-c34bf87371c7.url=https://reqres.in/api/users
                    Path:   $.connections.connection[?(@.id=='0947470a-1646-4f52-8dc0-c34bf87371c7')].field[?(@.id=='url')].value
                 */
                String jsonPath         = "$.connections.connection[?(@.id=='" + componentId + "')].field[?(@.id=='" + fieldType + "')]";
                String jsonPathValue    = jsonPath + "." + VALUE_FIELD;
                Object pathObject       = document.read(jsonPathValue);

                if(pathObject!=null && (pathObject instanceof JSONArray && ((JSONArray)pathObject).size()>=1) ){
                    //Field found
                    document.set(jsonPathValue, value);
                } else {
                    jsonPathValue    = jsonPath + "." + VALUE_FROM_FIELD;
                    pathObject       = document.read(jsonPathValue);

                    if(pathObject!=null && (pathObject instanceof JSONArray && ((JSONArray)pathObject).size()>=1) ){
                        //Field found
                        document.set(jsonPathValue, value);
                    } else {
                        //Field not found
                        document.put(jsonPath, VALUE_FIELD, value);
                    }
                }
            } else if(key.startsWith("ProcessProperty")) {
                //TODO
            }
        }

        String jsonString = document.jsonString();
        FileOutputStream outputStream = new FileOutputStream(inputOutputfile);
        byte[] strToBytes = jsonString.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }
}
