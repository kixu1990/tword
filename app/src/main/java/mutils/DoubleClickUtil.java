package mutils;


/**
 * created by kixu on 2020/03/03
 * 防按键连击类
 */
public class DoubleClickUtil {
    private static long mLastClick;

    public static boolean isDoubleClick(long milliseconds){
        if(System.currentTimeMillis() - mLastClick <= milliseconds){
            return  true;
        }
        mLastClick = System.currentTimeMillis();
        return false;
    }
}
