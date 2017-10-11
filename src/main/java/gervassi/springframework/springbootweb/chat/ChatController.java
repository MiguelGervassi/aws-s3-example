package gervassi.springframework.springbootweb.chat;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import gervassi.springframework.springbootweb.config.ProxyAuthenticator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    public static String WORKSPACE_ID = "ccb7937b-e6d4-4df7-9922-3daf4140338c";// "bb6b413c-1a2d-4e43-94b7-3cecb644fb39";
    public static String USER_ID = "e8e8e4e8-90a0-4a0e-b01d-1487141ecdeb";
    public static String PASSWORD = "lvjgemtU4zQF";

    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public MessageResponse sendMessage(@RequestBody ChatMessage chatMessage) throws Exception {
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
        service.setUsernameAndPassword(USER_ID, PASSWORD);
        Map<String, Object> context = new HashMap<String, Object>();
        System.out.println(service.getEndPoint().toString());
        if(!chatMessage.getContext().isEmpty()) {
            context = chatMessage.getContext();
        }
        System.out.println("Context Value: " + context);
        MessageRequest newMessage = new MessageRequest.Builder().inputText(chatMessage.getMessage()).context(context).build();
        MessageResponse response = service.message(WORKSPACE_ID, newMessage).execute();
        System.out.println("Response: " + response);
        MessageResponse rxMessageResponse = service.message(WORKSPACE_ID, newMessage).rx().get();
        System.out.println("retrieve rx mesasge responseeee:  " + rxMessageResponse);
        return response;
    }
}