package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request
            , HttpServletResponse response
            ,Exception e){
        //修复bug时可以进行打印exception
        e.printStackTrace();
        //处理自定义的业务异常
        if (e instanceof GlobalException){
            GlobalException globalException = (GlobalException) e;
            return Result.error(globalException.getCodeMsg());
        }

        if (e instanceof BindException){
            BindException exception = (BindException) e;
            List<ObjectError> allErrors = exception.getAllErrors();
            String message = null;
            //TODO,注:枚举创建之后是不能被修改的
            for (ObjectError error : allErrors) {
                if (message!=null){
                    message = message+","+error.getDefaultMessage();
                }else {
                    message = error.getDefaultMessage();
                }
            }
            log.error(message);
            Result<String> enumResult = Result.error(CodeMsg.BIND_ERROR);
            enumResult.setMsg(String.format(enumResult.getMsg(),message));
            return enumResult;
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }
}
