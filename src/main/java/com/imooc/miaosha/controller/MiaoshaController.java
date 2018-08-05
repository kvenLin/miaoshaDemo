package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabiitmq.MQSender;
import com.imooc.miaosha.rabiitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.MiaoshaOrderGoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/miaosha")
@RestController
@Slf4j
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

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    //暂时只关注秒杀逻辑,所以直接通过接口传入userId和goodsId
    //TODO,后续使用security完善权限控制
    @RequestMapping("/{path}/do_miaosha")
    public Object doMiaosha(long userId,long goodsId,@PathVariable("path") String path){
        //验证path
        if (!miaoshaService.checkPath(userId,goodsId,path)) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        MiaoShaUser user = userService.getById(userId);

        //使用内存的标记来减少redis的访问,判断库存是否不足
        boolean isOver = localOverMap.get(goodsId);
        if (isOver){
            return Result.error(CodeMsg.STOCK_NOT_ENOUGH);
        }

        //判断是否已经秒杀成功过
        MiaoshaOrder order = redisService.get(OrderKey.getMiaoshaOrderByUidGid,userId+"_"+goodsId,MiaoshaOrder.class);
        if (order!=null){
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        //预减库存,访问redis
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if (stock<0){
            localOverMap.put(goodsId,true);
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
    @RequestMapping("/result")
    public Object miaoshaResult(long userId,long goodsId){
        /**
         * orderId:成功
         * -1:秒杀失败
         * 0:还在排队中
         */
        long result = miaoshaService.getMiaoshaResult(userId,goodsId);
        return Result.success(result);
    }

    //暂时只关注秒杀逻辑,所以直接通过接口传入userId和goodsId
    //TODO,后续使用security完善权限 控制
    @RequestMapping("/path")//得到秒杀的接口
    public Object getMiaoshaPath(int verifyCode,long userId,long goodsId){

        boolean check = miaoshaService.checkVerifyCode(verifyCode,userId,goodsId);
        if (!check){
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }

        String path = miaoshaService.createPath(userId,goodsId);
        return Result.success(path);
    }

    @RequestMapping("/verifyCode")
    public Object getMiaoshaVerifyCode(long userId, long goodsId, HttpServletResponse response){
        BufferedImage image = miaoshaService.createMiaoshaVerifyCode(userId,goodsId);
        try {
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
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
        //启动服务时进行自动的清空redis缓存
//        redisService.flush();

        //系统启动时加载商品的库存
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }
}
