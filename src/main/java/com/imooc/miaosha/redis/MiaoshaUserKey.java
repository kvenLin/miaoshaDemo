package com.imooc.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{


    public MiaoshaUserKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey("tk");
    public static MiaoshaUserKey getByName = new MiaoshaUserKey("name");
}
