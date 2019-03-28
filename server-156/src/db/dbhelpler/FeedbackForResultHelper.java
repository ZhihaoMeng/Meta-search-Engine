package db.dbhelpler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.jdbc.DatabaseOperate;
/**
 * 
 * @author wenyuanliu
 * @date 20160525
 */
public class FeedbackForResultHelper {
	private String table_name = "feedback_for_result";
	
	/**
	 * 将用户点击记录插入数据表 
	 * @param userid 登录用户的id
	 * @param query 点击的查询词
	 * @param url 点击的url
	 * @param rank 点击的该条结果在结果列表中的排名
	 * @param result_order 用户点击的次序
	 * @param rank_method 当前情况下所使用的排序算法
	 */
	public void insertClickRecord(int userid, String query, String url, int rank, String rank_method, String date)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int click_order = 0;
				
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//统计click_order的条目数
		String select_sql = "select count(click_order) from " + table_name 
				+ " where userid = " 
				+ userid 
				+ " and query = '" 
				+ query 
				+ "' and rank_method = '"
				+ rank_method 
				+"'";
		try {
			rs = stmt.executeQuery(select_sql);
			System.out.println("feedback_for_result select_sql: "+ select_sql);
			if (rs.next()) {
				//得到当前对用户点击次序的数量，然后自增1之后与新的数据插入到数据表中
				click_order = rs.getInt(1);
				click_order++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String insert_sql = "insert into "+ table_name +" (userid,query,url,rank,click_order,rank_method,date) values("
				+ userid
				+ ",'"				
				+ query
				+ "','"
				+ url
				+ "'," 
				+ rank
				+","
				+click_order
				+",'"
				+rank_method
				+"','"
				+ date
				+"')";
		System.out.println("feedback_for_result insert_sql: "+insert_sql);
		try {
			stmt.executeUpdate(insert_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);	
		}
	}
	
	/**
	 * 更新用户对于某一算法的偏好
	 * @param userid
	 * @param prefer_method
	 */
	public void updatePreferMethod(int userid, String prefer_method)
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
		
		String update_sql = "update " + table_name + " set prefer_method = '" + prefer_method + "' where useid = "+ userid;
		System.out.println("update_sql: "+update_sql);
		try {
			stmt.executeUpdate(update_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		
	}
	
	/**
	 * 查询用户的偏好算法并返回
	 * @param userid
	 * @return
	 */
	public String queryPreferMethod(int userid)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String preferMethod = null;		
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String select_sql = "select prefer_method from "+ table_name + " where userid = "+userid;
		System.out.println("queryPreferMethod select_sql : "+select_sql);
		try {
			rs = stmt.executeQuery(select_sql);
			if (rs.next()) {
				preferMethod = rs.getString("prefer_method");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		return preferMethod;
	}
	/**
	 * 查询当前这种算法下指定用户点击了多少条目
	 * @param userid 用户名
	 * @param rank_method 指定的排名算法
	 * @return 返回该用户使用该算法点击的记录数
	 */
	public int queryClickRecord(int userid, String rank_method)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int result = 0;
				
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String select_sql = "select count(*) from " + table_name + " where userid = " + userid +" and rank_method = '" + rank_method + "' ";
		
		try {
			rs = stmt.executeQuery(select_sql);
			if(rs.next())
			{	
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		
		return result;
	}
	
	
	/**
	 * 返回指定的排序算法下的 用户点击条目位置列表 和 次序列表
	 * @param userid 用户id
	 * @return 两个列表
	 */
	public List<ArrayList<Integer>> queryRankAndClickOrder(int userid)
	{
		//ArrayList<Integer>[] resultList = new ArrayList[2];
		//resultList[0] = new ArrayList<Integer>();
		//resultList[1] = new ArrayList<Integer>();
		List<ArrayList<Integer>> resultList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> rankList = null;
		ArrayList<Integer> clickOrderList = null;
		
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
		
		String select_sql = "select rank, click_order from " + table_name 
				+ " where userid = " 
				+ userid
				+ " group by rank_method "
				+" order by rank asc";
		
		try {
			rs = stmt.executeQuery(select_sql);
			while(rs.next())
			{
				int rank = rs.getInt("rank");
				int click_order = rs.getInt("click_order");
				rankList = new ArrayList<Integer>();
				clickOrderList = new ArrayList<Integer>();
				rankList.add(rank);
				clickOrderList.add(click_order);
			}
			resultList.add(rankList);
			resultList.add(clickOrderList);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	/**
	 * 返回指定的排序算法下的 用户点击条目位置列表 和 次序列表
	 * @param userid 用户id
	 * @return 一个列表
	 */
	public List<Integer> queryRank(int userid, String rank_method)
	{
		ArrayList<Integer> rankList = null;
		
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
		
		String select_sql = "select rank from " + table_name 
				+ " where userid = " 
				+ userid
				+" order by rank asc";
		
		try {
			rs = stmt.executeQuery(select_sql);
			while(rs.next())
			{
				int rank = rs.getInt("rank");
				rankList = new ArrayList<Integer>();
				rankList.add(rank);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rankList;
	}
}
