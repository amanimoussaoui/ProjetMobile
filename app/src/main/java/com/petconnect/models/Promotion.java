package com.petconnect.models;

import java.util.Date;
import java.util.List;

// models/Promotion.java
public class Promotion {
    private String code;
    private String type; // percentage, fixed, shipping
    private double value;
    private Date validFrom;
    private Date validUntil;
    private int usageLimit;
    private int usedCount;
    private double minimumPurchase;
    private List<String> applicableCategories;

    public boolean isValid() {
        Date now = new Date();
        return now.after(validFrom) &&
                now.before(validUntil) &&
                usedCount < usageLimit;
    }
}
