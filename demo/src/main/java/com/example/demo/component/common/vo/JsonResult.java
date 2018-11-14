package com.example.demo.component.common.vo;


import com.example.demo.component.common.enums.ResultCodeEnum;
import com.google.gson.Gson;

public class JsonResult {

	public static String fillResult(Integer code, String message, Object result) {
		Gson gson = new Gson();
		Result resultModel = new Result(code, message, result);
		return gson.toJson(resultModel);
	}

	public static String fillEnumResult(ResultCodeEnum codeEnum, Object result) {
		Gson gson = new Gson();
		Result resultModel = new Result(codeEnum.getType(), codeEnum.getMessage(), result);
		return gson.toJson(resultModel);
	}

	/**
	 * 默认正确结果
	 * 
	 * @return
	 */
	public static String okResult(Object result) {
		return JsonResult.fillEnumResult(ResultCodeEnum.SUCCESS_CODE, result);
	}

	/**
	 * 错误结果
	 * 
	 * @param code
	 * @param message
	 * @return
	 */
	public static String errorResult(Integer code, String message) {
		return JsonResult.fillResult(code, message, null);
	}

	/**
	 * 错误结果 内容
	 * @param message
	 * @return
	 */
	public static String errorMsgResult(String message) {
		return errorResult(ResultCodeEnum.ERROR_CODE_DEFAULT.getType(),message);
	}

	/**
	 * 错误返回值
	 * @param codeEnum
	 * @return
	 */
	public static String errorCodeEnumResult(ResultCodeEnum codeEnum) {
		return JsonResult.fillEnumResult(codeEnum, null);
	}

	/**
	 * 默认错误结果
	 * 
	 * @return
	 */
	public static String errorDefaultResult() {
		return JsonResult.errorCodeEnumResult(ResultCodeEnum.ERROR_CODE_DEFAULT);
	}
}
