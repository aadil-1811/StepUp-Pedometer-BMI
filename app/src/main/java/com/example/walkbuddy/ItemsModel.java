package com.example.walkbuddy;

public class ItemsModel {
    private String date1, step1, dis1, cal1;

    public ItemsModel(String date1, String step1, String dis1, String cal1) {
        this.date1 = date1;
        this.step1 = step1;
        this.dis1 = dis1;
        this.cal1 = cal1;
    }

    public String getCal1() {
        return cal1;
    }

    public void setCal1(String cal1) {
        this.cal1 = cal1;
    }

    public String getDis1() {
        return dis1;
    }

    public void setDis1(String dis1) {
        this.dis1 = dis1;
    }

    public String getStep1() {
        return step1;
    }

    public void setStep1(String step1) {
        this.step1 = step1;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }
}
