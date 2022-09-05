package cn.withub.ut;

import cn.withub.guard.data.UserInfo;

public interface IHttpCallBack {

    void showResult(String apiName, int code, String message, UserInfo data);
}
