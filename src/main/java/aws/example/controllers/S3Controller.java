package aws.example.controllers;

import aws.example.s3.*;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.util.List;

//    @Path("/users/{userid}")
//    public Response getUsr(@PathParam("userid") String userId) {
@RestController
@RequestMapping("/s3")
public class S3Controller {
    @RequestMapping(path = "/createBucket", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String createBucket(@RequestBody String bucket_name) throws Exception {
        CreateBucket.createBucket(bucket_name);
        return "success";
    }

    @RequestMapping(path = "/bucket/{bucket_name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Bucket getBucket(@PathParam("bucket_name") String bucket_name) throws Exception {
        return CreateBucket.getBucket(bucket_name);
    }

    @RequestMapping(path = "/buckets", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public static List<Bucket> listBuckets() throws Exception {
        return ListBuckets.listBuckets();
    }

    @RequestMapping(path = "/deleteBucket", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String deleteBucket(@RequestBody String bucket_name) throws Exception {
        DeleteBucket.main(new String[]{ bucket_name });
        return "success";
    }

    //    @RequestMapping(path = "/upload", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
//    public String upload(@RequestBody S3Object s3Object) throws Exception {


//    @RequestMapping(path = "/upload", method = RequestMethod.PUT)
//    public String upload(@PathParam("bucketName") String bucketName, @PathParam("cmkId") String cmkId, @RequestPart("fileInput") MultipartFile fileInput) throws  Exception {
//        System.out.println(bucketName);
//        System.out.println(cmkId);
//        System.out.println(fileInput);
//        KmsUploadSSE.main(new Object[] { bucketName, fileInput, cmkId });
//        return "success";
//    }

    @RequestMapping(path = "/cse-kms-upload", method = RequestMethod.PUT)
    public String uploadUsingClientSideEncryption(@PathParam("bucketName") String bucketName, @PathParam("kms_cmk_id") String kms_cmk_id, @RequestPart("fileInput") MultipartFile fileInput) throws  Exception {
        System.out.println(bucketName);
        System.out.println(kms_cmk_id);
        System.out.println(fileInput.getOriginalFilename());
        KmsUploadCSE.main(new Object[] { bucketName, fileInput, kms_cmk_id });
        return "success";
    }

    @RequestMapping(path = "/sse-kms-upload", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String uploadUsingServerSideEncryption(@PathParam("bucketName") String bucketName, @PathParam("kms_cmk_id") String kms_cmk_id, @RequestPart("fileInput") MultipartFile fileInput) throws  Exception {
        System.out.println(bucketName);
        System.out.println(kms_cmk_id);
        System.out.println(fileInput.getOriginalFilename());
        KmsUploadSSE.main(new Object[] { bucketName, fileInput, kms_cmk_id });
        return "success";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathParam("bucketName_dl") String bucketName_dl, @PathParam("objectName_dl")
            String objectName_dl, @PathParam("kms_cmk_id_dl") String kms_cmk_id_dl, @PathParam("encryptCheckbox") boolean encryptCheckBox) throws Exception {
        byte[] bytes;
        if(encryptCheckBox) {
            bytes = getClientSideEncryptedObject(bucketName_dl, objectName_dl, kms_cmk_id_dl);
        } else {
            bytes = getServerSideEncryptedObject(bucketName_dl, objectName_dl);
        }
        String fileName = URLEncoder.encode(objectName_dl, "UTF-8").replaceAll("\\+", "%20");
        System.out.println("Bucket Name: " + bucketName_dl);
        System.out.println("Object Name: " + objectName_dl);
        System.out.println("Key ID: " + kms_cmk_id_dl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public byte[] getServerSideEncryptedObject(String bucketName_dl, String objectName_dl) throws Exception {
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        S3Object o = s3.getObject(bucketName_dl, objectName_dl);
        S3ObjectInputStream inputStream = o.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    public byte[] getClientSideEncryptedObject(String bucketName_dl, String objectName_dl, String kms_cmk_id) throws Exception {
        Provider bcProvider = new BouncyCastleProvider();
        KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration().withCryptoProvider(bcProvider).withAwsKmsRegion(Region.getRegion(Regions.US_EAST_1));
        AmazonS3Encryption encryptionClient = new AmazonS3EncryptionClientBuilder().withEncryptionMaterials(materialProvider).withCryptoConfiguration(cryptoConfiguration).withKmsClient(AWSKMSClientBuilder.defaultClient()).build();
        S3Object o = encryptionClient.getObject(bucketName_dl, objectName_dl);
        S3ObjectInputStream inputStream = o.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    @RequestMapping(path = "/objects", method = RequestMethod.GET)
    public List<S3ObjectSummary> listObjects(@PathParam("bucketName") String bucketName) throws Exception {
        System.out.println("Bucket Name: " + bucketName);
        return ListObjects.listObjects(new String[] { bucketName });
    }

    @RequestMapping(path = "/deleteObject", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String deleteObject(@RequestBody String bucket_name, String fileName) throws Exception {
        DeleteObject.main(new String[]{ bucket_name, fileName });
        return "success";
    }
}