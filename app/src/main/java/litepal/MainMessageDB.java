package litepal;

import com.example.tword.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kixu on 2019/12/16.
 */

public class MainMessageDB extends DataSupport {
    private int userId;
    private long messageId;
    private String headlin;
    private Date date;

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
