package com.example.myapplication;

import android.net.Uri;
import java.io.Serializable;


public class PhotoPost implements Serializable {

    String photoUri; //사진
    String title; //제목
    String name; // 유저 이름
    String date; // 날짜
    String uid; // 올린 유저 고유 번호

    PhotoPost(){

    }

    //생성자
    PhotoPost(String photoUri, String title, String name, String date, String uid){
        this.photoUri = photoUri;
        this.title = title;
        this.name = name;
        this.date = date;
        this.uid = uid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
