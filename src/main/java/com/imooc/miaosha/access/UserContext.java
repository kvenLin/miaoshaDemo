package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoShaUser;

public class UserContext {
    //threadLocal是和当前线程绑定的,在threadLocal中放东西是放到当前线程的
    public static ThreadLocal<MiaoShaUser> userHolder = new InheritableThreadLocal<>();

    public static void setUser(MiaoShaUser user){
        userHolder.set(user);
    }

    public static MiaoShaUser getUser(){
        return userHolder.get();
    }
}
