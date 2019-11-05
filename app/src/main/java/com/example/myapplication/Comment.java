package com.example.myapplication;
//댓글 객체
public class Comment {
    String name;
    String contents;
    String date;
    String uid;

    Comment(){

    }

    Comment(String name, String contents, String date, String uid){
        this.name = name;
        this.contents = contents;
        this.date = date;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
