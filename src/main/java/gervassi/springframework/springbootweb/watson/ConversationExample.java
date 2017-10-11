package gervassi.springframework.springbootweb.watson;

/*
 * Copyright 2017 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import gervassi.springframework.springbootweb.config.ProxyAuthenticator;
import jersey.repackaged.jsr166e.CompletableFuture;
import okhttp3.*;

/**
 * Example of how to call the {@link ConversationService#message(String, MessageRequest)} method synchronous,
 * asynchronous, and using react.
 *
 * @version v1-experimental
 */
public class ConversationExample {
    public static String WORKSPACE_ID = "ccb7937b-e6d4-4df7-9922-3daf4140338c";
    public static String USER_ID = "e8e8e4e8-90a0-4a0e-b01d-1487141ecdeb";
    public static String PASSWORD = "lvjgemtU4zQF";

    public static void main(String[] args) throws Exception {
        ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03) {
//            @Override
//            protected OkHttpClient configureHttpClient() {
//                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.98.20.23", 8080));
//                return super.configureHttpClient().newBuilder().proxy(proxy).build();
//            }
//            @Override
//            public Request authenticate(Route route, Response response) throws IOException {
//                String credential = Credentials.basic(username, password);
//                return response.request().newBuilder()
//                        .header("Proxy-Authorization", credential)
//                        .build();
//            }
        };
//        new ProxyAuthenticator();
//        service.setEndPoint("http://10.98.20.23:8080");
//        service.setUsernameAndPassword("gervasim", "J0hnwick2");

        // sync
        service.setUsernameAndPassword(USER_ID, PASSWORD);
        MessageRequest newMessage = new MessageRequest.Builder().inputText("").build();
        MessageResponse response = service.message(WORKSPACE_ID, newMessage).execute();
        System.out.println(response);

        // async
        service.message(WORKSPACE_ID, newMessage).enqueue(new ServiceCallback<MessageResponse>() {
            @Override
            public void onResponse(MessageResponse response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(Exception e) { }
        });

        // rx callback
        service.message(WORKSPACE_ID, newMessage).rx()
                .thenApply(new CompletableFuture.Fun<MessageResponse, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(MessageResponse message) {
                        return message.getOutput();
                    }
                }).thenAccept(new CompletableFuture.Action<Map<String, Object>>() {
            @Override
            public void accept(Map<String, Object> output) {
                System.out.println(output);
            }
        });

        // rx async callback
        service.message(WORKSPACE_ID, newMessage).rx()
                .thenApplyAsync(new CompletableFuture.Fun<MessageResponse, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(MessageResponse message) {
                        return message.getOutput();
                    }
                }).thenAccept(new CompletableFuture.Action<Map<String, Object>>() {
            @Override
            public void accept(Map<String, Object> output) {
                System.out.println(output);
            }
        });

        // rx sync
        MessageResponse rxMessageResponse = service.message(WORKSPACE_ID, newMessage).rx().get();
        System.out.println(rxMessageResponse);
    }

}
