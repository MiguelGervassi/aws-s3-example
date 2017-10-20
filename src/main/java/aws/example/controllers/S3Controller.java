package aws.example.controllers;

import aws.example.s3.*;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
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

    @RequestMapping(path = "/cse-kms-upload", method = RequestMethod.PUT)
    public String uploadUsingClientSideEncryption(@PathParam("bucketName") String bucketName, @PathParam("cmkId") String cmkId, @RequestPart("fileInput") MultipartFile fileInput) throws  Exception {
        System.out.println(bucketName);
        System.out.println(cmkId);
        System.out.println(fileInput.getOriginalFilename());
        KmsUploadCSE.main(new String[] { bucketName, fileInput.getOriginalFilename(), cmkId });
        return "success";
    }

    @RequestMapping(path = "/sse-kms-upload", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String uploadUsingServerSideEncryption(@PathParam("bucketName") String bucketName, @PathParam("cmkId") String cmkId, @RequestPart("fileInput") MultipartFile fileInput) throws  Exception {
        System.out.println(bucketName);
        System.out.println(cmkId);
        System.out.println(fileInput.getOriginalFilename());
        KmsUploadSSE.main(new String[] { bucketName, fileInput.getOriginalFilename(), cmkId });
        return "success";
    }

    @RequestMapping(path = "/bucket/{bucket_name}/object/{fileName}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getObject(@PathParam("bucket_name") String bucket_name, @PathParam("fileName") String fileName) throws Exception {
        GetObject.main(new String[] { bucket_name, fileName });
        return "success";
    }

    @RequestMapping(path = "/bucket/{bucket_name}/objects", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String listObjects(@PathParam("bucket_name") String bucket_name) throws Exception {
        ListObjects.main(new String[] { bucket_name });
        return "success";
    }

    @RequestMapping(path = "/deleteObject", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String deleteObject(@RequestBody String bucket_name, String fileName) throws Exception {
        DeleteObject.main(new String[]{ bucket_name, fileName });
        return "success";
    }
}