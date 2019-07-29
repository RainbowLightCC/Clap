package cn.funnymc.login;

import java.io.*;
import java.sql.*;

/**
 * 访问本地MySQL/lamp
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
				} catch (IOException e1) {}
			}
		}
	}
	/**
	 * 连接数据库
	 */
	public static void getConn() {
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 结束，关闭连接
	 */
	public static void end() {
		try{
			if(conn!=null) conn.close();
		}catch(SQLException se){
			se.printStackTrace();
		}
	}
	/**
	 * 执行一个查询语句
	 * @param sql SQL语句
	 * @return 查询结果ResultSet
	 */
	public static ResultSet query(String sql) {
		System.out.println("MySQL Query: "+sql);
		ResultSet rs = null;
		Statement stmt = null;
		//检查连接过期
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
	 * 执行一个更新语句
	 * @param sql SQL语句
	 * @return 查询结果ResultSet
	 */
	public static void update(String sql) {
		System.out.println("MySQL Update: "+sql);
		Statement stmt = null;
		//检查连接过期
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
