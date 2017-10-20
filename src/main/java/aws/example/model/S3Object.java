package aws.example.model;

public class S3Object {
    String bucketName;
    String fileName;

    public S3Object() {
    }

    public S3Object(String bucket_name, String file_name) {
        this.bucketName = bucket_name;
        this.fileName = file_name;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
