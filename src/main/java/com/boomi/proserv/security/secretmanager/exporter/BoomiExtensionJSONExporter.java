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
                /*
                    KV:     Connection.0947470a-1646-4f52-8dc0-c34bf87371c7.url=https://reqres.in/api/users
                    Path:   $.connections.connection[?(@.id=='0947470a-1646-4f52-8dc0-c34bf87371c7')].field[?(@.id=='url')].value
                 */
                String[] keyArray       = key.split("\\.");
                String componentId      = keyArray[1];
                String fieldType        = keyArray[2];
                String jsonPath         = "$.connections.connection[?(@.id=='" + componentId + "')].field[?(@.id=='" + fieldType + "')]";
                applyValue              (document, value, jsonPath, VALUE_FIELD, VALUE_FROM_FIELD);

            } else if(key.startsWith("ProcessProperty")) {
                /**
                 KV:   ProcessProperty.ed0e2ebd-72d5-4e34-82d1-4bb256e6dd22.5d0ea973-3737-4cec-9c81-646ca1aa2aae=true
                 Path: $.processProperties.ProcessProperty[?(@.id=='ed0e2ebd-72d5-4e34-82d1-4bb256e6dd22')].ProcessPropertyValue[?(@.key=='5d0ea973-3737-4cec-9c81-646ca1aa2aae')]
                 */
                String[] keyArray       = key.split("\\.");
                String componentId      = keyArray[1];
                String propertyKey      = keyArray[2];
                String jsonPath         = "$.processProperties.ProcessProperty[?(@.id=='" + componentId + "')].ProcessPropertyValue[?(@.key=='" + propertyKey + "')]";
                applyValue              (document, value, jsonPath, VALUE_FIELD, VALUE_FROM_FIELD);
            } else if(key.startsWith("CrossReferences")) {
                /**
                 KV:   CrossReferences.58f6b076-d66b-451d-856a-4de6127b5e1c.0.ref1=traceID_start
                 Path: $.crossReferences.crossReference[?(@.id=='58f6b076-d66b-451d-856a-4de6127b5e1c')].CrossReferenceRows.row[0]
                 */
                String[] keyArray       = key.split("\\.");
                String componentId      = keyArray[1];
                String crossIndex       = keyArray[2];
                String crossKey         = keyArray[3];
                String jsonPath         = "$.crossReferences.crossReference[?(@.id=='" + componentId + "')].CrossReferenceRows.row[" + crossIndex + "]";
                applyValue              (document, value, jsonPath, crossKey, crossKey);
            }
        }

        String jsonString = document.jsonString();
        FileOutputStream outputStream = new FileOutputStream(inputOutputfile);
        byte[] strToBytes = jsonString.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }

    private void applyValue(DocumentContext document, String value, String jsonPath, String valueField, String valueField2) {
        String jsonPathValue    = jsonPath + "." + valueField;

        try {
            Object pathObject = document.read(jsonPathValue);

            if (pathObject != null && (pathObject instanceof JSONArray && ((JSONArray) pathObject).size() >= 1)) {
                //Field found
                document.set(jsonPathValue, value);
            } else {
                jsonPathValue = jsonPath + "." + valueField2;
                pathObject = document.read(jsonPathValue);

                if (pathObject != null && (pathObject instanceof JSONArray && ((JSONArray) pathObject).size() >= 1)) {
                    //Field found
                    document.set(jsonPathValue, value);
                } else {
                    //Field not found
                    document.put(jsonPath, valueField, value);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
