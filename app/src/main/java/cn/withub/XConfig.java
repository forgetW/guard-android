package cn.withub;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import cn.withub.guard.Authing;
import cn.withub.guard.oneclick.OneClick;
import cn.withub.guard.social.Lark;
import cn.withub.guard.social.WeCom;

import static android.content.Context.MODE_PRIVATE;

public class XConfig {

    public static final String SP_NAME = "SP_GUARD";

    public static final String SP_KEY_SCHEMA = "SP_SCHEMA";
    public static final String SP_KEY_HOST = "SP_HOST";
    public static final String SP_KEY_APPID = "SP_APPID";
    public static String APPID = "";

    String keyHost;
    String keySchema;
    String keyAppId;
    String keyBizId;
    String clientId;

    public String getClientId() {
        return clientId;
    }

    public XConfig setClientId(String clientId) {
        this.clientId = clientId;
        Authing.setClientId(clientId);
        return this;
    }

    public String getKeyBizId() {
        return keyBizId;
    }

    public XConfig setKeyBizId(String keyBizId) {
        this.keyBizId = keyBizId;
        return this;
    }

    public String getKeyHost() {
        return keyHost;
    }

    public XConfig setKeyHost(String keyHost) {
        this.keyHost = keyHost;
        return this;
    }

    public String getKeySchema() {
        return keySchema;
    }

    public XConfig setKeySchema(String keySchema) {
        this.keySchema = keySchema;
        return this;
    }

    public String getKeyAppId() {
        return keyAppId;
    }

    public XConfig setKeyAppId(String keyAppId) {
        APPID = keyAppId;
        this.keyAppId = keyAppId;
        return this;
    }


    public void init(Application application) {
        // one click
        OneClick.bizId = this.getKeyBizId() != null && !this.getKeyHost().equals("") ? this.getKeyBizId() : "74ae90bd84f74b69a88b578bbbbcdcfd";

        String schema = this.getKeySchema() != null && !this.getKeyHost().equals("") ? this.getKeySchema() : "https";
        String host = this.getKeyHost() != null && !this.getKeyHost().equals("") ? this.getKeyHost() : "zhdj.nmgdj.gov.cn";
        String appid = this.getKeyAppId() != null && !this.getKeyHost().equals("") ? this.getKeyAppId() : "cb803b0c95b9439bb49e5843f403e9a7";
        Authing.setScheme(schema);
        Authing.setHost(host);
        Authing.init(application, appid);
    }

    public static void saveScheme(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_SCHEMA, s).commit();
    }

    public static String loadScheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_SCHEMA, "https");
    }

    public static void saveHost(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_HOST, s).commit();
    }

    public static String loadHost(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_HOST, "zhdj.nmgdj.gov.cn");
    }

    public static void saveAppId(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_APPID, s).commit();
    }

    public static String loadAppId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_APPID, APPID);
    }

//    public static void init(Application application) {
//        // one click
//        OneClick.bizId = "74ae90bd84f74b69a88b578bbbbcdcfd";
//
//        String schema = loadScheme(application);
//        String host = loadHost(application);
//        String appid = loadAppId(application);
//        Authing.setScheme(schema);
//        Authing.setHost(host);
//        Authing.init(application, appid);
//    }
}
