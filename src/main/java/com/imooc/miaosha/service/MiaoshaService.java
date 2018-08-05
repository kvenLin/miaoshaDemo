package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods){
        //可以进行秒杀
        //1.减库存,2.下订单,3.写入秒杀,事务
        boolean success = goodsService.reduceStock(goods);
        if (success){
            //order_info,miaosha_order
            return orderService.createOrder(user,goods);
        }
        setGoodsOver(goods.getId());
        return null;
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exist(MiaoshaKey.isGoodsOver,""+goodsId);
    }

    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        //下单成功
        if (order!=null){
            return order.getOrderId();
        }
        boolean isOver = getGoodsOver(goodsId);
        //卖完
        if (isOver){
            return -1;
        }
        //排队中
        return 0;
    }

    public boolean checkPath(long userId,long goodsId,String path) {
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, userId + "_" + goodsId, String.class);
        if (path==null){
            return false;
        }
        if (path.equals(pathOld)){
            return true;
        }
        return false;
    }

    public String createPath(long userId, long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,userId+"_"+goodsId,path);
        return path;
    }
}
