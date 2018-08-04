package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabiitmq.MQSender;
import com.imooc.miaosha.rabiitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.MiaoshaOrderGoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/miaosha")
@RestController
public class MiaoshaController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private MiaoshaUserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    //暂时只关注秒杀逻辑,所以直接通过接口传入userId和goodsId
    //TODO,后续使用security完善权限控制
    @RequestMapping("/do_miaosha")
    public Object doMiaosha(Long userId,long goodsId){
        MiaoShaUser user = userService.getById(userId);
        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if (stock<0){
            return Result.error(CodeMsg.STOCK_NOT_ENOUGH);
        }

        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setUser(user);
        mqSender.sendMiaoshaMessage(message);
        return Result.success(0);//排队中,前端需要进行轮询请求结果


        /*//判断商品库存
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount()<=0){
            return Result.error(CodeMsg.STOCK_NOT_ENOUGH);
        }
        //判断是否已经秒杀成功过
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if (order!=null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //可以进行秒杀
        //1.减库存,2.下订单,3.写入秒杀,事务
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);

        //返回订单和商品信息
        MiaoshaOrderGoodsVo miaoshaOrderGoodsVo = new MiaoshaOrderGoodsVo(orderInfo,goodsVo);
        return Result.success(miaoshaOrderGoodsVo);*/

    }


    //暂时只关注秒杀逻辑,所以直接通过接口传入userId和goodsId
    //TODO,后续使用security完善权限 控制
    @GetMapping("/result")
    public Object miaoshaResult(long userId,long goodsId){
        MiaoShaUser miaoShaUser = userService.getById(userId);
        /**
         * orderId:成功
         * -1:秒杀失败
         * 0:还在排队中
         */
        long result = miaoshaService.getMiaoshaResult(userId,goodsId);
        return Result.success(result);
    }

    /**
     * 当前controller初始化时会回调当前的方法
     * 可以作为初始化的一些配置方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos==null){
            return;
        }
        //系统启动时加载商品的库存
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
        }
    }
}
