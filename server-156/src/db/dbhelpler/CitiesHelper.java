package db.dbhelpler;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.sparql.pfunction.library.listIndex;

import db.dao.CitiesDao;
import db.dao.ProvincesDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

public class CitiesHelper {
	private CitiesDao citiesDao;
	
	private static CitiesHelper instance;
	
	private static CitiesHelper getInstance(){
		if (null == instance) {
			synchronized(CitiesHelper.class){
				if (null == instance) {
					instance = new CitiesHelper();
				}
			}
		}
		return instance;
	}
	
	private CitiesHelper(){
		citiesDao = (CitiesDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.CITIES_DAO_BEAN_NAME);
	}
	
	private static CitiesDao getCitiesDao(){
		return getInstance().citiesDao;
	}
	
	public static List<String[]> getCitiesByProvinceId(String pid){
		List<String[]> ret = new ArrayList<>();
		List<Object[]> tmpList = getCitiesDao().getAllCitiesByProvinceId(pid);
		for (Object[] objects : tmpList) {
			String[] str = new String[2];
			str[0] = objects[0].toString();
			str[1] = objects[1].toString();
			ret.add(str);
			
		}
		return ret; 
	}
	
	public static String getCityById(String cid){
		return getCitiesDao().getCityById(cid);
	}
}
