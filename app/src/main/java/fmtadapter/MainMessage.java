package fmtadapter;

import java.util.Date;

/**
 * Created by Administrator on 2019/12/6.
 */

public class MainMessage {
    private String headlin;
    private long messageId;
    private String lastContent;
    private Date lastTime;

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public MainMessage(String headlin){
        this.headlin = headlin;
    }

    public String getHeadlin() {
        return headlin;
    }

    public void setHeadlin(String headlin) {
        this.headlin = headlin;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
