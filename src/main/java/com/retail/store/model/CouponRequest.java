package com.retail.store.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class CouponRequest {

    @Min(1)
    @Max(90)
    private int percent = 10;

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
