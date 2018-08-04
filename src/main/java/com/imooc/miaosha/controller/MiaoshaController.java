package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.*;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.MiaoshaOrderGoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/miaosha")
@RestController
public class MiaoshaController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private MiaoshaUserService userService;

    //暂时只关注秒杀逻辑,所以直接通过接口传入userId和goodsId
    //TODO,后续使用security完善权限控制
    @RequestMapping("/do_miaosha")
    public Object doMiaosha(Long userId,long goodsId){
        MiaoShaUser user = userService.getById(userId);
        //判断商品库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount()<=0){
            return Result.error(CodeMsg.STOCK_NOT_ENOUGH);
        }
        //判断是否秒杀成功
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if (order!=null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //可以进行秒杀
        //1.减库存,2.下订单,3.写入秒杀,事务
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);

        //返回订单和商品信息
        MiaoshaOrderGoodsVo miaoshaOrderGoodsVo = new MiaoshaOrderGoodsVo(orderInfo,goodsVo);
        return Result.success(miaoshaOrderGoodsVo);
    }
}
