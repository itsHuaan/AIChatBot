package com.example.aichatbot;
public class Message {
    public static String sent_by_user = "User";
    public static String sent_by_ai = "ChatBot";
    String message;
    String send_by;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSend_by() {
        return send_by;
    }

    public void setSend_by(String send_by) {
        this.send_by = send_by;
    }
    public Message(String message, String send_by){
        this.message = message;
        this.send_by = send_by;
    }
}
