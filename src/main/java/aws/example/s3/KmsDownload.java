package aws.example.s3;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.net.URLEncoder;
import java.security.Provider;

public class KmsDownload {
    public static ResponseEntity<byte[]> download(String bucketName_dl, String objectName_dl,
           String kms_cmk_id_dl, boolean encryptCheckBox) throws Exception {
        byte[] bytes;
        System.out.println("KMS CMK value: " + kms_cmk_id_dl);
        if(encryptCheckBox && !StringUtils.isNullOrEmpty(kms_cmk_id_dl)) {
            bytes = getClientSideEncryptedObject(bucketName_dl, objectName_dl, kms_cmk_id_dl);
        } else {
            bytes = getServerSideEncryptedObject(bucketName_dl, objectName_dl);
        }
        String fileName = URLEncoder.encode(objectName_dl, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private static byte[] getServerSideEncryptedObject(String bucketName_dl, String objectName_dl) throws Exception {
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        S3Object o = s3.getObject(bucketName_dl, objectName_dl);
        S3ObjectInputStream inputStream = o.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    private static byte[] getClientSideEncryptedObject(String bucketName_dl, String objectName_dl, String kms_cmk_id) throws Exception {
        Provider bcProvider = new BouncyCastleProvider();
        KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration().withCryptoProvider(bcProvider).withAwsKmsRegion(Region.getRegion(Regions.US_EAST_1));
        AmazonS3Encryption encryptionClient = new AmazonS3EncryptionClientBuilder().withEncryptionMaterials(materialProvider).withCryptoConfiguration(cryptoConfiguration).withKmsClient(AWSKMSClientBuilder.defaultClient()).build();
        S3Object o = encryptionClient.getObject(bucketName_dl, objectName_dl);
        S3ObjectInputStream inputStream = o.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }
}
