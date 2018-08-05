package com.imooc.miaosha.access;

public @interface AccessLimit {
    int seconds();
    int maxCount();
    boolean needLogin() default true;

}
