package litepal;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * Created by kixu on 2019/12/25.
 */

public class MessageContentDB extends DataSupport {
    private int sender;
    private long senderTime;
    private String stringContent;
    private long msgId;
    private int userId;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public long getSenderTime() {
        return senderTime;
    }

    public void setSenderTime(long senderTime) {
        this.senderTime = senderTime;
    }

    public String getStringContent() {
        return stringContent;
    }

    public void setStringContent(String stringContent) {
        this.stringContent = stringContent;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
