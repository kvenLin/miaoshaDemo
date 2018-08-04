package com.imooc.miaosha.rabiitmq;

import com.imooc.miaosha.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//发送者
@Service
@Slf4j
public class MQSender {

    //操作queue的工具类
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 四种模式的学习
     * @param message
     */
    /*public void send(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send msg:"+msg);
        //指定发送到哪个queue
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }
    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send topic msg:"+msg);
        //指定发送到哪个queue
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }

    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send fanout msg:"+msg);
        //不指定key,但是需要使用""将参数填充
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }

    public void sendHeader(Object message){
        String msg = RedisService.beanToString(message);
        log.info("send header msg:"+msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }*/


    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String msg = RedisService.beanToString(message);
        log.info("send msg:"+msg);
        //指定发送到哪个queue
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }
}
