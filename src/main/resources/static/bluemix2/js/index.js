// var userName = window.prompt("Enter your name", "some user");


var userName = "broadridge-user";

init();

function init() {
    var contextDict = {};
    var message = {message: "", from: userName, context: contextDict };
    post('/chat', message);
    return false;
}

function reset() {
    var $contextHiddenInput = $('#contextHiddenInput');
    var $chatBox = $("#scrollingChat");
    $contextHiddenInput.val("");
    $chatBox.html("");
    var contextDict = {};
    var message = {message: "", from: userName, context: contextDict };
    post('/chat', message);
    return false;
}


function post(url, data) {
    return $.ajax({
        type: 'POST',
        url: url,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data),
        success: function (responseData) {

            // set context
            var $contextHiddenInput = $('#contextHiddenInput');
            var context = responseData.context;
            console.log("This is the context data you are storing: " + JSON.stringify(context));
            $contextHiddenInput.val(JSON.stringify(context));

            // set response
            var responseList = responseData.output.text;
            for(var response in responseList) {
                var messageInput = responseList[response];
                if(messageInput != '') {
                    console.log(messageInput);
                    var placeholder = "";
                    if(messageInput.indexOf("[ACCOUNT_DETAILS]") > -1) {
                        showMessageFromWatson("Sure. I am gathering your account information...");
                        placeholder = "[ACCOUNT_DETAILS]";
                        if(!context['username']) context['username'] = "johndoe";
                        console.log("We are checking for username here: " + context['username']);
                        var url = "https://openwhisk.ng.bluemix.net/api/v1/web/Miguel.Gervassi_dev/broadridge.accounts/getaccountnumber.json";
                        showMessageFromWatsonModified(url, context, messageInput, placeholder, context);
                    }
                    else if(messageInput.indexOf("[BALANCE_DETAILS]") > -1) {
                        placeholder = "[BALANCE_DETAILS]";
                        showMessageFromWatson("Please hold momentarily as I retrieve your balance information for those details...");
                        var url = "https://openwhisk.ng.bluemix.net/api/v1/web/Miguel.Gervassi_dev/broadridge.accounts/getbalance.json";
                        showMessageFromWatsonModified(url, context, messageInput, placeholder, context);
                    }
                    else if(messageInput.indexOf("[PENDING_DETAILS]") > -1) {
                        placeholder = "[PENDING_DETAILS]";
                        showMessageFromWatson("I will check that out for you...");
                        var url = "https://openwhisk.ng.bluemix.net/api/v1/web/Miguel.Gervassi_dev/broadridge.accounts/getpending.json";
                        showMessageFromWatsonModified(url, context, messageInput, placeholder, context);
                    } else {
                        showMessageFromWatson(messageInput);
                    }
                    break;
                }
            }
            // alert(JSON.stringify(response));
        }
    })
}

function showMessageFromWatson(messageInput) {
    var message = {message: messageInput, from: "Watson"};
    appendMessage(message);
}

// have a receipt and expense.


function showMessageFromWatsonModified(url, data, messageInput, placeholder, context) {
    console.log("Data we are possing as ontext to the get account dteails method: " + JSON.stringify((data)));
    return $.ajax({
        type: 'POST',
        url: url,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data),
        success: function (responseData) {
            var result = responseData.result;
            if(placeholder == "[ACCOUNT_DETAILS]") {
                console.log("You are setting the context here");
                context["AccountNumber"] = result;
                console.log("Account Number in context is : " + context["AccountNumber"]);
            }
            messageInput = messageInput.replace(placeholder, result);
            console.log("Check this value out: " + messageInput);
            if(messageInput.indexOf("I could not find an account with those details") > -1) {
                // delete context["AccountNumber"];
                delete context["SocialSecurityNumber"];
            }
            var $contextHiddenInput = $('#contextHiddenInput');
            $contextHiddenInput.val(JSON.stringify(context));
            showMessageFromWatson(messageInput);
        }
    })
}

