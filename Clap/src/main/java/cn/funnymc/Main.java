package cn.funnymc;

import java.net.UnknownHostException;

import cn.funnymc.server.*;
import cn.funnymc.login.*;
/**
 * ������Ϸ�������
 * @author TheFunnyCrafter
 */
public class Main {
	public static void main(String[] args) throws UnknownHostException {
		System.out.println("Starting Clapping Game Server...");
		//��10087�˿�������Clap������
		new Clap(10087).start();
		//��ʼ��MySQL
		MySQL.getPassword();
		MySQL.getConn();
	}
}
