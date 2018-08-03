package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoShaUserDao;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {

    private static final String COOKIE_NAME_TOKEN="token";
    @Autowired
    private MiaoShaUserDao miaoShaUserDao;
    @Autowired
    private RedisService redisService;

    public MiaoShaUser getById(long id){
        return miaoShaUserDao.getById(id);
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if (loginVo==null){
            //业务异常
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        //TODO,一般应该由前端进行第一次的加密传输,后端进行二次加密后进行校验
        String formPass = MD5Util.inputPassToFormPass(loginVo.getPassword());
        //判断是否存在手机号
        MiaoShaUser miaoShaUser = getById(Long.parseLong(mobile));
        if (miaoShaUser==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoShaUser.getPassword();
        String salt = miaoShaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,salt);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token,token,miaoShaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }
}
