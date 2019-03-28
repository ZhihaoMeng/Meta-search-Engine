package common.functions.recommendation.click;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.entities.searchresult.Result;
import db.dbhelpler.ClickLogHelper;
import db.dbhelpler.UserClusterHelper;
import db.dbhelpler.UserHelper;
import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.ClickRecordEntity;

public class CFRecommendation {

	private static CFRecommendation instance;

	private static CFRecommendation getInstance(){
		
		if(null==instance){
			synchronized (ClickRecommendation.class) {
				if(null==instance) instance=new CFRecommendation();
			}
		}
		return instance;
	}
	
	private CFRecommendation(){
		
	}
	
	
	/**
	 * 获取结果推荐内容
	 * @param ret 用于追加推荐结果（追加方式，如果已有内容，也不清空）
	 * @param query 查询词
	 * @param userid 用户ID
	 */
	public static void getCFRecommendation(List<Result> ret, String query,
			int userid) {

		if (!UserHelper.isLegalUserID(userid))
			return;
		Set<Integer> groupUserId = new HashSet<Integer>();
		UserClusterHelper.getUsersInCluster(userid, groupUserId);
		getCFRecommendation(ret, query, groupUserId,userid);
	}
	
	/**
	 * 获取结果推荐内容
	 * @param ret 用于追加推荐结果（追加方式，如果已有内容，也不清空）
	 * @param query 查询词
	 * @param groupUserid 群组用户的ID
	 */
	public static void getCFRecommendation(List<Result> ret, String query, Set<Integer> groupUserid,int userid){
		
		//参数检查
		if(MyStringChecker.isBlank(query)||null==groupUserid||groupUserid.isEmpty()||null==ret) return;
		
		//查找日志数据
		List<ClickRecordEntity> logs=new ArrayList<ClickRecordEntity>();
		ClickLogHelper.getClickedResults(logs,groupUserid,query);

		//logs是同组用户搜索过query后点击的结果List
		//格式化推荐结果
		Iterator<ClickRecordEntity> it=logs.iterator();
		Map<ClickRecordEntity, Double> resultSim = new HashMap<>();
		//根据产生推荐的用户与查询用户相似度的高低对结果进行排序
		List<ClickRecordEntity> sortedlogs = new ArrayList<>();
		
		while (it.hasNext()) {
			ClickRecordEntity log = it.next();
			if (null == log) continue;
			int from = log.getUid();
			double sim = RecommendationUtil.calculteSim(from, userid);
			resultSim.put(log, sim);	
		}
		RecommendationUtil.getSortedHashtableByValue(sortedlogs,resultSim);		
		Iterator<ClickRecordEntity> itSorted = sortedlogs.iterator();
		Map<Integer,ClickRecordEntity> log2Result = new HashMap<Integer,ClickRecordEntity>();
		while(itSorted.hasNext()){
			ClickRecordEntity log=itSorted.next();
			if(null==log) continue;			
			//不应推荐用户自身之前的点击结果、且推荐结果不应是同一用户的三条点击记录 20160227
			if (log.getUid() != userid) {
				if (!log2Result.containsKey(log.getUid())) {
					log2Result.put(log.getUid(), log);
				}else{
					if (log.getDatetime().compareTo(log2Result.get(log.getUid()).getDatetime()) >= 0) {
						log2Result.put(log.getUid(), log);
					}
				}
			}
			
		}
		Iterator iter = log2Result.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry<Integer,ClickRecordEntity>) iter.next();
			ClickRecordEntity retLog = (ClickRecordEntity) entry.getValue();
			Result r=new Result(retLog.getTitle(), retLog.getAbstr(), retLog.getUrl(), null);
			r.formatClickRecommResult(retLog);
			if(r.isUsable()) ret.add(r);
		}
	}
	
}
