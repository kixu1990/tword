package mutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.tword.MyApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 将imagePath转成byte[]
 * 通过bitMap压缩图片
 */
public class ImageToBytes {
    public static byte[] imagePathToBytes(String imagePath){
        String name = MyApplication.getContext().getExternalCacheDir()+"/face.jpg";
        byte[] bytes = null;
        try {
            Bitmap bitmap = compressImage(new FileInputStream(new File(imagePath)));
            FileOutputStream out = new FileOutputStream(new File(name));
            if(bitmap.compress(Bitmap.CompressFormat.JPEG,80,out)){   //80%的压缩率
                out.flush();
                out.close();
                FileInputStream fis = new FileInputStream(new File(name));
                bytes = new byte[fis.available()];
                fis.read(bytes);
                fis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Bitmap compressImage(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        try {
            while((len = in.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = baos.toByteArray();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts );

        int width = opts.outWidth;
        int height = opts.outHeight;

        int wCompress = 160;    //长度
        int hCompress = 160;    //高度

        int wScale = width/wCompress;
        int hScale = height/hCompress;

        int scale = Math.max(wScale, hScale);

        if(scale <= 0){
            scale = 1;
        }

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;

        return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }
}
