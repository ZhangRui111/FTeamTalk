package com.example.john.fteamtalk;

/**
 * Created by john on 2017/5/8.
 */

public class DataFriendInfo {
    private int userId;
    private String userNickName;
    private String department;

    public DataFriendInfo(int userId, String userNickName, String department) {
        this.userId = userId;
        this.department = department;
        this.userNickName = userNickName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
