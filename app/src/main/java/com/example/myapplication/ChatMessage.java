package com.example.myapplication;

public class ChatMessage {

    String username; //유저 이름
    String message;  //메시지
    String date; // 메시지를 입력한 시간

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
