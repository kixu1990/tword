package com.example.tword.erp;

import java.io.Serializable;

public class ErpSizeData implements Serializable {
    private String size;
    private int todayProduction;
    private int production;

    public ErpSizeData(String size,int todayProduction,int production){
        this.size = size;
        this.todayProduction = todayProduction;
        this.production = production;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getTodayProduction() {
        return todayProduction;
    }

    public void setTodayProduction(int todayProduction) {
        this.todayProduction = todayProduction;
    }

    public int getProduction() {
        return production;
    }

    public void setProduction(int production) {
        this.production = production;
    }
}
