package litepal;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by kixu on 2019/12/16.
 */

public class MainMessageDB extends DataSupport {
    private byte[] image;   //显示图片
    private int userId;     //使用者ID
    private long messageId; //消息ID
    private String headlin; //消息标题
    private Date date;      //最后消息日期

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getHeadlin() {
        return headlin;
    }

    public void setHeadlin(String headlin) {
        this.headlin = headlin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
