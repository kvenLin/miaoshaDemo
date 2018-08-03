package com.imooc.miaosha.util;

import java.util.UUID;

public class UUIDUtil {

    public static String uuid(){
        //原生的uuid是用'-'进行分隔的,这里把'-'去掉
        return UUID.randomUUID().toString().replace("-","");
    }
}
