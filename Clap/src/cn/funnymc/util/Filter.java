package cn.funnymc.util;

public class Filter {
	public static String filter(String str) {
		return str
				.replace("\\", "\\\\")
				.replace("\'","\\\'")
				.replace("\"","\\\"");
	}
}
