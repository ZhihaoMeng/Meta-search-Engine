package db.dbhelpler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.dao.ProvincesDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

public class ProvincesHelper {
	private ProvincesDao provinceDao;
	
	private static ProvincesHelper instance;
	
	private static ProvincesHelper getInstance(){
		if (null == instance) {
			synchronized (ProvincesHelper.class) {
				if (null == instance) {
					instance = new ProvincesHelper();
				}
			}
		}
		return instance;
	}
	
	private ProvincesHelper(){
		provinceDao = (ProvincesDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.PROVINCES_DAO_BEAN_NAME); 
	}
	
	private static ProvincesDao getProvincesDao(){
		return getInstance().provinceDao;
	}
	
	public static List<String[]> getProvinces(){
		List<String[]> ret = new ArrayList<>();
		List<Object[]> tmpList = getProvincesDao().getAllProvinces();
		for (Object[] objects : tmpList) {
			String[] string = new String[2];
			string[0] = objects[0].toString();
			string[1] = objects[1].toString();
			ret.add(string);
		}
		return ret;
		
	}
	
	public static String getProvinceById(String pid){
		return getProvincesDao().getProvinceById(pid);
	}
	
}
