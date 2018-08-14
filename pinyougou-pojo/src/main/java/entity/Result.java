package entity;

import java.io.Serializable;

/**
 * @author 三国的包子
 * @version 1.0
 * @description 描述
 * @title 标题
 * @package entity
 */
public class Result implements Serializable {

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    private boolean success;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
