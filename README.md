# Working Example for Encrypted S3 Upload and Download using KMS (AWS Managed CMK)

## Getting the Application Up & Running

### Building Locally

#### Pre-requisites
- Git
- Java 8
- Maven

#### Building war artifact
    git clone https://github.com/MiguelGervassi/aws-s3-example.git
    cd aws-s3-example
    mvn clean package –DskipTests=true

### Deploying to AWS
  1. Upload .war artifact created above into S3 bucket
  2. Create EC2 using Community AMI: aws-elasticbeanstalk-amzn-2017.03.0.x86_64-tomcat8java8-hvm-201705151908 
  3. Configure your EC2 Instance Details to use IAM role which has access to both S3 and KMS services.
  4. In Advanced Details, include following script for user data
        ```
        #!/bin/bash
        aws s3api get-object --bucket <bucket_name_you_uploaded_war_file_to> --key aws-s3-example-0.0.1-SNAPSHOT.war aws-s3-example.war
        nohup java -jar aws-s3-example.war &	
        ```
  5. Create / Configure Security Group with Custom TCP Rule to allow incoming connections on port 8080
  6. Launch your instance
  7. Wait a few moments after application status is "Running". Copy the Public IP / DNS of your EC2 instance and paste it in your address bar.
  Make sure to specify port 8080 in as part of EC2 instance URL: <EC2 DNS>:8080
 

