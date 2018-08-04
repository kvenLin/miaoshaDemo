package com.imooc.miaosha.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailInfo {
    private GoodsVo goodsVo;
    private Integer miaoshaStatus;
    private Integer remainSeconds;
}
