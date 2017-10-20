package aws.example.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.internal.crypto.JceEncryptionConstants;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Map;

public class KmsUploadCSE {

    private static AmazonS3EncryptionClientBuilder encryptionClient;
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

        // Hack for JCE Unlimited Strength, Developer Use Only
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
        // end of hack

        String bucketName = args[0];
        String file_path = args[1];
        String kms_cmk_id = args[2];
        String key_name = Paths.get(file_path).getFileName().toString();

        Provider bcProvider = new BouncyCastleProvider();
        EncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
        CryptoConfiguration cryptoConfiguration = new CryptoConfiguration().withCryptoProvider(bcProvider).withAwsKmsRegion(Region.getRegion(Regions.US_EAST_1));
        encryptionClient = new AmazonS3EncryptionClientBuilder().withEncryptionMaterials(materialProvider).withCryptoConfiguration(cryptoConfiguration).withKmsClient(AWSKMSClientBuilder.defaultClient());
//        AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(new ProfileCredentialsProvider(), materialProvider,
//                new CryptoConfiguration().withKmsRegion(Regions.US_EAST_1))
//                .withRegion(Region.getRegion(Regions.US_EAST_1));
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
//        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            File file = new File(file_path);
            byte[] plaintext = "Hello World, S3 Client-side Encryption Using Asymmetric Master Key!"
                    .getBytes();

//            String fileContentString = OBJECT_MAPPER.writeValueAsString(file);
//            byte[] fileContentBytes = fileContentString.getBytes(StandardCharsets.UTF_8);
//            InputStream fileInputStream = new ByteArrayInputStream(fileContentBytes);
            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType(CONTENT_TYPE);
//            metadata.setContentLength(fileContentBytes.length);
//            PutObjectRequest putRequest = new PutObjectRequest(
//                    bucketName, key_name, file);
            PutObjectRequest putRequest = new PutObjectRequest(
                    bucketName, key_name, file);
            encryptRequestUsingMetadata(putRequest, materialProvider.getEncryptionMaterials(), cryptoConfiguration.getCryptoProvider());
//            if(putRequest.getFile() != null) {
//                Mimetypes mimetypes = Mimetypes.getInstance();
//                metadata.setContentType(mimetypes.getMimetype(putRequest.getFile()));
//            }
//            putRequest.setMetadata(metadata);

//            InputStream inputStream = file.getInputstream();
//            encryptionClient.build().putObject(putRequest);
            encryptionClient.build().putObject(putRequest);
            // Request server-side encryption.
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
//            putRequest.setMetadata(objectMetadata);
//            PutObjectResult response = s3.putObject(putRequest);
//            System.out.println("Uploaded object encryption status is " +
//                    response.getSSEAlgorithm());
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

    public static PutObjectRequest encryptRequestUsingMetadata(PutObjectRequest request, EncryptionMaterials materials, Provider cryptoProvider) {
        // Generate a one-time use symmetric key and initialize a cipher to encrypt object data
        SecretKey envelopeSymmetricKey = generateOneTimeUseSymmetricKey();
        Cipher symmetricCipher = createSymmetricCipher(envelopeSymmetricKey, Cipher.ENCRYPT_MODE, cryptoProvider, null);

        // Encrypt the envelope symmetric key
        byte[] encryptedEnvelopeSymmetricKey = getEncryptedSymmetricKey(envelopeSymmetricKey, materials, cryptoProvider);

        // Store encryption info in metadata
        ObjectMetadata metadata = updateMetadataWithEncryptionInfo(request, encryptedEnvelopeSymmetricKey, symmetricCipher, materials.getMaterialsDescription());

        // Update the request's metadata to the updated metadata
        request.setMetadata(metadata);

        // Create encrypted input stream
        InputStream encryptedInputStream = getEncryptedInputStream(request, symmetricCipher);
        request.setInputStream(encryptedInputStream);

        // Treat all encryption requests as input stream upload requests, not as file upload requests.
        request.setFile(null);

        return request;
    }

