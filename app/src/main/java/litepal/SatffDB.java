package litepal;

import org.litepal.crud.DataSupport;

/**
 * Created by kixu on 2019/12/11.
 */

public class SatffDB extends DataSupport {
    private String satffName;
    private int satffId;
    private String department;

    public String getSatffName() {
        return satffName;
    }

    public void setSatffName(String satffName) {
        this.satffName = satffName;
    }

    public int getSatffId() {
        return satffId;
    }

    public void setSatffId(int satffId) {
        this.satffId = satffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
