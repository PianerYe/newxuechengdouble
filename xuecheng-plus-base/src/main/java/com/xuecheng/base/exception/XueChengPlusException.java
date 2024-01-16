package com.xuecheng.base.exception;

/**
 * Date:2023/7/5
 * Author: Yepianer
 * Description: 自定义异常处理器
 */
public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public XueChengPlusException() {
    }
    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }
}
