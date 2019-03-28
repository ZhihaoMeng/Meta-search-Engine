package db.dbhelpler;

import java.util.HashMap;
import java.util.Map;

import db.dao.UserInfoDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.UserInfoEntity;

public class UserInfoHelper {
	private UserInfoDao userInfoDao;
	
	private static UserInfoHelper instance;
	
	private static UserInfoHelper getInstance(){
		if (null == instance) {
			synchronized (UserInfoHelper.class) {
				if (null == instance) {
					instance = new UserInfoHelper();
				}
			}
		}
		return instance;
	}
	
	private UserInfoHelper(){
		userInfoDao = (UserInfoDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.USER_INFO_DAO_BEAN_NAME);
	}
	
	private static UserInfoDao getUserInfoDao(){
		return getInstance().userInfoDao;
	}
	
	public static Map<String, String> getUserInfoById(int uid){
		Map<String, String > ret = new HashMap<>();
		
		ret = getUserInfoDao().getUserInfosById(uid);
		return ret;
	}
	
	public static int addUserInfoEntity(UserInfoEntity userInfo){
		int ret = 0;
		ret = getUserInfoDao().add(userInfo);
		return ret;
	}
	
	public final static boolean isLegalUserInfoID(int userid){
		return userid>0;
	}
}
