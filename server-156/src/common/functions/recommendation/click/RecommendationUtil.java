package common.functions.recommendation.click;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.dao.UserInfoDao;
import db.dbhelpler.UserFavorWordsHelper;
import db.dbhelpler.UserInfoHelper;
import server.info.entites.transactionlevel.ClickRecordEntity;
import server.info.entites.transactionlevel.UserFavorWordsEntity;

public class RecommendationUtil {

	private UserInfoDao userInfoDao;
	//配置Spring文件，通过工具类调用Dao
	//SpringBeanNames
	public static double calculteSim(int userid1, int userid2){
		double sim;
		Map<String, String> userinfo1 = UserInfoHelper.getUserInfoById(userid1);
		Map<String, String> userinfo2 = UserInfoHelper.getUserInfoById(userid2);
		List<String> userwords1 = UserFavorWordsHelper.getWordsOfUser(userid1);
		List<String> userwords2 = UserFavorWordsHelper.getWordsOfUser(userid2);
		int k11 = getCoincideNumber(userwords1, userwords2);
		int k12 = userwords1.size() - k11;
		int k21 = userwords2.size() - k11;
		int k22 = UserFavorWordsHelper.getWordsNum() - k12 - k21 - k11;
		double wordSim = LikelihoodSimilarity.userSimilarity(k11, k12, k21, k22);
		wordSim = Math.log10(wordSim);
		if (null != userinfo1 && !userinfo1.isEmpty() && null != userinfo2 && !userinfo2.isEmpty()) {
			//标准化(log)
			double infoSim = JaccardSimilarity.userSimilarity(userinfo1, userinfo2);
			
			sim = infoSim + wordSim;
			return sim;
		}else {
			return wordSim;
		}
	}
	
	public static void getSortedHashtableByValue(List<ClickRecordEntity> sortedlogs, Map<ClickRecordEntity, Double> map) { 
	    if (map == null || map.isEmpty()) return;
		Set set = map.entrySet();
		List<Map.Entry<ClickRecordEntity, Double>> entries = new ArrayList<>(set);
		Collections.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Double key1 = (Double) ((Entry) arg0).getValue();
				Double key2 = (Double) ((Entry) arg1).getValue();
				return key2.compareTo(key1);
			}
		});
		for (Entry<ClickRecordEntity, Double> entry : entries) {
			sortedlogs.add(entry.getKey());
		}
	}
	
	private static int getCoincideNumber(List<String> list1, List<String> list2){
		if (list1 == null || list2 == null) {
            return 0;
        }
        int num = 0;
        for (String str1 : list1) {
            if (list2.contains(str1)) {
                num++;
            }
        }
        return num;
	}
}
