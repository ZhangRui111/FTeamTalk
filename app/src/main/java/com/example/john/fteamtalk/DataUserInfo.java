package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/23.
 */

public class DataUserInfo {
    private int id;
    private String username;
    private String nickname;
    private String usericon;
    private String department;
    private String usersex;
    private String signature;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsericon() {
        return usericon;
    }

    public String getDepartment() {
        return department;
    }

    public String getUsersex() {
        return usersex;
    }

    public String getSignature() {
        return signature;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUsericon(String usericon) {
        this.usericon = usericon;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setUsersex(String usersex) {
        this.usersex = usersex;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
