package com.boomi.proserv.security.secretmanager;

import com.boomi.proserv.security.secretmanager.exporter.SecretExporter;
import com.boomi.proserv.security.secretmanager.importer.SecretImporter;

import java.util.List;

/**
 * Usage:
 *   java -Dimporter=<importerClass> -DimporterParams='<params>' -Dexporter=<exporterClass> -DexporterParams='<params>' com.boomi.proserv.security.secretmanager.BoomiSecretManager
 * Example for AWS:
 *   java -Dimporter=com.boomi.proserv.security.secretmanager.importer.AWSSecretManager -DimporterParams='ap-southeast-1 boomi/Initial-TestAtomCloud/API' -Dexporter=com.boomi.proserv.security.aws.secretmanager.exporter.DisplayExporter -DexporterParams='boomi/Initial-TestAtomCloud/API ap-southeast-1' com.boomi.proserv.security.secretmanager.BoomiSecretManager
 */
public class BoomiSecretManager {
    public static void main(String[] args) {

        String importerClass    = "com.boomi.proserv.security.secretmanager.importer.AWSSecretManager";
        String exporterClass    = "com.boomi.proserv.security.secretmanager.exporter.DisplayExporter";

        if(System.getProperty("importer")!=null) {
            importerClass = System.getProperty("importer");
        }
        if(System.getProperty("exporter")!=null){
            exporterClass = System.getProperty("exporter");
        }

        try {
            Class<?> importerClassz = Class.forName(importerClass);
            SecretImporter importer = (SecretImporter) importerClassz.getDeclaredConstructor().newInstance();

            System.out.println("Importer: " + importer.getClass().getName());
            String[] importerParams = stringParamsToArray(System.getProperty("importerParams"), "ap-southeast-1 boomi/Int-TestAtomCloud/API");
            System.out.println("Importer Parameters: " + arrayToString(importerParams));

            List<SecretKV> secrets = importer.getSecret(importerParams);

            if(secrets!=null) {
                Class<?> exporterClassz = Class.forName(exporterClass);
                SecretExporter exporter = (SecretExporter) exporterClassz.getDeclaredConstructor().newInstance();

                System.out.println("Exporter: " + exporter.getClass().getName());
                String[] exporterParams = stringParamsToArray(System.getProperty("exporterParams"), "");
                System.out.println("Exporter Parameters: " + arrayToString(exporterParams));
                exporter.export(secrets, exporterParams);
            } else {
                System.err.println("No secrets returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] stringParamsToArray(String params, String defaultValues){
        if(params!=null) {
            return params.split(" ");
        } else if(defaultValues!=null){
            return defaultValues.split(" ");
        } else {
            return null;
        }
    }

    private static String arrayToString(String[] array) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<array.length; i++) {
            buffer.append(array[i]);
            if(i<array.length-1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
}
