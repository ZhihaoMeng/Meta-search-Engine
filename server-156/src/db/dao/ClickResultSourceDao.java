package db.dao;

import java.util.List;
import java.util.Set;

import server.info.entites.transactionlevel.ClickRecordForResultMergeEntity;

public interface ClickResultSourceDao {

	public int add(ClickRecordForResultMergeEntity log);
	
	//public void delete(int id);
	
	public int update(ClickRecordForResultMergeEntity log);
	
	//public ClickRecordForResultMergeEntity get(int logid);
	
	/**
	 * 从数据库获取用户点击记录，记录中包含结果的来源
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param userid 要查找日志信息的用户的ID
	 */
	public void getLogOfUser(List<ClickRecordForResultMergeEntity> ret,int userid);
	
	/**
	 * 从数据库获取用户点击记录，记录中包含结果的来源
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param uidSet 要查找日志信息的用户ID集合
	 */
	public void getLogOfUser(List<ClickRecordForResultMergeEntity> ret, Set<Integer> uidSet);
	
	/**
	 * 从数据库获取特定的用户点击记录，记录中包含结果的来源
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param uidSet 要查找日志信息的用户ID集合
	 * @param query 查询词
	 */
	public void getLogOfUser(List<ClickRecordForResultMergeEntity> ret, Set<Integer> uidSet, String query);
	
	/**
	 * 从数据库获取用户点击记录，记录中包含结果的来源
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param userid 用户ID
	 * @param query 查询词
	 */
	public void getLogOfUser(List<ClickRecordForResultMergeEntity> ret, int userid, String query);

	/**
	 * 查找用户的点击记录，以查询词增序排列返回
	 * @param ret 返回值
	 * @param uidSet 用户ID集合
	 */
	public void getLogOrderbyQueryInc(List<ClickRecordForResultMergeEntity> ret, Set<Integer> uidSet);
}
