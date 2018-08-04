package com.imooc.miaosha.vo;

import com.imooc.miaosha.domain.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MiaoshaOrderGoodsVo {
    private OrderInfo orderInfo;
    private GoodsVo goodsVo;
}
