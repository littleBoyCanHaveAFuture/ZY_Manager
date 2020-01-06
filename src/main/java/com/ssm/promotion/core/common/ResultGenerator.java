package com.ssm.promotion.core.common;

/**
 * 响应结果生成工具
 *
 * @author Administrator
 */
public class ResultGenerator {
    public static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    public static final String DEFAULT_FAIL_MESSAGE = "FAIL";

    public static Result genSuccessResult() {
        Result result = new Result();
        result.setResultCode(Constants.RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    public static Result genSuccessResult(Object data) {
        Result result = new Result();
        result.setResultCode(Constants.RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static Result genFailResult(String message) {
        Result result = new Result();
        result.setResultCode(Constants.RESULT_CODE_SERVER_ERROR);
        if (message == null || message.length() < 1) {
            message = DEFAULT_FAIL_MESSAGE;
        }
        result.setMessage(message);
        return result;
    }

    public static Result genRelogin() {
        Result result = new Result();
        result.setResultCode(Constants.RESULT_CODE_SERVER_RELOGIN);
        return result;
    }

    public static Result genFailResults(String message) {
        Result result = new Result();
        result.setResultCode(Constants.RESULT_CODE_FAIL);
        if (message == null || message.length() < 1) {
            message = DEFAULT_FAIL_MESSAGE;
        }
        result.setMessage(message);
        return result;
    }
}
