package com.example.myapplication;

public class Profile{

    //프로필 사진
    private String imageUrl;
    //이메일(아이디)
    private String email;
    //비밀번호
    private String password;
    //이름
    private String name;
    //마을
    private String village;
    //기수
    private String ageNum;
    //회원고유 번호
    private String uid;

    public Profile(){

    }

    public Profile(String imageUrl, String email, String password, String name, String village, String ageNum, String uid){
        this.imageUrl = imageUrl;
        this.email = email;
        this.password = password;
        this.name = name;
        this.village = village;
        this.ageNum = ageNum;
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAgeNum() {
        return ageNum;
    }

    public void setAgeNum(String ageNum) {
        this.ageNum = ageNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
