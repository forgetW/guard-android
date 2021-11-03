package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private String userPoolId;
    private String identifier; // host
    private String name;
    private String userpoolLogo;
    private List<String> enabledLoginMethods;
    private List<String> loginTabList;
    private String defaultLoginMethod;
    private int verifyCodeLength = 6;

    public static Config parse(JSONObject data) throws JSONException {
        Config config = new Config();

        config.setUserPoolId(data.getString("userPoolId"));
        config.setIdentifier(data.getString("identifier"));
        config.setName(data.getString("name"));
        config.setUserpoolLogo(data.getString("userpoolLogo"));
        config.setVerifyCodeLength(data.getInt("verifyCodeLength"));

        JSONObject loginTabs = data.getJSONObject("loginTabs");
        JSONArray loginTabList = loginTabs.getJSONArray("list");
        config.setLoginTabList(toStringList(loginTabList));
        config.setDefaultLoginMethod(loginTabs.getString("default"));

        JSONObject passwordTabConfig = data.getJSONObject("passwordTabConfig");
        JSONArray enabledLoginMethods = passwordTabConfig.getJSONArray("enabledLoginMethods");
        config.setEnabledLoginMethods(toStringList(enabledLoginMethods));
        return config;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserpoolLogo() {
        return userpoolLogo;
    }

    public void setUserpoolLogo(String userpoolLogo) {
        this.userpoolLogo = userpoolLogo;
    }

    public List<String> getEnabledLoginMethods() {
        return enabledLoginMethods;
    }

    public void setEnabledLoginMethods(List<String> enabledLoginMethods) {
        this.enabledLoginMethods = enabledLoginMethods;
    }

    public List<String> getLoginTabList() {
        return loginTabList;
    }

    public void setLoginTabList(List<String> loginTabList) {
        this.loginTabList = loginTabList;
    }

    public String getDefaultLoginMethod() {
        return defaultLoginMethod;
    }

    public void setDefaultLoginMethod(String defaultLoginMethod) {
        this.defaultLoginMethod = defaultLoginMethod;
    }

    public int getVerifyCodeLength() {
        return verifyCodeLength;
    }

    public void setVerifyCodeLength(int verifyCodeLength) {
        this.verifyCodeLength = verifyCodeLength;
    }

    private static List<String> toStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add((array.getString(i)));
        }
        return list;
    }
}
