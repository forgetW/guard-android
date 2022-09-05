package cn.withub.guard.handler;


import cn.withub.guard.Authing;

public class BaseHandler {

    protected Authing.AuthProtocol getAuthProtocol() {
        return Authing.getAuthProtocol();
    }
}