function appendMessage(message) {
    var fromNow = moment(message.time).format('HH:mm:ss');
    var $messageHtml = $(`
    <div class="segments load">
        <div class="${message.from == userName ? 'from-user': 'from-watson'} latest ${message.from == userName ? 'sub' : 'top'}">
            <div class="message-inner">
                <p>${message.message}</p>
            </div>
        </div>
    </div>`
    );
    console.log($messageHtml);
    var $messages = $("#scrollingChat");
    $messages.append($messageHtml);
    $messages.scrollTop($messages.prop("scrollHeight"));
}

// function displayMessage(newPayload, typeValue) {
//     var isUser = isUserMessage(typeValue);
//     var textExists = (newPayload.input && newPayload.input.text)
//         || (newPayload.output && newPayload.output.text);
//     if (isUser !== null && textExists) {
//         // Create new message DOM element
//         var messageDivs = buildMessageDomElements(newPayload, isUser);
//         var chatBoxElement = document.querySelector(settings.selectors.chatBox);
//         var previousLatest = chatBoxElement.querySelectorAll((isUser
//             ? settings.selectors.fromUser : settings.selectors.fromWatson)
//             + settings.selectors.latest);
//         // Previous "latest" message is no longer the most recent
//         if (previousLatest) {
//             Common.listForEach(previousLatest, function(element) {
//                 element.classList.remove('latest');
//             });
//         }
//
//         messageDivs.forEach(function(currentDiv) {
//             chatBoxElement.appendChild(currentDiv);
//             // Class to start fade in animation
//             currentDiv.classList.add('load');
//         });
//         // Move chat to the most recent messages when new messages are added
//         scrollToChatBottom();
//     }
// }
//
// // Constructs new DOM element from a message payload
// function buildMessageDomElements(newPayload, isUser) {
//     var textArray = isUser ? newPayload.input.text : newPayload.output.text;
//     if (Object.prototype.toString.call( textArray ) !== '[object Array]') {
//         textArray = [textArray];
//     }
//     var messageArray = [];
//
//     textArray.forEach(function(currentText) {
//         if (currentText) {
//             var messageJson = {
//                 // <div class='segments'>
//                 'tagName': 'div',
//                 'classNames': ['segments'],
//                 'children': [{
//                     // <div class='from-user/from-watson latest'>
//                     'tagName': 'div',
//                     'classNames': [(isUser ? 'from-user' : 'from-watson'), 'latest', ((messageArray.length === 0) ? 'top' : 'sub')],
//                     'children': [{
//                         // <div class='message-inner'>
//                         'tagName': 'div',
//                         'classNames': ['message-inner'],
//                         'children': [{
//                             // <p>{messageText}</p>
//                             'tagName': 'p',
//                             'text': currentText
//                         }]
//                     }]
//                 }]
//             };
//             messageArray.push(Common.buildDomElement(messageJson));
//         }
//     });
//
//     return messageArray;
// }
// function getPreviousMessages() {
//     $.get('/chat').done(messages => messages.forEach(appendMessage));
// }

function sendMessage() {
    var $messageInput = $('#textInput');
    console.log("THIS IS THE MESSAGEINPUT" + $messageInput.val());
    var $contextHiddenInput = $('#contextHiddenInput');
    console.log("HIDDENVALUE: " + $contextHiddenInput.val());
    var contextDict = {};
    if($contextHiddenInput.val() != '') {
        contextDict = JSON.parse($contextHiddenInput.val());
    }
    console.log("CONTEXTDIC: " + contextDict);
    var message = {message: $messageInput.val(), from: userName, context: contextDict };
    $messageInput.val('');
    appendMessage(message);
    post('/chat', message);
    return false;
}

// function onNewMessage(result) {
//     var message = JSON.parse(result.body);
//     appendMessage(message);
// }

// function connectWebSocket() {
//     var socket = new SockJS('/chatWS');
//     stompClient = Stomp.over(socket);
//     //stompClient.debug = null;
//     stompClient.connect({}, (frame) => {
//         console.log('Connected: ' + frame);
//     stompClient.subscribe('/topic/messages', onNewMessage);
// });
// }
//
// getPreviousMessages();
// connectWebSocket();