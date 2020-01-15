package com.example.tword;

import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;

import message.MyMessage;

/**
 * Created by kixu on 2019/9/9.
 */

public class User {

    private String userName = "";

    public int userId = 0;

    private String passwrd = "";
    private String loginName = "";

    private static User INSTANCE = new User();

    private User(){};

    public static User getINSTANCE(){
        return INSTANCE;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswrd() {
        return passwrd;
    }

    public void setPasswrd(String passwrd) {
        this.passwrd = passwrd;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
