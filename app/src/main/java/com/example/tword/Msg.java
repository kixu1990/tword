package com.example.tword;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kixu on 2019/9/7
 * 用于显示对话内容的子类
 * 实现Parcelable 序列化类
 */

public class Msg implements Parcelable {
    public static int TYPE_RECEIVED = 0;  //标识为接收
    public static int TYPE_SENT = 1;      //标识为发出
    private String content;               //实体内容
    private int type;
    private byte[] imageSrc;              //头像,注意会不会因为这个原因 型成高重复Byte[] 导至内存使用高

    public Msg(String content, int type, byte[] imageSrc){
        this.content = content;
        this.type = type;
        this.imageSrc = imageSrc;
    }

    public byte[] getImageSrc(){
        return  imageSrc;
    }
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }

    /**
     * 描述 默认返回0  就可以了
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *序列化过程
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeInt(TYPE_RECEIVED);
        dest.writeInt(TYPE_SENT);
        dest.writeInt(type);
        dest.writeByteArray(imageSrc);
    }

    /**
     * 反序列化
     */
    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel source) {
            return new Msg(source);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    protected Msg(Parcel source){
        content = source.readString();
        TYPE_RECEIVED = source.readInt();
        TYPE_SENT = source.readInt();
        type = source.readInt();
        imageSrc = source.createByteArray();
    }

}
