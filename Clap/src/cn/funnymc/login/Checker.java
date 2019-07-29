package cn.funnymc.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.funnymc.login.MySQL;
import cn.funnymc.util.Filter;
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
		try {
			ResultSet rs=MySQL.query(sql);
			if(rs.next()) {
				return rs.getString("name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
