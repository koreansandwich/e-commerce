package backend.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
    private String text;
    private String sender;
    private String user;       // 메시지를 보낸 사용자 이름 (또는 ID)
    private LocalDateTime timestamp;

    public MessageDTO() {}

    public MessageDTO(String text, String sender, String user, LocalDateTime timestamp) {
        this.text = text;
        this.sender = sender;
        this.user = user;
        this.timestamp = timestamp;
    }

    // Getter 및 Setter
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

