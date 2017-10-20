package aws.example.controllers;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.ListKeysRequest;
import com.amazonaws.services.kms.model.ListKeysResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kms")
public class KMSController {
    //    {Keys: [{KeyId: 50ed4054-c884-4e95-a7a7-4b0bc877a352,KeyArn: arn:aws:kms:us-east-1:375199752119:key/50ed4054-c884-4e95-a7a7-4b0bc877a352}, {KeyId: 770c1834-f173-4dde-83a0-90eb92037b18,KeyArn: arn:aws:kms:us-east-1:375199752119:key/770c1834-f173-4dde-83a0-90eb92037b18}, {KeyId: 915fabeb-9f55-44df-a455-a7f575cbc368,KeyArn: arn:aws:kms:us-east-1:375199752119:key/915fabeb-9f55-44df-a455-a7f575cbc368}, {KeyId: b62d0b80-8259-4067-8926-27af6fb93395,KeyArn: arn:aws:kms:us-east-1:375199752119:key/b62d0b80-8259-4067-8926-27af6fb93395}, {KeyId: c7e4e68a-f5d3-447d-b834-e0b4afd94550,KeyArn: arn:aws:kms:us-east-1:375199752119:key/c7e4e68a-f5d3-447d-b834-e0b4afd94550}, {KeyId: d67c780a-af93-4bf5-b502-9ec27dc54a3f,KeyArn: arn:aws:kms:us-east-1:375199752119:key/d67c780a-af93-4bf5-b502-9ec27dc54a3f}, {KeyId: e1e13471-51c7-4c7a-b028-36f65541335e,KeyArn: arn:aws:kms:us-east-1:375199752119:key/e1e13471-51c7-4c7a-b028-36f65541335e}],Truncated: false}
    //    {Aliases: [{AliasName: alias/aws/ebs,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/ebs,}, {AliasName: alias/aws/elasticfilesystem,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/elasticfilesystem,}, {AliasName: alias/aws/rds,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/rds,TargetKeyId: d67c780a-af93-4bf5-b502-9ec27dc54a3f}, {AliasName: alias/aws/redshift,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/redshift,}, {AliasName: alias/aws/s3,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/s3,TargetKeyId: e1e13471-51c7-4c7a-b028-36f65541335e}, {AliasName: alias/aws/ssm,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/aws/ssm,}, {AliasName: alias/ldkeys,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/ldkeys,TargetKeyId: c7e4e68a-f5d3-447d-b834-e0b4afd94550}, {AliasName: alias/s3_br_gto_dev_kms_us_east_1,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/s3_br_gto_dev_kms_us_east_1,TargetKeyId: 50ed4054-c884-4e95-a7a7-4b0bc877a352}, {AliasName: alias/test-keys,AliasArn: arn:aws:kms:us-east-1:375199752119:alias/test-keys,TargetKeyId: 770c1834-f173-4dde-83a0-90eb92037b18}],Truncated: false}
    @RequestMapping(path = "/keys", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public static List listKeys() throws Exception {
        String marker = null;
        AWSKMS kms = AWSKMSClientBuilder.defaultClient();
        ListKeysRequest req = new ListKeysRequest().withMarker(marker);
        ListKeysResult result = kms.listKeys(req);
        List keyListEntry = result.getKeys();
        System.out.println(keyListEntry);
        return keyListEntry;
    }

    @RequestMapping(path = "/aliases", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public static List listAliases() throws Exception {
        AWSKMS kms = AWSKMSClientBuilder.defaultClient();
        ListAliasesResult result = kms.listAliases();
        List aliasListEntry = result.getAliases();
        System.out.println(aliasListEntry);
        return aliasListEntry;
    }
}
