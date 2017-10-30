package com.fh.exception;

/**
 * 服务异常类
 *
 * @author xx.liu
 * @version 1.0
 */
public class ServiceException extends RuntimeException
{
    /**
     * int类型的错误代码，不能为0，0表示无错误
     */
    protected int errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    /**
     * 使用错误信息和错误代码构建错误异常
     * @param errorCode 错误代码
     * @param errorMsg  错误信息
     */
    public ServiceException(int errorCode, String errorMsg )
    {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 使用错误代码和错误信息+原始异常创建异常
     * @param errorCode 错误代码
     * @param errorMsg  错误信息
     * @param cause     原始异常
     */
    public ServiceException(int errorCode, String errorMsg, Throwable cause )
    {
        super( cause );
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString()
    {
        return super.toString() + "[" + errorCode + "]-[" + errorMsg + "]";
    }

    @Override
    public String getMessage()
    {
        return "[" + errorCode + "]-[" + errorMsg + "]";
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }
}
