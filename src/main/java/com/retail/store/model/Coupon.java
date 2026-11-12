package com.retail.store.model;

public class Coupon {

    private String code;
    private int percent;

    public Coupon() {
    }

    public Coupon(String code, int percent) {
        this.code = code;
        this.percent = percent;
    }

    public String getCode() {
        return code;
    }

    public int getPercent() {
        return percent;
    }
}
