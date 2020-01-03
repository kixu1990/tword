package message;

import java.io.Serializable;
import java.sql.Date;

/**
 * 用于传输的消息类
 * Created by kixu on 2019/9/16.
 */

public class MyMessage implements Serializable{
    private int sender;
    private int[] receivers;
    private String stringContent;
    private String header;
    private String loginName = "";
    private String loginPassword = "";
    private String userName = "";
    private String messageLable = "";
    private long messageId = 0;
    private Object[] objects;
    private Date date;

    public MyMessage(int sender,int[] receivers,String header){
        this.sender = sender;
        this.receivers = receivers;
        this.header = header;

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageLable() {
        return messageLable;
    }

    public void setMessageLable(String messageLable) {
        this.messageLable = messageLable;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int[] getReceivers() {
        return receivers;
    }

    public void setReceivers(int[] receivers) {
        this.receivers = receivers;
    }

    public String getStringContent() {
        return stringContent;
    }

    public void setStringContent(String stringContent) {
        this.stringContent = stringContent;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
}
