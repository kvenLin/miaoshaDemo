package com.imooc.miaosha.controller;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/to_list")
    public Object toList(){
        return Result.success(goodsService.listGoodsVo());
    }


}
