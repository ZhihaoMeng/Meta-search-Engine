package db.dbhelpler;

import java.util.List;

import db.dao.CompanyDao;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;

public class CompanyHelper {

	private CompanyDao companyDao;
	
	private static CompanyHelper instance;
	
	private CompanyHelper(){
		companyDao = (CompanyDao) SpringBeanFactoryUtil.getBean(SpringBeanNames.COMPANY_DAO_BEAN_NAME);
	}
	
	private static CompanyHelper getInstance(){
		if (null == instance) {
			synchronized(CompanyHelper.class){
				if (null == instance) {
					instance = new CompanyHelper();
				}
			}
		}
		return instance;
	}
	
	private static CompanyDao getCompanyDao(){
		return getInstance().companyDao;
	}
	
	public static List<String> getCompaniesByJobAndAddress(int jid, int aid){
		return getCompanyDao().getCompaniesByJobAndAddress(jid, aid);
	}
	
}
