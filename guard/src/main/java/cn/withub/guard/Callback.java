package cn.withub.guard;

public interface Callback<T> {
    void call(boolean ok, T data);
}
