package cn.withub.guard.handler.register;

import cn.withub.guard.data.UserInfo;

public interface IRegisterRequestCallBack {

    void callback(int code, String message, UserInfo userInfo);
}
