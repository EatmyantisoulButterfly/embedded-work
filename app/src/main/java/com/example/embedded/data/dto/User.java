package com.example.embedded.data.dto;

import java.io.Serializable;

public class User implements Serializable {
    private String account;
    private String passWord;
    private String name;
    private String age;
    private String personNumber;
    private String sex;
    private String address;

    public User(String account, String passWord, String name, String age, String personNumber, String sex, String address) {
        this.account = account;
        this.passWord = passWord;
        this.name = name;
        this.age = age;
        this.personNumber = personNumber;
        this.sex = sex;
        this.address = address;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
