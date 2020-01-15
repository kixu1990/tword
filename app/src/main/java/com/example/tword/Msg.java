package com.example.tword;

/**
 * Created by kixu on 2019/9/7.
 */

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    private byte[] imageSrc;

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

}
