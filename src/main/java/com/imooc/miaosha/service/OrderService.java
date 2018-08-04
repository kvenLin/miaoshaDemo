package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = redisService.get(OrderKey.getMiaoshaOrderByUidGid,userId+"_"+goodsId,MiaoshaOrder.class);
        if (miaoshaOrder==null){
            miaoshaOrder = orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        }
        if (miaoshaOrder!=null){
            redisService.set(OrderKey.getMiaoshaOrderByUidGid,userId+"_"+goodsId,miaoshaOrder);
        }
        return miaoshaOrder;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods) {
        //插入order_info,miaosha_order两张表
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        //注:插入数据后返回的ID是直接赋值给对象,而不是通过方法返回ID值
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        //从赋予ID值后的对象中获取ID值
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        //缓存订单
        redisService.set(OrderKey.getMiaoshaOrderByUidGid,user.getId()+"_"+goods.getId(),miaoshaOrder);
        return orderInfo;
    }
}
