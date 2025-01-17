package cn.withub.guard.network;

import org.json.JSONObject;

public class Response {
    private int code;
    private String message;
    private JSONObject data;

    // work-around. should move this to data
    private String recoveryCode;

    public Response() {}

    public Response(int code, String message, JSONObject obj) {
        this.code = code;
        this.message = message;
        this.data = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(String recoveryCode) {
        this.recoveryCode = recoveryCode;
    }
}
