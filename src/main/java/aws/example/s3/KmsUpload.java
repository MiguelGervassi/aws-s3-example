package aws.example.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.security.Provider;

public class KmsUpload {

    public static void upload(String bucketName, MultipartFile fileInput, String kms_cmk_id, String encryptionRadioOption)
    {
        final String USAGE = "\n" +
                "To run this example, supply the name of an S3 bucket and a file to\n" +
                "upload to it.\n" +
                "\n" +
                "Ex: PutObject <bucketname> <filename> <kmskey>\n";
        String file_path = fileInput.getOriginalFilename();
        String key_name = Paths.get(file_path).getFileName().toString();
        AmazonS3 s3;
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(fileInput.getContentType());
            objectMetadata.setContentLength(fileInput.getSize());
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName, key_name, fileInput.getInputStream(), objectMetadata);
            putRequest.setMetadata(objectMetadata);
            if(encryptionRadioOption.equalsIgnoreCase("SSE-KMS")) {
                s3 = AmazonS3ClientBuilder.defaultClient();
                putRequest.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(kms_cmk_id));
            } else {
                // JCE or Third Party Bouncy Castle
                Provider bcProvider = new BouncyCastleProvider();
                KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
                CryptoConfiguration cryptoConfiguration = new CryptoConfiguration().withCryptoProvider(bcProvider).withAwsKmsRegion(Region.getRegion(Regions.US_EAST_1));
                s3 = new AmazonS3EncryptionClientBuilder().withEncryptionMaterials(materialProvider).withCryptoConfiguration(cryptoConfiguration).withKmsClient(AWSKMSClientBuilder.defaultClient()).build();
            }
            System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucketName);
            // Request encryption client based on selection
            PutObjectResult response = s3.putObject(putRequest);
            System.out.println("Uploaded object encryption status is " +
                    response.getSSEAlgorithm());
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
        } catch (Exception e) {
            System.out.println("Everything else: " + e);
        }
        System.out.println("Done!");
    }
}
