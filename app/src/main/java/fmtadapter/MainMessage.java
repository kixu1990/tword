package fmtadapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by kixu on 2019/12/6.
 */

public class MainMessage implements Parcelable {
    private byte[] image;          //图标
    private String headlin;       //消息标题
    private long messageId;       //消息ID
    private String lastContent;   //最后一个内容
    private Date lastTime;        //最后发送时间

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

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

    //Parcelable序列化的各种必要方法----------------------------------------------------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(image);
        dest.writeString(headlin);
        dest.writeLong(messageId);
        dest.writeString(lastContent);
        dest.writeLong(lastTime.getTime());
    }

    protected MainMessage(Parcel sourse){
        image = sourse.createByteArray();
        headlin = sourse.readString();
        messageId = sourse.readLong();
        lastContent = sourse.readString();
        lastTime = new Date(sourse.readLong());
    }

    public static final Creator<MainMessage> CREATOR = new Creator<MainMessage>() {
        @Override
        public MainMessage createFromParcel(Parcel source) {
            return new MainMessage(source);
        }

        @Override
        public MainMessage[] newArray(int size) {
            return new MainMessage[size];
        }
    };
    //---------------------------------------------------------------------------------------------------------
}
