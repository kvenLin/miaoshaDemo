package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.domain.User;
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

    @Transactional
    public OrderInfo miaosha(User user, GoodsVo goods){
        //可以进行秒杀
        //1.减库存,2.下订单,3.写入秒杀,事务
        goodsService.reduceStock(goods);
        //order_info,miaosha_order
        return orderService.createOrder(user,goods);
    }
}
