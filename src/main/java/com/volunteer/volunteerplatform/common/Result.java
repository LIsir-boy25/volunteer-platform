package com.volunteer.volunteerplatform.common;

import lombok.Data;

@Data
public class Result<T> {
    private String code; // 状态码
    private String msg;  // 提示信息
    private T data;      // 返回数据

    // --- 修复点：添加这个不带参数的方法 ---
    public static <T> Result<T> success() {
        return success(null);
    }
    // ------------------------------------

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 默认的 500 服务器错误
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }

    // 【核心新增】支持自定义状态码的错误返回（用于配合前端的 401 拦截）
    public static <T> Result<T> error(String code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}