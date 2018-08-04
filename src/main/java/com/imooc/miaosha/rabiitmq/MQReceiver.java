package com.imooc.miaosha.rabiitmq;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;

 /*   //指定通过哪个queue来读取数据
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receiver(String message){
        log.info("receive msg:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiverTopic1(String message){
        log.info("receive queue1 msg:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiverTopic2(String message){
        log.info("receive queue2 msg:"+message);
    }

    //headerReceiver
    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void receiverHeaderQueue(byte[] message){
        log.info("receive header msg:"+new String(message));
    }
*/

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiver(String message){
        log.info("receive msg:"+message);
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
        //得到用户信息和商品ID信息
        MiaoShaUser user = miaoshaMessage.getUser();
        long goodsId = miaoshaMessage.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getGoodsStock();
        if (stock<=0){
            return;
        }

        //判断是否已经秒杀成功过
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order!=null){
            return;
        }

        //可以进行秒杀,生成秒杀订单
        //1.减库存,2.下订单,3.写入秒杀,事务
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
    }

}
