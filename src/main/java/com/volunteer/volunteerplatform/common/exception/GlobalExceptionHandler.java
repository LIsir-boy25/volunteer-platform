package com.volunteer.volunteerplatform.common.exception;

import com.volunteer.volunteerplatform.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获业务逻辑异常
     * 当 Service 层抛出 throw new RuntimeException("密码错误") 时，这里会接住它
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntime(RuntimeException e) {
        e.printStackTrace(); // 在控制台打印日志，方便你调试
        return Result.error(e.getMessage()); // 把 "密码错误" 传给前端
    }

    /**
     * 捕获数据库重复冲突异常
     * 当你注册一个已经存在的学号时，这里会接住报错
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQL(SQLIntegrityConstraintViolationException e) {
        return Result.error("学号或账号已存在，请检查后重新输入！");
    }

    /**
     * 捕获其他系统级未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统繁忙，请联系管理员");
    }
}