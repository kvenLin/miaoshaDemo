package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabiitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    /*@RequestMapping("/mq/header")
    public String header(){
        mqSender.sendHeader("hello imooc!!!");
        return "hello";
    }

    @RequestMapping("/mq/fanout")
    public String fanout(){
        mqSender.sendFanout("hello imooc!!!");
        return "hello";
    }


    @RequestMapping("/mq/topic")
    public String topic(){
        mqSender.sendTopic("hello imooc!!!");
        return "hello";
    }

    @RequestMapping("/mq")
    public String mq(){
        mqSender.send("hello imooc!!!");
        return "hello";
    }
*/
    @RequestMapping("/db/get")
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }
    @RequestMapping("/db/tx")
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    public Object redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    public Object redisSet(){
        User user = new User(1,"1111");
        boolean b = redisService.set(UserKey.getById,""+1,user);
        return Result.success(b);
    }


}

