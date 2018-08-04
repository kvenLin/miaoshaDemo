package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {
    private OrderInfo orderInfo;
    private GoodsVo goodsVo;
}
