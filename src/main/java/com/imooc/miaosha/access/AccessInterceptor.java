package com.imooc.miaosha.access;

import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取用户并保存到ThreadLocal中
//            UserContext.setUser(user);
        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit==null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            //TODO,判断用户是否需要登录
//            if(user==null){
//                WebUtil.render(response,CodeMsg.USER_NOT_LOGIN);
//                return false;
//            }

            //用户已经登录则获取其Id组成对应的key
//            key += "_"+user.getId();

            //TODO,根据当前用户的key查询redis进行防刷的逻辑
//            AccessKey accessKey = AccessKey.withExpire(seconds);
//            Integer count = redisService.get(accessKey, key, Integer.class);
//            if (count==null){
//                redisService.set(accessKey,key,1);
//            }else if (count<maxCount){
//                redisService.incr(AccessKey.access,key);
//            }else {
//                WebUtil.render(response,CodeMsg.ACCESS_LIMIT);
//                return false;
//            }
        }
        return true;
    }
}
