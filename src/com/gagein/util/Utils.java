package com.gagein.util;

public abstract class Utils {
	
	/**
	 * 邮箱正则表达式
	 */
	public static final String regular_email = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
	
	/**
	 * 密码正则表达式
	 */
	public static final String regular_password = "[^\\n\\s]{6,20}";
	
	/**
	 * 电话正则表达式
	 */
	public static final String regular_phone = "[0-9]{8,14}";
	
	/**
	 * 数字正则表达式
	 */
	public static final String regular_numeric = "[0-9]+";
	
	/**
	 * 字母正则表达式
	 */
	public static final String regular_character = "[A-Z0-9a-z]+";
	
	/**
	 * 特殊字母正则表达式
	 */
	public static final String regular_special_char = "[A-Z0-9a-z._%+-@]+";
	
	/**
	 * 首字母大写正则表达式
	 */
	public static final String regular_first_char_upper = "[A-Z]+";
	
	/**
	 * 网址正则表达式
	 */
	public static final String regular_url1 = "(http|https)://((\\w)*|([0-9]*)|([-|_])*)+([\\.|/]((\\w)*|([0-9]*)|([-|_])*))+"; 
	public static final String regular_url2 = "((\\w)*|([0-9]*)|([-|_])*)+([\\.|/]((\\w)*|([0-9]*)|([-|_])*))+"; 
	
}
