package com.imooc.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

//    public static final int TOKEN_EXPIRE = 3600*24*2;//token有两天的失效时间
    public static final int TOKEN_EXPIRE = 1;//token有两天的失效时间


    public MiaoshaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");
}
