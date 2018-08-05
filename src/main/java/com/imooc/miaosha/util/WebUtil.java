package com.imooc.miaosha.util;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class WebUtil {
    public static void render(HttpServletResponse response,CodeMsg codeMsg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String s = JSON.toJSONString(Result.error(codeMsg));
        out.write(s.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
