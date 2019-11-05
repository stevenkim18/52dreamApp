package com.example.myapplication;

import android.net.Uri;

import java.io.Serializable;

public class Post implements Serializable {

    String category; //카테고리
    String title; //제목
    String content; //내용
    String name; // 글쓴이
    String date; // 날짜
    String uid; // 회원고유번호
    String photoUri; //사진 uri

    Post(){

    }

    //사진이 없는 게시물의 생성자
    Post(String category, String title, String content, String name, String date, String uid){
        this.category = category;
        this.title = title;
        this.content = content;
        this.name = name;
        this.date = date;
        this.uid = uid;
        this.photoUri = null;
    }

    //사진이 있는 게시물의 생성자
    Post(String category, String title, String content, String name, String date, String uid, String photoUri){
        this.category = category;
        this.title = title;
        this.content = content;
        this.name = name;
        this.date = date;
        this.uid = uid;
        this.photoUri = photoUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
