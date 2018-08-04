package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    public Object info(long userId,long orderId){
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoodsVo(goodsVo);
        detailVo.setOrderInfo(orderInfo);
        return Result.success(detailVo);
    }
}
