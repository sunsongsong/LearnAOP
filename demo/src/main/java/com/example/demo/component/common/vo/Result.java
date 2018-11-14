package com.example.demo.component.common.vo;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = -2487074053808281342L;

	/**
	 * 0正常 其他 错误
	 */
	private Integer code;

	private String message;

	private Object result;

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Result(Integer code, String message, Object result) {
		this.code = code;
		this.message = message;
		this.result = result;
	}

}
