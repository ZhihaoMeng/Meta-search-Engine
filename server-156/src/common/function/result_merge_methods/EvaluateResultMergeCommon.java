package common.function.result_merge_methods;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import common.entities.searchresult.Result;
import db.jdbc.DatabaseOperate;

/**
 * 数据库相关类，用于测试某一基础算法和新算法各自搜索的结果
 * @author lwy
 *
 */
public class EvaluateResultMergeCommon {
	
	String tableName = "test_set2";
	public int getRelevantScore(String url, String query,int result_location, double overlap_rate, double overlap_one){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int relevant_level = 0;
		try {
			conn = DatabaseOperate.getConnection();
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String select_sql = "select revelant_level from click_log_test where query='"+ query +"' and url='" + url +"'";
		//System.out.println("getRelevantScore select_sql: "+select_sql);
		
		try {
			rs = stmt.executeQuery(select_sql);
			if(rs.next()){
				relevant_level = rs.getInt(1);				
			}
			String insert_sql = "insert into " + tableName 
					+" (result_method,relevant_level,result_location,query,url,overlap_rate,overlap_one) "
					+ " values ('SDM0.2',"+ relevant_level +"," + result_location + ",'" + query +"','"+url+"'," 
					+ overlap_rate + "," + overlap_one +")";
			int num = stmt.executeUpdate(insert_sql);
			//if(num > 0) System.out.println("成功插入" + num +"条数据！");
			//else System.out.println("插入数据失败");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
		}
		//System.out.println("relevant_level---->"+ relevant_level);
		return relevant_level;
	}
	/**
	 * 新算法下的结果在人工标注中的相关度分值列表
	 * @param targetList 测试的前10/20/30条结果
	 * @param query 查询词
	 * @return list 返回的得分列表
	 */
	@SuppressWarnings("finally")
	public List<Integer> getEvaluateScore(List<Result> targetList, String query){
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
		HashMap<String, Integer> notatedMap = new HashMap<String, Integer>();
		List<Integer> ret = new ArrayList<Integer>();
		
		String select_sql = "select distinct url, revelant_level from click_log_test where query ='" + query +"'";
		System.out.println("getEvaluateScore select_sql: "+select_sql);
		try {
			rs = stmt.executeQuery(select_sql);						
			while(rs.next()){
				String url = rs.getString("url");
				int revelant_level = rs.getInt("revelant_level");
				notatedMap.put(url.trim(), revelant_level);				
			}
			for(int i = 0; i < targetList.size(); i++){
				for(Iterator<Entry<String, Integer>> iterator = notatedMap.entrySet().iterator(); iterator.hasNext();){
					Entry<String, Integer> entry = iterator.next();
					if(targetList.get(i).getLink().trim().equals(entry.getKey())){
						ret.add(entry.getValue());
					}else{
						if(i < 10)
							ret.add(3);
						else if(i >= 10 && i < 20)
							ret.add(2);
						else if(i >= 20)
							ret.add(1);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DatabaseOperate.closeResult(rs);
			DatabaseOperate.closeState(stmt);
			DatabaseOperate.closeConn(conn);
			for (int i = 0; i < ret.size(); i++) {
				System.out.print(ret.get(i));
			}
			System.out.println(" ");
			return ret;
		}
	}
}
