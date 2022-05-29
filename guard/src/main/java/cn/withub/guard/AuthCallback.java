package cn.withub.guard;

import java.io.Serializable;

public interface AuthCallback<T> extends Serializable {
    void call(int code, String message, T data);
}