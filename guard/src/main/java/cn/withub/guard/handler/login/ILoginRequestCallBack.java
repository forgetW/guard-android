package cn.withub.guard.handler.login;

import cn.withub.guard.data.UserInfo;

public interface ILoginRequestCallBack {

     void callback(int code, String message, UserInfo userInfo);

}
