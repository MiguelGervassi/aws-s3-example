package aws.example.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

public class KmsUploadSSE {

    private static AmazonS3Client encryptionClient;

    public static void main(Object[] args)
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
        String bucketName = args[0].toString();
        MultipartFile fileInput = (MultipartFile) args[1];
        String file_path = fileInput.getOriginalFilename();
        String kms_cmk_id = args[2].toString();
        String key_name = Paths.get(file_path).getFileName().toString();
        System.out.format("Uploading %s to S3 bucket %s...\n", file_path, bucketName);
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            // Request server-side encryption.
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(fileInput.getContentType());
            objectMetadata.setContentLength(fileInput.getSize());
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName, key_name, fileInput.getInputStream(), objectMetadata);
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
        } catch (Exception e) {
            System.out.println("Everything else: " + e);
        }
        System.out.println("Done!");
    }
}
