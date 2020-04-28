package litepal;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * Created by kixu on 2019/12/25.
 */

public class MessageContentDB extends DataSupport {
    private int sender;           //发送者
    private long senderTime;      //发送时间
    private String stringContent; //文本内容
    private long msgId;           // 消息ID
    private int userId;           //使用者ID

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
