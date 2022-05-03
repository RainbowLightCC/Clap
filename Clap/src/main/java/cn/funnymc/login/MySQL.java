package cn.funnymc.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * ���ʱ���MySQL/lamp
 * @author lcj
 */
public class MySQL {
	static private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	static private final String DB_URL = "jdbc:mysql://localhost:3306/lamp?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	static private final String USER = "root";
	static private final String PASSFILE="/root/MYSQL";
	static private String PASS;
	static private Connection conn=null;
	public static void getPassword() {
		File file = new File(PASSFILE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			PASS=reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored) {}
			}
		}
	}
	/**
	 * �������ݿ�
	 */
	public static Connection getConn() {
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * �������ر�����
	 */
	public static void end() {
		try{
			if(conn!=null) conn.close();
		}catch(SQLException se){
			se.printStackTrace();
		}
	}
	/**
	 * ִ��һ����ѯ���
	 * @param sql SQL���
	 * @return ��ѯ���ResultSet
	 */
	public static ResultSet query(String sql) {
//		System.out.println("MySQL Query: "+sql);
		ResultSet rs = null;
		Statement stmt = null;
		//������ӹ���
		try {conn.getNetworkTimeout();}
		catch (SQLException e) {
			getConn();
		}
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(stmt!=null) stmt.close();
			}catch(SQLException se2){}
			try{
				if(conn!=null) conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
		return rs;
	}
	/**
	 * ִ��һ���������
	 * @param sql SQL���
	 * @return ��ѯ���ResultSet
	 */
	public static void update(String sql) {
		System.out.println("MySQL Update: "+sql);
		Statement stmt = null;
		//������ӹ���
		try {conn.getNetworkTimeout();}
		catch (SQLException e) {
			getConn();
		}
		try{
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(stmt!=null) stmt.close();
			}catch(SQLException se2){}
			try{
				if(conn!=null) conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}
}
