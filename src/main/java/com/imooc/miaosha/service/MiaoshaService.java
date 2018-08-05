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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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

    public boolean checkVerifyCode(int verifyCode, long userId, long goodsId) {
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, userId + "_" + goodsId, Integer.class);
        if (codeOld==null||codeOld-verifyCode!=0){
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode,userId + "_" + goodsId);
        return true;
    }

    public String createPath(long userId, long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,userId+"_"+goodsId,path);
        return path;
    }

    public BufferedImage createMiaoshaVerifyCode(long userId, long goodsId) {
        int with = 80;
        int height = 32;

        //createImage
        BufferedImage image = new BufferedImage(with,height,BufferedImage.TYPE_INT_RGB);//RGB,红绿蓝
        Graphics g = image.createGraphics();
        //set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,with,height);
        //draw the border
        g.setColor(Color.black);
        g.drawRect(0,0,with-1,height-1);
        //create a random instance to generate the codes
        Random random = new Random();
        //make some confusion,生成50个干扰的点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(with);
            int y = random.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        //generate a random code
        String verifyCode = generateVerifyCode(random);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        //计算表达式结果后,把验证码存入redis
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode,userId+"_"+goodsId,rnd);
        //输出图片
        return image;
    }

    /**
     * 计算验证码的表达式
     * @param exp
     * @return
     */
    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (int) engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public static char[] ops = new char[]{'+','-','*'};

    /**
     * 生成验证码的表达式
     * @param random
     * @return
     */
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = ""+num1+op1+num2+op2+num3;
        return exp;
    }
}
