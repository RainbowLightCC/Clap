package cn.funnymc;

import java.net.UnknownHostException;

import cn.funnymc.server.*;
import cn.funnymc.login.*;
/**
 * SocketChat 程序入口
 * @author lcj
 */
public class Main {
	public static void main(String[] args) throws UnknownHostException {
//		test();
		System.out.println("Starting Clapping Game Server...");
		//在10087端口上启动Clap服务器
		new Clap(10087).start();
		//初始化MySQL
		MySQL.getPassword();
		MySQL.getConn();
	}
	
	private static void test() {
		
	}
}
