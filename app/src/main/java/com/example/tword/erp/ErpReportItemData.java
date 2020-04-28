package com.example.tword.erp;

import java.util.List;

/**
 * 内层RecyclerView的数据 bean
 */
public class ErpReportItemData {
    private String department;    //部门
    private String lotNumber;     //批号
    private int toltal;           //总数
    private int todayProduction;  //今日完成数
    private int production;       //总完成数
    private int NotProduction;    //未完成数
    private int progress;         //进度百分比
    private List<ErpColorData> erpColorDataList;

    public ErpReportItemData(String department,String lotNumber,int toltal,int todayProduction,int production,int NotProduction,int progress,List<ErpColorData> erpColorDataList){
        this.department = department;
        this.lotNumber = lotNumber;
        this.toltal = toltal;
        this.todayProduction = todayProduction;
        this.production = production;
        this.NotProduction = NotProduction;
        this.progress = progress;
        this.erpColorDataList = erpColorDataList;
    }

    public List<ErpColorData> getErpColorDataList() {
        return erpColorDataList;
    }

    public void setErpColorDataList(List<ErpColorData> erpColorDataList) {
        this.erpColorDataList = erpColorDataList;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public int getToltal() {
        return toltal;
    }

    public void setToltal(int toltal) {
        this.toltal = toltal;
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

    public int getNotProduction() {
        return NotProduction;
    }

    public void setNotProduction(int notProduction) {
        NotProduction = notProduction;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
