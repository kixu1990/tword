package com.example.tword.erp;

import java.io.Serializable;
import java.util.List;

public class ErpColorData implements Serializable {
    private String color;
    private int todayProduction;
    private int production;
    private List<ErpSizeData> erpSizeDatas;

    public ErpColorData(String color,int todayProduction,int production,List<ErpSizeData> erpSizeDatas){
        this.color = color;
        this.todayProduction = todayProduction;
        this.production = production;
        this.erpSizeDatas = erpSizeDatas;
    }

    public List<ErpSizeData> getErpSizeDatas() {
        return erpSizeDatas;
    }

    public void setErpSizeDatas(List<ErpSizeData> erpSizeDatas) {
        this.erpSizeDatas = erpSizeDatas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
