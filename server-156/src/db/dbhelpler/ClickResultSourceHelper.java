package db.dbhelpler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import db.jdbc.DatabaseOperate;

/**
 *如果数据库有变化或者成员搜索引擎数量增加，则这个数据库帮助类要修改
 * @author elaine
 *
 */
public class ClickResultSourceHelper {

	private String table_name = "click_result_source";
	private static final ArrayList<Integer> countsList = new ArrayList<Integer>();
	private ClickSourcePreferenceHelper preferenceHelper = new ClickSourcePreferenceHelper();
	boolean flag = false;
	
	/**
	 * 查询并统计所有用户的点击来源引擎数目
	 * @return Arraylist<Integer>,返回的结果依次为百度、搜狗、有道、必应、雅虎的点击次数
	 */
	public ArrayList<Integer> queryAllSourceClicks(int userid)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		if (countsList.size() != 0) {
			countsList.clear();
		}

		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//将每个记录中的数值进行加和
		String sql = "SELECT SUM(baidu_count) AS baidu_sum, SUM(sogou_count) AS sogou_sum, "
				+ "SUM(youdao_count) AS youdao_sum, SUM(bing_count) AS bing_sum, SUM(yahoo_count) AS yahoo_sum"
				+ " FROM " + table_name +" where userid = " + userid;
		try {
			rs = stmt.executeQuery(sql);															
			while(rs.next())
			{	
				ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
				int columnCount = md.getColumnCount();
				//结果表格如下： baidu_sum sogou_sum youdao_sum bing_sum yahoo_sum
				for (int i = 1; i <= columnCount; i++) {
					countsList.add(rs.getInt(i));
				}	
				preferenceHelper.UpdateUserPreference(userid, countsList);
			}
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		return countsList;
	}
	
	/**
	 * 更新源点击
	 * @param userid
	 * @param baiduCount
	 * @param sogouCount
	 * @param youdaoCount
	 * @param bingCount
	 * @param yahooCount
	 * @param query
	 * @param url
	 * @param abstr
	 */
	@SuppressWarnings("resource")
	public void UpdateSourceClick(int userid, int baiduCount, int sogouCount, int youdaoCount, int bingCount, int yahooCount,
			String query, String url, String abstr, String date)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {			
			String select_sql2 = "SELECT * FROM "+ table_name +" WHERE userid =" + userid + " and url ='" + url + "' and abstr ='"+ abstr +"' ";
			System.out.println("select_sql: "+select_sql2);
			rs = stmt.executeQuery(select_sql2);
			if(rs.next())
			{
			
				//query = rs.getString("query");		
				
				int value = rs.getInt("baidu_count");
				baiduCount = baiduCount + value;				
								
				value = rs.getInt("sogou_count");
				sogouCount = sogouCount + value;				

				value = rs.getInt("youdao_count");
				youdaoCount = youdaoCount + value;
			
				value = rs.getInt("bing_count");
				bingCount = bingCount + value;			

				value = rs.getInt("yahoo_count");
				yahooCount = yahooCount + value;
				
				String update_sql = "UPDATE "+ table_name +
						" SET baidu_count = " + baiduCount
						+ ",sogou_count = " + sogouCount 
						+ ",youdao_count = "+ youdaoCount 
						+",bing_count = "+ bingCount 
						+",yahoo_count = " + yahooCount
						+ " where userid = " + userid
						+ " and url ='" + url + "' and abstr ='" + abstr + "' and date ='"+ date +"'" ;
				stmt.executeUpdate(update_sql);
			}
			else{
				String insert_sql = "insert into "+ table_name +" (userid,baidu_count,sogou_count,youdao_count,bing_count,yahoo_count,query,url,abstr,date) values("
						+ userid
						+ ","
						+ baiduCount
						+ ","
						+ sogouCount
						+ ","
						+ youdaoCount
						+ ","
						+ bingCount
						+ ","
						+ yahooCount
						+ ",'"
						+ query
						+ "','"
						+ url
						+ "','" 
						+ abstr
						+"','"
						+ date
						+"')";
				System.out.println("insert_sql: "+insert_sql);
				stmt.executeUpdate(insert_sql);
			}			
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);			
		}
	}
	
}
