package com.boomi.proserv.security.secretmanager.importer;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.DeletedSecret;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.boomi.proserv.security.secretmanager.SecretKV;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AzureSecretManager implements SecretImporter {

    public List<SecretKV> getSecret(String ... params) throws Exception {
        String keyVaultName     = params[0];
        String secretName       = params[1];
        String secretValueFile  = null;
        if(params.length>2) {
            secretValueFile = params[2];
        }

        List<SecretKV> list = null;

        System.out.println("Getting/Setting secret " + secretName + " from " + keyVaultName + "...");

        // Create a Secrets Manager client
        String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUri).credential(new DefaultAzureCredentialBuilder().build()).buildClient();

        if(secretValueFile!=null && System.getProperty("update").equals("true")) {
            //Saving secret to Azure Key Vault, the secret is a JSON file (2nd parameter)
            System.out.println("Saving secret ...");
            String secretValue = new String(Files.readAllBytes(Paths.get(secretValueFile)));
            secretClient.setSecret(new KeyVaultSecret(secretName, secretValue));
            System.out.println("Secret saved");
            return null;
        } else {
            //Retrieve secret from Azure Key Vault, the secret (JSON file) will be parsed
            System.out.println("Retrieving secret ...");
            KeyVaultSecret retrievedSecret = secretClient.getSecret(secretName);
            String secret = retrievedSecret.getValue();
            System.out.println("Secret is " + secret.substring(0, secret.length()>16?16:secret.length()) + "... (truncated)");
            return SecretKV.getList(secret);
        }
    }
}
