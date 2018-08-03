package com.imooc.miaosha.vo;

import com.imooc.miaosha.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    @NotNull(message = "不能为空")
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 6,message = "密码长度不能少于6位字符")
    private String password;
}
