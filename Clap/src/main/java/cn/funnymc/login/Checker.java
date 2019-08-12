package cn.funnymc.login;

import cn.funnymc.util.Filter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * 连接MySQL进行登陆状态/用户检查
 * @author FunnyCrafter
 */
public class Checker {
	/**
	 * 检查token，返回用户名
	 * 
	 * ***可能为null！
	 * @param token:从/login/login_token.php获得的token
	 * @return id or null if token is invalid
	 */
	public static String checkToken(String token) {
		token=Filter.filter(token);
		String sql="SELECT * FROM `token` where token='"+token+"'";
		System.out.println("MySQL Query: "+sql);
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn=MySQL.getConn();
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				return rs.getString("name");
			}
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
		return null;
	}
}
