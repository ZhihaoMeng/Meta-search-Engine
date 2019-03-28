package db.dbhelpler;

import java.util.ArrayList;
import java.util.List;

import db.dao.JobDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

public class JobHelper {

	private static JobHelper instance;
	private JobDao jobDao;
	
	private static JobHelper getInstance(){
		if (null == instance) {
			synchronized(JobHelper.class){
				if (null == instance) {
					instance = new JobHelper();
				}
			}
		}
		return instance;
	}
	
	private static JobDao getJobDao(){
		return getInstance().jobDao;
	}
	
	private JobHelper(){
		jobDao = (JobDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.JOB_DAO_BEAN_NAME);
	}
	
	
	public static  List<String[]> getAllJobs(){
		List<String[]> ret = new ArrayList<>();
		
		List<Object[]> tmpList = getJobDao().getAllJobs();
		for (Object[] objects : tmpList) {
			String[] str = new String[2];
			str[0] = objects[0].toString();
			str[1] = objects[1].toString();
			ret.add(str);
		}
		return ret;
		
	}
	public static String getJobById(int jid){
		return getJobDao().getJobById(jid);
	}
	public static String getJobById(String jid){
		int id = Integer.parseInt(jid);
		return getJobDao().getJobById(id);
	}
	
}
