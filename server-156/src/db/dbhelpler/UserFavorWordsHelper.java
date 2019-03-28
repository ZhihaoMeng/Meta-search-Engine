package db.dbhelpler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import agent.data.inblackboard.ClusterUserData;
import db.dao.UserFavorWordsDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

public class UserFavorWordsHelper {
	private UserFavorWordsDao userFavorWordsDao;
	
	private static UserFavorWordsHelper instance;

	private UserFavorWordsHelper(){
		userFavorWordsDao = (UserFavorWordsDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_FAVOR_WORDS_DAO_BEAN_NAME);
	}
	
	private static UserFavorWordsHelper getInstance(){
		if (null == instance) {
			synchronized(UserFavorWordsHelper.class){
				if (null == instance) {
					instance = new UserFavorWordsHelper();
				}
			}
		}
		return instance;
	}
	
	private static UserFavorWordsDao getUserFavorWordsDao(){
		return getInstance().userFavorWordsDao;
	}
	
	public static  List<String> getWordsOfUser(int uid){
		List<String> ret = new ArrayList<>();
		ret = getUserFavorWordsDao().getAllWordsByUser(uid);
		return ret;
		
	}
	public static int getWordsNum(){
		int ret = 0;
		ret = getUserFavorWordsDao().getWordsNum();
		return ret;
	}
	
	public static List<ClusterUserData> getClusterData(){
		List<ClusterUserData> ret = new ArrayList<>();
		Map<Integer, List<String>> userWords = getUserFavorWordsDao().getUsersAndWords();
		Iterator<Map.Entry<Integer, List<String>>> it = userWords.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, List<String>> entry =  it.next();
			Integer id = entry.getKey();
			List<String> querys = entry.getValue();
			ClusterUserData data = new ClusterUserData(id.toString(), querys);			
			ret.add(data);
		}
		
		return ret;
	}
}
