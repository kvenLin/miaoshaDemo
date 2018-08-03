package com.imooc.miaosha.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeMsg {

    PASSWORD_CAN_NOT_NULL(0,"密码不能为空"),
    SUCCESS(200,"成功"),
    SERVER_ERROR(500,"服务器异常"),
    MOBILE_NOT_EXIST(405,"手机号不存在"),
    PASSWORD_ERROR(501,"密码错误"),
    BIND_ERROR(502,"参数检验错误: %s"),

    ;
    private Integer code;
    private String msg;

}
