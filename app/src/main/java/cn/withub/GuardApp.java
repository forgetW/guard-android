package cn.withub;

import android.content.Context;
import android.content.SharedPreferences;

import cn.withub.guard.Authing;
import cn.withub.guard.oneclick.OneClick;


public class GuardApp extends android.app.Application {

//    public static final String SP_NAME = "SP_AUTHING_GUARD";
//
//    public static final String SP_KEY_SCHEMA = "SP_SCHEMA";
//    public static final String SP_KEY_HOST = "SP_HOST";
//    public static final String SP_KEY_APPID = "SP_APPID";
//
//    public static void saveScheme(Context context, String s) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
//        sp.edit().putString(SP_KEY_SCHEMA, s).commit();
//    }
//
//    public static String loadScheme(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
//        return sp.getString(SP_KEY_SCHEMA, "https");
//    }
//
//    public static void saveHost(Context context, String s) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
//        sp.edit().putString(SP_KEY_HOST, s).commit();
//    }
//
//    public static String loadHost(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
//        return sp.getString(SP_KEY_HOST, "zhdj.nmgdj.gov.cn");
//    }
//
//    public static void saveAppId(Context context, String s) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
//        sp.edit().putString(SP_KEY_APPID, s).commit();
//    }
//
//    public static String loadAppId(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
//        return sp.getString(SP_KEY_APPID, "dfb7ffcb782f4be7bb4d659dc9c9a005");
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        // one click
//        OneClick.bizId = "74ae90bd84f74b69a88b578bbbbcdcfd";
//
//        String schema = loadScheme(this);
//        String host = loadHost(this);
//        String appid = loadAppId(this);
//        Authing.setScheme(schema);
//        Authing.setHost(host);
//        Authing.init(getApplicationContext(), appid);

//        XConfig.init(this);
        new XConfig().init(this);
    }
}