    /**
     * Generates a one-time use Symmetric Key on-the-fly for use in envelope encryption.
     */
    private static SecretKey generateOneTimeUseSymmetricKey() {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance(JceEncryptionConstants.SYMMETRIC_KEY_ALGORITHM);
            generator.init(JceEncryptionConstants.SYMMETRIC_KEY_LENGTH, new SecureRandom());
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new AmazonClientException("Unable to generate envelope symmetric key:" + e.getMessage(), e);
        }
    }

    /**
     * Creates a symmetric cipher in the specified mode from the given symmetric key and IV.  The given
     * crypto provider will provide the encryption implementation.  If the crypto provider is null, then
     * the default JCE crypto provider will be used.
     */
    private static Cipher createSymmetricCipher(SecretKey symmetricCryptoKey, int encryptMode, Provider cryptoProvider, byte[] initVector) {
        try {
            Cipher cipher;
            if(cryptoProvider != null) {
                cipher = Cipher.getInstance(JceEncryptionConstants.SYMMETRIC_CIPHER_METHOD, cryptoProvider);
            } else {
                cipher = Cipher.getInstance(JceEncryptionConstants.SYMMETRIC_CIPHER_METHOD);
            }
            if(initVector != null) {
                cipher.init(encryptMode, symmetricCryptoKey, new IvParameterSpec(initVector));
            } else {
                cipher.init(encryptMode, symmetricCryptoKey);
            }
            return cipher;
        } catch (Exception e) {
            throw new AmazonClientException("Unable to build cipher with the provided algorithm and padding: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts a symmetric key using the provided encryption materials and returns
     * it in raw byte array form.
     */
//    private static byte[] getEncryptedSymmetricKey(SecretKey toBeEncrypted, EncryptionMaterials materials, Provider cryptoProvider) {
//        Key keyToDoEncryption;
////        if(materials.getKeyPair() != null) {
////            // Do envelope encryption with public key from key pair
////            keyToDoEncryption = materials.getKeyPair().getPublic();
////        } else {
//            // Do envelope encryption with symmetric key
//        keyToDoEncryption= materials.getSymmetricKey();
////        }
//        try {
//            Cipher cipher;
//            byte[] toBeEncryptedBytes = toBeEncrypted.getEncoded();
//            if(cryptoProvider != null) {
//                cipher = Cipher.getInstance(keyToDoEncryption.getAlgorithm(), cryptoProvider);
//            } else {
//                cipher = Cipher.getInstance(keyToDoEncryption.getAlgorithm()); // Use default JCE Provider
//            }
//            cipher.init(Cipher.ENCRYPT_MODE, keyToDoEncryption);
//            return cipher.doFinal(toBeEncryptedBytes);
//        } catch (Exception e) {
//            throw new AmazonClientException("Unable to encrypt symmetric key: " + e.getMessage(), e);
//        }
//    }

    /**
     * Updates the metadata to contain the encrypted symmetric key, IV, and calculated content length of the encrypted data.
     */
    private static ObjectMetadata updateMetadataWithEncryptionInfo(PutObjectRequest request, byte[] keyBytesToStoreInMetadata,
                                                                   Cipher symmetricCipher, Map<String, String> materialsDescription) {
        ObjectMetadata metadata = request.getMetadata();
        if(metadata == null) {
            metadata = new ObjectMetadata();
        }

        if(request.getFile() != null) {
            Mimetypes mimetypes = Mimetypes.getInstance();
            metadata.setContentType(mimetypes.getMimetype(request.getFile()));
        }

        // If we generated a symmetric key to encrypt the data, store it in the object metadata.
        if(keyBytesToStoreInMetadata != null) {
            keyBytesToStoreInMetadata = Base64.encodeBase64(keyBytesToStoreInMetadata);
            metadata.addUserMetadata(Headers.CRYPTO_KEY, new String(keyBytesToStoreInMetadata));
        }

        // Put the cipher initialization vector (IV) into the object metadata
        byte[] initVectorBytes = symmetricCipher.getIV();
        initVectorBytes = Base64.encodeBase64(initVectorBytes);
        metadata.addUserMetadata(Headers.CRYPTO_IV, new String(initVectorBytes));

        // Put the materials description into the object metadata as JSON
        JSONObject descriptionJSON = new JSONObject(materialsDescription);
        metadata.addUserMetadata(Headers.MATERIALS_DESCRIPTION, descriptionJSON.toString());

        // Put the calculated length of the encrypted contents in the metadata
        long cryptoContentLength = calculateCryptoContentLength(symmetricCipher, request, metadata);
        if(cryptoContentLength > 0) {
            metadata.setContentLength(cryptoContentLength);
        }

        return metadata;
    }

    /**
     * Calculates the length of the encrypted file given the original plaintext
     * file length and the cipher that will be used for encryption.
     *
     * @return
     *      The size of the encrypted file in bytes, or 0 if no content length
     *      has been set yet.
     */
    private static long calculateCryptoContentLength(Cipher symmetricCipher, PutObjectRequest request, ObjectMetadata metadata) {
        long plaintextLength;
        if(request.getFile() != null) {
            plaintextLength = request.getFile().length();
        } else if(request.getInputStream() != null && metadata.getContentLength() > 0) {
            plaintextLength = metadata.getContentLength();
        } else {
            return 0;
        }
        long cipherBlockSize = symmetricCipher.getBlockSize();
        long offset = cipherBlockSize - (plaintextLength % cipherBlockSize);
        return plaintextLength + offset;
    }

    /**
     * Returns an input stream encrypted with the given symmetric cipher.
     */
    private static InputStream getEncryptedInputStream(PutObjectRequest request, Cipher symmetricCipher) {
        try {
            InputStream originalInputStream = request.getInputStream();
            if(request.getFile() != null) {
                originalInputStream = new FileInputStream(request.getFile());
            }
            return new CipherInputStream(originalInputStream, symmetricCipher);
        } catch(Exception e) {
            throw new AmazonClientException("Unable to create cipher input stream: " + e.getMessage(), e);
        }
    }
}
