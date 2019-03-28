package db.dbhelpler;

import java.util.Set;

import db.dao.UserClusterDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.UserClusterEntity;

public class UserClusterHelper {

	private UserClusterDao userClusterDao;
	private static UserClusterHelper instance;
	private static UserClusterHelper getInstance(){
		if (null == instance) {
			synchronized(UserClusterHelper.class){
				if (null == instance) {
					instance = new UserClusterHelper();
				}
			}
		}
		return instance;
	}
	private UserClusterHelper() {
		userClusterDao = (UserClusterDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_CLUSTER_DAO_BEAN_NAME);
	}
	
	public static UserClusterDao getUserClusterDao(){
		return getInstance().userClusterDao;
	}
	
	public static void add(UserClusterEntity entity){
		getUserClusterDao().add(entity);
	}
	
	public static void clear(){
		getUserClusterDao().clear();
	}
	
	public static void getUsersInCluster(int userid, Set<Integer> ret){
		if(!UserHelper.isLegalUserID(userid)||null==ret) return;
		
		getUserClusterDao().getUsersIdClustered(userid, ret);
	}
	
}
