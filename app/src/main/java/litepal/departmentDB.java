package litepal;

import org.litepal.crud.DataSupport;

/**
 * Created by kixu on 2019/12/11.
 */

public class departmentDB extends DataSupport {
    private String departmentName;
    private String departmentManager;
    private int userId;
    private int version;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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
