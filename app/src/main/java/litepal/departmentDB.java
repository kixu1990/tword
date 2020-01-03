package litepal;

import org.litepal.crud.DataSupport;

/**
 * Created by kixu on 2019/12/11.
 */

public class departmentDB extends DataSupport {
    private String departmentName;
    private String departmentManager;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentManager() {
        return departmentManager;
    }

    public void setDepartmentManager(String departmentManager) {
        this.departmentManager = departmentManager;
    }
}
