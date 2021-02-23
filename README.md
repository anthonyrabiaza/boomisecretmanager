# Boomi Secret Manager

I wanted to share a solution I recently developed to simplify the Management of Credentials and Connection Properties when using Dell Boomi to access secured endpoints (Database, HTTP Client, etc.)

Boomi Secret Management includes Importer which can connect to:
- AWS Secrets Manager
- Azure Key Vault

In addition to the imported, several Exporter are available:
- Boomi JSON Environment Extension
- Standard Property files
- Postman Global files
- Display

Example of execution (in 1 line):
```shell
java
-Dimporter=com.boomi.proserv.security.secretmanager.importer.AzureSecretManager 
-DimporterParams="boomi-secrets Int-TestAtomCloud"
-Dexporter=com.boomi.proserv.security.secretmanager.exporter.BoomiExtensionJSONExporter
-DexporterParams="/home/ant/IdeaProjects/boomisecretmanager/componentsExtracted.json"
```

## Use for Boomi Extensions
### Example using Azure Key Vault
![Alt text](resources/boomisecretmanager-Boomi_JSON_Extensions.png?raw=true "boomisecretmanager")

The first step is the retrieval the current Environment configuration. In order to get this configuration, a call (GET) to the Environment extension API is required [AtomSphere](https://help.boomi.com/bundle/developer_apis/page/int-Environment_extensions_object.html).
For instance:
```bash
curl -u <username>:<password> https://api.boomi.com/api/rest/v1/<accountId>/EnvironmentExtensions/<environnmentId> > <pathToJSONFile>
```
The JSON file with the current configuration will be returned:
```json5
{
    "@type": "EnvironmentExtensions",
    "connections": {
        "@type": "Connections",
        "connection": [
            {
                "@type": "Connection",
                "field": [
                    {
                        "@type": "Field",
                        "id": "username",
                        "encryptedValueSet": false,
                        "usesEncryption": false,
                        "componentOverride": false,
                        "useDefault": true
                    },
                    {
                        "@type": "Field",
                        "id": "password",
                        "encryptedValueSet": true,
                        "usesEncryption": true,
                        "componentOverride": false,
                        "useDefault": false
                    },
                    {
                        "@type": "Field",
                        "id": "host",
                        "encryptedValueSet": false,
                        "usesEncryption": false,
                        "componentOverride": false,
                        "useDefault": true
                    },
                    (...)
                ],
                "id": "<connectionId>",
                "name": "MySQL"
            },
```
This file will be provided as an export parameter for Boomi Secret Manager: using -DexporterParams argument.

The import parameters will be: 
- The name of the keyvault (here boomi-secrets)
- The name of the secret (here Int-TestAtomCloud), please note that this name is also used as the name of the Environment in Boomi

For instance:
```shell
-DimporterParams="boomi-secrets Int-TestAtomCloud"
```

![Alt text](resources/azure-key-vaults.png?raw=true "boomisecretmanager")

The content of the Secret will be in JSON format. 

For BoomiJSONExport, it will read the element starting with "Connection.", with the convention "Connection.*connectionId*.*property*=*newValue*"
For instance:
```json5
{ 
  (...)
  "Connection.00000-ffffff-sdsdsds.url": "http://www.amock.io/api/tests", //For a HTTP Client
  "Connection.11111-aaaaaa-xyzxyzx.password": "Pa$$w0rd123!", //For a Database
  (...)
}
```

Once Boomi Secret Manager is executed, the JSON File will be updated. This file can now be send back to the AtomSphere using the POST method:
```bash
curl -u <username>:<password> -X POST --data-binary @<pathToJSONFile> https://api.boomi.com/api/rest/v1/<accountId>/EnvironmentExtensions/<environnmentId>
```

### Example using AWS Secrets Manager
The tool is working in the manner with AWS Secrets Manager, the key differences will be that the secrets stored are key/value (the JSON will be generated automatically).
![Alt text](resources/aws-secrets-manager.png?raw=true "boomisecretmanager")

The import parameters will be:
- The name of the AWS region 
- The name of the secret (here boomi/Int-TestAtomCloud/API) where Int-TestAtomCloud is the environment name

For instance:
```shell
-DimporterParams="ap-southeast-1 boomi/Int-TestAtomCloud/API"
```

## Use for Postman
For Postman Global file generation, no existing JSON file is required as Boomi Secret Manager will generated the JSON File from scratch:

![Alt text](resources/boomisecretmanager-Postman.png?raw=true "boomisecretmanager")

```shell
Java
-Dimporter=com.boomi.proserv.security.secretmanager.importer.AWSSecretManager
-DimporterParams="ap-southeast-1 boomi/PreProd-VMWare/API"
-Dexporter=com.boomi.proserv.security.secretmanager.exporter.PostmanGlobalExporter
-DexporterParams="My_Workspace.postman_globals.json"
com.boomi.proserv.security.secretmanager.BoomiSecretManager
```

Example of Postman request:

![Alt text](resources/postman-request.png?raw=true "boomisecretmanager")

And an example of Configuration in Azure Vault:
```json5
{ 
  (...)
  "cicd_api_baseUrl": "https://test.connect.boomi.com",
  "cicd_api_user": "boomi_xyz_HELLO_USER",
  "cicd_api_password": "abcd000-1234-789x-0000-122jd3232",
  (...)
}
```

## Dependencies and Connectivity

### Azure Key Vault
When using Azure Key Vault, **az** command needs to be available. Also the login process (i.e. az login) is required before calling the Java class. See [Azure docs](https://docs.microsoft.com/en-us/cli/azure/authenticate-azure-cli) for more details.

### AWS Secret Manager
The two variables AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY can be defined as environment variables or aws.accessKeyId and aws.secretKe Java Parameters can be provied to the Java client. See [AWS docs](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html)  for more details.