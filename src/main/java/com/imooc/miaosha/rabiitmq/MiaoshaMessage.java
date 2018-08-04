package com.imooc.miaosha.rabiitmq;

import com.imooc.miaosha.domain.MiaoShaUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiaoshaMessage {
    private MiaoShaUser user;
    private long goodsId;
}
