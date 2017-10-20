package aws.example.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;

public class KmsUploadSSE {

    private static AmazonS3Client encryptionClient;

    public static void main(String[] args)
    {
        final String USAGE = "\n" +
                "To run this example, supply the name of an S3 bucket and a file to\n" +
                "upload to it.\n" +
                "\n" +
                "Ex: PutObject <bucketname> <filename> <kmskey>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Please do not use this hack in Production
        // Make sure you have the JCE unlimited strength policy files installed and configured for your JVM correctly
//        try {
//            Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
//            field.setAccessible(true);
//
//            Field modifiersField = Field.class.getDeclaredField("modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//
//            field.set(null, false);
//        } catch(Exception e) {
//            System.out.println(e);
//        }
//        // end of hack

        String bucketName = args[0];
        String file_path = args[1];
        String kms_cmk_id = args[2];
        String key_name = Paths.get(file_path).getFileName().toString();

//        KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
//        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration().withAwsKmsRegion(Region.getRegion(Regions.US_EAST_1));
//        encryptionClient = new AmazonS3EncryptionClientBuilder().standard().withEncryptionMaterials(materialProvider).withCryptoConfiguration(cryptoConfiguration).build();
//        encryptionClient.setCryptoConfiguration();
//        new ProfileCredentialsProvider(), materialProvider,
//                new CryptoConfiguration().withKmsRegion(Regions.US_EAST_1))
//                .withRegion(Region.getRegion(Regions.US_EAST_1));

        // Upload object using the encryption client.
//        byte[] plaintext = "Hello World, S3 Client-side Encryption Using Asymmetric Master Key!"
//                .getBytes();
//        System.out.println("plaintext's length: " + plaintext.length);
//        encryptionClient.putObject(new PutObjectRequest(bucketName, key_name,
//                new ByteArrayInputStream(plaintext), new ObjectMetadata()));

        System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucketName);
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            File file = new File(file_path);
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName, key_name, file);
//            encryptionClient.putObject(new PutObjectRequest(bucketName, key_name, file));
//            AmazonS3Client s3 = new AmazonS3Client(new ProfileCredentialsProvider())
//                    .withRegion(Region.getRegion(Regions.US_EAST_1));
//            PutObjectRequest req = new PutObjectRequest(bucketName, key_name,
//                    file).withSSEAwsKeyManagementParams(kms_cmk_id.toString());
//            PutObjectResult putResult = s3.putObject(req);

            // Request server-side encryption.
            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            putRequest.setMetadata(objectMetadata);
            putRequest.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(kms_cmk_id));
            PutObjectResult response = s3.putObject(putRequest);
            System.out.println("Uploaded object encryption status is " +
                    response.getSSECustomerAlgorithm());
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        System.out.println("Done!");
    }
}
