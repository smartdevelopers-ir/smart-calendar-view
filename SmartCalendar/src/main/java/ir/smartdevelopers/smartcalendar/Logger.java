package ir.smartdevelopers.smartcalendar;

import android.util.Log;


public class Logger {
    private static String TAG="TTT";
    public static void LogV(String tag,String message,Exception e){
        if (BuildConfig.DEBUG){
            Log.v(tag,message,e);
        }
    }
    public static void LogV(String tag,String message){
        LogV(tag,message,null);
    }
    public static void LogV(String message){
        LogV(TAG,message,null);
    }
}
