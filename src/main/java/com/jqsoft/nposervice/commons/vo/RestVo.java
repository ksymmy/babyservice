package com.jqsoft.nposervice.commons.vo;

import com.jqsoft.nposervice.commons.constant.ResultMsg;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;

@Data
public class RestVo<T> implements Serializable {

	private static final long serialVersionUID = -5668554152671000203L;

	/**
	 * 应用返回编码
	 */
	private String code;

	/**
	 * 应用返回消息
	 */
	private String message;

	/**
	 * 应用返回结果
	 */
	private T data;

	private boolean success = true;

	public RestVo(){
		ResultMsg result = ResultMsg.SUCCESS;
		this.code = result.getCode();
		this.message = result.getName();
	}

	public String getCode() {
		return code;
	}

	private void setCode(String code) {
		this.code = code;
		if(StringUtils.isBlank(this.code) || !ResultMsg.SUCCESS.getCode().equals(this.code)){
			this.success = false;
		}
	}

	public void setCode(ResultMsg result) {
		this.code = result.getCode();
		this.message = result.getName();
		if (StringUtils.isBlank(this.code) || !ResultMsg.SUCCESS.getCode().equals(this.code)) {
			this.success = false;
		}
	}

	private void setAppResult(ResultMsg result) {
		this.code = result.getCode();
		this.message = result.getName();
		if(StringUtils.isBlank(this.code) || !ResultMsg.SUCCESS.getCode().equals(this.code)){
			this.success = false;
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}


	public static RestVo SUCCESS(){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setAppResult(ResultMsg.SUCCESS);
		return restVo;
	}

	public static RestVo SUCCESS(Object obj){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setAppResult(ResultMsg.SUCCESS);
		restVo.setData(obj);
		return restVo;
	}

	public static RestVo FAIL(){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setAppResult(ResultMsg.FAIL);
		return restVo;
	}

	public static RestVo FAIL(String msg){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setAppResult(ResultMsg.FAIL);
		restVo.setMessage(msg);
		return restVo;
	}

	public static RestVo FAIL(ResultMsg result){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setAppResult(result);
		return restVo;
	}

    public static RestVo ERROR(){
        RestVo<Object> restVo = new RestVo<>();
        restVo.setAppResult(ResultMsg.ERROR);
        return restVo;
    }

	public static RestVo FAIL(String code, String msg){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setCode(code);
		restVo.setMessage(msg);
		restVo.setSuccess(false);
		return restVo;
	}

	public static RestVo FAIL(ResultMsg result, String msg){
		RestVo<Object> restVo = new RestVo<>();
		restVo.setCode(result.getCode());
		restVo.setMessage(msg);
		return restVo;
	}

	public boolean isSuccess(){
		return success;
	}
}
