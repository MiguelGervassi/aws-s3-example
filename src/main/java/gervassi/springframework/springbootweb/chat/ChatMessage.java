package gervassi.springframework.springbootweb.chat;

import org.aspectj.weaver.patterns.HasMemberTypePattern;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class ChatMessage {
    public String message;
    public String from;
    public OffsetDateTime time;
    public HashMap<String, Object> context;

    public ChatMessage() {
    }

    public ChatMessage(String message, String from, OffsetDateTime time) {
        this.message = message;
        this.from = from;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

    public HashMap<String, Object> getContext() {
        return context;
    }

    public void setContext(HashMap<String, Object> context) {
        this.context = context;
    }
}