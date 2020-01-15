package litepal;

import org.litepal.crud.DataSupport;

/**
 * Created by kixu on 2019/12/11.
 */

public class SatffDB extends DataSupport {
    private String satffName;
    private int satffId;
    private String department;
    private byte[] userImage;
    private int version;
    private int userId;
    private String post;
    private String email;
    private String phoneNumber;
    private String state;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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
