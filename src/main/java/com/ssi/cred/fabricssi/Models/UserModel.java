package com.ssi.cred.fabricssi.Models;

public class UserModel {
    public String Username;
    public String Id;
    public String Email;
    public String Alias;
    public String FirstName;
    public long phno;

    public long getPhno() {
        return phno;
    }

    public void setPhno(long phno) {
        this.phno = phno;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }


}
