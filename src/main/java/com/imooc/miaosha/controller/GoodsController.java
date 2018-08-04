package com.imooc.miaosha.controller;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.vo.GoodsDetailInfo;
import com.imooc.miaosha.vo.GoodsVo;
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

    @RequestMapping("/to_detail")
    public Object detail(long goodsId){
        //TODO,注:一般正规的生产环境很少用自增的Id,可以使用snowflake
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now<startAt){
            //秒杀还没开始,倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now)/1000);
        }else if (now>endAt){
            //秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        return Result.success(new GoodsDetailInfo(goodsVo,miaoshaStatus,remainSeconds));
    }

}
