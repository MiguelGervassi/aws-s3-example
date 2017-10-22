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
