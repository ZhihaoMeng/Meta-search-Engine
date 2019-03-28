package db.dbhelpler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import agent.data.inblackboard.MergeCommonData;
import db.jdbc.DatabaseOperate;

/**
 * 最终成员搜索引擎偏好类
 * @author elaine
 *
 */
public class ClickSourcePreferenceHelper {

	private String table_name = "click_source_preference";
	private ArrayList<Double> preferenceList = new ArrayList<Double>();
	
	/**
	 * 更新指定id用户的偏好度
	 * 偏好度计算公式：a*e^(-b*(allCounts))+ baiduCount/allCounts;
	 * @param userid
	 * @param countList
	 */
	public void UpdateUserPreference(int userid, ArrayList<Integer> countList)
	{
		double baiduCount = countList.get(0);
		double sogouCount = countList.get(1);
		double youdaoCount = countList.get(2);
		double bingCount = countList.get(3);
		double yahooCount = countList.get(4);
		double allCounts = baiduCount + sogouCount + youdaoCount + bingCount + yahooCount;
		//double initValue = 500*Math.pow(Math.E, -(2*allCounts));
		double baiduPrefer = 0;
		double sogouPrefer = 0;
		double youdaoPrefer = 0;
		double bingPrefer = 0;
		double yahooPrefer = 0;
		
		if(allCounts != 0)
		{
			baiduPrefer = baiduCount/allCounts;
			sogouPrefer = sogouCount/allCounts;
			youdaoPrefer = youdaoCount/allCounts;
			bingPrefer = bingCount/allCounts;
			yahooPrefer = yahooCount/allCounts;
		}
		
		//得到最终的权重结果
		/*MergeCommonData instance = MergeCommonData.getInstance();
		instance.setBaiduWeight(initValue + baiduPrefer);
		instance.setBingWeight(initValue + bingPrefer);
		instance.setSogouWeight(initValue + sogouPrefer);
		instance.setYahooWeight(initValue + yahooPrefer);
		instance.setYoudaoWeight(initValue + youdaoPrefer);*/
		
		//得到最终结果并存放到数据库中
		Connection conn = null;
		Statement stmt = null;
		int result = -1;
		
		try{
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
			String update_sql = "UPDATE " + table_name 
					+" SET baidu_preference = "+( baiduPrefer)
					+",sogou_preference = "+(sogouPrefer)
					+",youdao_preference = "+( youdaoPrefer)
					+",bing_preference = "+( bingPrefer)
					+",yahoo_preference = "+( yahooPrefer)
					+" WHERE userid = "+ userid;
			result  = stmt.executeUpdate(update_sql);
			
			if (result == 0) {
				String insert_sql = "insert into " + table_name + " (userid,baidu_preference,sogou_preference,youdao_preference,bing_preference,yahoo_preference) "
						+" values ("+ userid
						+","
						+baiduPrefer
						+","
						+sogouPrefer
						+","
						+youdaoPrefer
						+","
						+bingPrefer
						+","
						+yahooPrefer
						+")";
				System.out.println("ClickSourcePreferenceHelper insert_sql: "+insert_sql);
				stmt.executeUpdate(insert_sql);
			}
			
		}catch(SQLException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);	
		}
	}
	
	/**
	 * 查询指定用户对各成员搜索引擎的偏好度
	 * @param userid
	 * @return 返回的列表存储的内容依次为：baiduPrefer sogouPrefer youdaoPrefer bingPrefer yahooPrefer
	 */
	public ArrayList<Double> SelectUserPreference(int userid)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;		
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
			String select_sql = "select * from "+ table_name + " where userid = " + userid;
			rs = stmt.executeQuery(select_sql);
			if (rs.next()) {
				ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
				 int columnCount = md.getColumnCount();
				 
				 //注意这里要从id和userid之后的列开始
				 for(int i = 3; i <= columnCount; i++)
				 {
					 preferenceList.add(rs.getDouble(i));
				 }				 	
			}
			else preferenceList = null;
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return preferenceList;
	}
}
