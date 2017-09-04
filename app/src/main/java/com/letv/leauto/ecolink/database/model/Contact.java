package com.letv.leauto.ecolink.database.model;


import com.letv.leauto.ecolink.utils.PinYinUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact implements Serializable{

    private String name;
    private String sortKey;
    private String number;
    private String  yellowType;

    public Contact() {

    }

    public ArrayList<String> getNumList() {
        return numList;
    }

    public void setNumList(ArrayList<String> numList) {
        this.numList = numList;
    }

    public Contact(String name, String number, String yellowType) {
        this.name = name;
        this.number = number;
        this.yellowType = yellowType;
    }

    private ArrayList<String> numList;
    private int type = 0;
    private String time = "";
    private Date today =null;
    private int num = 1;
    String sortLetters;

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", number='" + number + '\'' +
                ", type=" + type +
                ", time='" + time + '\'' +
                ", today=" + today +
                ", num=" + num +
                '}';
    }
    public Date getToday() {
        return today;
    }
    public void setToday(Date today) {
        this.today = today;
    }

    public int getNum(){
        return num;
    }
    public void setNum(int num){
        this.num = num;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortKey() {
        return PinYinUtil.getFirstSpell(name.substring(0, 1)).toUpperCase();
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (type != contact.type) return false;
        return name != null ? name.equals(contact.name) : contact.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}
