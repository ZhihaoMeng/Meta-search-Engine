package common.functions.recommendation.group;

import java.util.ArrayList;
import java.util.List;

import agent.data.inblackboard.ClusterUserData;
import common.functions.recommendation.click.LikelihoodSimilarity;
import db.dao.UserClusterDao;
import db.dao.UserFavorWordsDao;
import db.dbhelpler.UserClusterHelper;
import db.dbhelpler.UserFavorWordsHelper;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.UserClusterEntity;

public class UserClusterer {

	private static UserClusterer userCluster;
	private double eps;
	private int minPts;
	private int totalQueryNumber;
	
	private List<ClusterUserData> data;
	
	public void setEps(double eps) {
		this.eps = eps;
	}
	public void setMinPts(int minPts) {
		this.minPts = minPts;
	}
	
	private UserClusterer(){
		
	}
	
	public static UserClusterer getInstance(){
		if (null == userCluster ) {
			synchronized (UserClusterer.class) {
				if (null == userCluster) {
					userCluster = new UserClusterer();
				}
			}
		}
		return userCluster;
	}
	
	private UserClusterDao userClusterDao;
	
	private UserClusterDao getUserClusterDao(){
		if (null == userClusterDao) {
			synchronized(this){
				userClusterDao = (UserClusterDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_CLUSTER_DAO_BEAN_NAME);
			}
		}
		return userClusterDao;
	}
	
	
	
	public void cluster(){
		init();
		getClusterData();
		List<Cluster> clusters = new ArrayList<>();
		for (ClusterUserData d : data) {
			if (d.isClustered() || d.isNoise()) {
				continue;
			}else{
				List<ClusterUserData> neighbor = getNeibor(d,data);
				if (neighbor.size() < minPts) {
					d.setNoise();
				}else {
					Cluster cluster = new Cluster();
					cluster.add(d);
					d.setKey();
					
					for (ClusterUserData tempuser : neighbor) {
						cluster.add(tempuser);
						tempuser.setClusterd();
						if (!tempuser.isBorder() && !tempuser.isKey() && !tempuser.isNoise() && !tempuser.isClustered()) {
							List<ClusterUserData> neighbor2 = getNeibor(tempuser,data);
							if (neighbor2.size() >= minPts) {
								for (ClusterUserData user2 : neighbor2) {
									if (!user2.isClustered()) {
										cluster.add(user2);
										user2.setClusterd();
									}
								} 
							}
							
						}						
					}
					clusters.add(cluster);
				}
			}
		}
		
		UserClusterDao ucDao = getUserClusterDao();
		//封装成UserClusterEntity后存入数据库
		UserClusterHelper.clear();
		int id = 1;
		for (Cluster cluster : clusters) {
			List<Integer> uId = new ArrayList<>();
			List<ClusterUserData> cuData = cluster.getUsers();
			for (ClusterUserData clusterUserData : cuData) {
				int uid = Integer.parseInt(clusterUserData.getUserId());
				uId.add(uid);
			}
			String cid = id + "";
			id++;
			UserClusterEntity entity = new UserClusterEntity();
			entity.setClusterId(cid);
			entity.setUidlist(uId);
			ucDao.add(entity);
		}
	}
	
	private List<ClusterUserData> getNeibor(ClusterUserData user, List<ClusterUserData> users) {
		List<ClusterUserData> neighbor = new ArrayList<>();
		List<String> userQuery = user.getQuerys();
		for (ClusterUserData user2 : users) {
			List<String> userQuery2 = user2.getQuerys();
			int k11 = getCoincideNumber(userQuery, userQuery2);
			int k12 = userQuery.size() - k11;
			int k21 = userQuery2.size() - k11;
			int k22 = totalQueryNumber - userQuery.size() - userQuery2.size() + k11;
			double sim = LikelihoodSimilarity.userSimilarity(k11, k12, k21, k22);
			if (sim >= eps) {
				neighbor.add(user2);
			}
		}	
		return neighbor;
	}
	
	private void getClusterData(){
		data = UserFavorWordsHelper.getClusterData();
	}
	
	
	
	private int getCoincideNumber(List<String> list1, List<String> list2){
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

	private void init()
	{
		this.eps = 60;
		this.minPts = 10;
		this.totalQueryNumber = UserFavorWordsHelper.getWordsNum();
	}
}
