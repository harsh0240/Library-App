package com.example.user.librarytrial;

import android.support.annotation.NonNull;

/**
 * Created by user on 27-01-2018.
 */

public class User {

    String username;
    String email;
    String password;

    public User() {
        //Empty Constructor For Firebase
    }


    public User(String username, String email)
    {
        this.username = username;
        this.email = email;
    }

    //Getters and Setters
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public String getEmail()
    {

        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public void setPassword(String password){this.password = password;}
    public String getPassword() {return  password;}
}
