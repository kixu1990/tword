package com.example.tword.erp;

import java.util.List;

/**
 * 外层RecyclerView的数据 bean
 */
public class ErpReportData {
    private String department;       //部门
    private String lotCount;         //发织数
    private String todayProduction;  //今天完成数
    private String production;       //总完成数
    private String notProduction;    //未完成数
    private List<ErpReportItemData> itemDataList; //每行数据

    public ErpReportData(String department,List<ErpReportItemData> itemDataList){
        this.department = department;
        this.itemDataList = itemDataList;
        int lc = 0;
        int tp = 0;
        int p = 0;
        int np = 0;
        for(ErpReportItemData itemData :itemDataList){
            lc += itemData.getToltal();
            tp += itemData.getTodayProduction();
            p += itemData.getProduction();
            np += itemData.getNotProduction();
        }
        this.lotCount = String.valueOf(lc);
        this.todayProduction = String.valueOf(tp);
        this.production = String.valueOf(p);
        this.notProduction = String.valueOf(np);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<ErpReportItemData> getItemDataList() {
        return itemDataList;
    }

    public void setItemDataList(List<ErpReportItemData> itemDataList) {
        this.itemDataList = itemDataList;
    }

    public String getLotCount() {
        return lotCount;
    }

    public void setLotCount(String lotCount) {
        this.lotCount = lotCount;
    }

    public String getTodayProduction() {
        return todayProduction;
    }

    public void setTodayProduction(String todayProduction) {
        this.todayProduction = todayProduction;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getNotProduction() {
        return notProduction;
    }

    public void setNotProduction(String notProduction) {
        this.notProduction = notProduction;
    }
}
