package aws.example.controllers;

import aws.example.s3.*;
import com.amazonaws.services.s3.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {
    @RequestMapping(path = "/buckets", method = RequestMethod.GET)
    public static List<Bucket> listBuckets() throws Exception {
        return ListBuckets.listBuckets();
    }

    @RequestMapping(path = "/objects", method = RequestMethod.GET)
    public List<S3ObjectSummary> listObjects(@PathParam("bucketName") String bucketName) throws Exception {
        return ListObjects.listObjects(new String[] { bucketName });
    }

    @RequestMapping(path = "/upload", method = RequestMethod.PUT)
    public void upload(@PathParam("bucketName") String bucketName, @PathParam("kms_cmk_id") String kms_cmk_id,
           @PathParam("encryptionRadioOption") String encryptionRadioOption, @RequestPart("fileInput") MultipartFile fileInput) throws Exception {
        KmsUpload.upload(bucketName, fileInput, kms_cmk_id, encryptionRadioOption);
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathParam("bucketName_dl") String bucketName_dl, @PathParam("objectName_dl")
            String objectName_dl, @PathParam("kms_cmk_id_dl") String kms_cmk_id_dl, @PathParam("encryptCheckbox") boolean encryptCheckBox) throws Exception {
        return KmsDownload.download(bucketName_dl, objectName_dl, kms_cmk_id_dl, encryptCheckBox);
    }
}