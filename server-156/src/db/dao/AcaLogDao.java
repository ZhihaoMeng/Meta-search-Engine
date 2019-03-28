package db.dao;

import java.util.List;
import java.util.Set;
import server.info.entites.transactionlevel.AcaLogRecordEntity;

public interface AcaLogDao {
	public int add(AcaLogRecordEntity log);
	
	public void delete(int id);
	
	public int update(AcaLogRecordEntity log);
	
	public AcaLogRecordEntity get(int id);
	
	/**
	 * 从数据库获取用户的查询日志
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param userid 要查找日志信息的用户的ID
	 */
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret,int userid);
	/**
	 * 从数据库获取用户的查询日志信息
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param userid 用户ID
	 * @param query 查询词
	 */
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret, int userid, String query);
	/**
	 * 从数据库获取用户的查询日志信息
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param uidSet 要查找日志信息的用户ID集合
	 */
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret, Set<Integer> uidSet);
	
	/**
	 * 从数据库获取用户的对特定查询词的查询日志信息
	 * @param ret 获取到的信息追加存放到这里，不能为null
	 * @param uidSet 要查找日志信息的用户ID集合
	 * @param query 查询词
	 */
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret, Set<Integer> uidSet, String query);
	


	/**
	 * 查找用户的查询日志，以查询词增序排列返回
	 * @param ret 返回值
	 * @param uidSet 用户ID集合
	 */
	public void getAcaLogOrderbyQueryInc(List<AcaLogRecordEntity> ret, Set<Integer> uidSet);
}
