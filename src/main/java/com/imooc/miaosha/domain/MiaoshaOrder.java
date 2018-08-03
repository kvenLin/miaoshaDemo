package com.imooc.miaosha.domain;

import lombok.Data;

@Data
public class MiaoshaOrder {
    private Long id;
    private Long userId;
    private Integer orderId;
    private Long goodsId;
}
