package com.example.notes_app.model;

import android.content.SharedPreferences;

public class UserData {

    String name;
    String email;
    String url;
    String userId;

    public UserData(String name, String email, String url, String userId) {
        this.name = name;
        this.email = email;
        this.url = url;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void saveToSharedPreferences(SharedPreferences.Editor editor) {
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("url", url);
        editor.putString("userId", userId);
    }
}
