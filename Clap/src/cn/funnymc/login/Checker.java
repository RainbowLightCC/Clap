package cn.funnymc.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.funnymc.login.MySQL;
import cn.funnymc.util.Filter;
/**
 * ����MySQL���е�½״̬/�û����
 * @author FunnyCrafter
 */
public class Checker {
	/**
	 * ���token�������û���
	 * 
	 * ***����Ϊnull��
	 * @param token:��/login/login_token.php��õ�token
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
