package com.imooc.miaosha.controller;

import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.vo.GoodsDetailInfo;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/to_list")
    public Object toList(){
        //取缓存
        List goodsList = redisService.get(GoodsKey.getGoodsList,"",List.class);
        //如果缓存不为空就返回数据
        if (goodsList!=null){
            return Result.success(goodsList);
        }
        //从数据库中取得结果不为空,进行缓存
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos!=null){
            redisService.set(GoodsKey.getGoodsList,"",goodsVos);
        }
        return Result.success(goodsVos);
    }

    @RequestMapping("/to_detail")
    public Object detail(long goodsId){
        //取缓存
        GoodsDetailInfo goodsDetail = redisService.get(GoodsKey.getGetGoodsDetail,""+goodsId,GoodsDetailInfo.class);
        if (goodsDetail!=null){
            return Result.success(goodsDetail);
        }
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
        GoodsDetailInfo goodsDetailInfo = new GoodsDetailInfo(goodsVo,miaoshaStatus,remainSeconds);
        //从数据库中取得结果不为空,进行缓存
        if (goodsVo!=null){
            redisService.set(GoodsKey.getGetGoodsDetail,""+goodsId,goodsDetailInfo);
        }
        return Result.success(goodsDetailInfo);
    }

}
