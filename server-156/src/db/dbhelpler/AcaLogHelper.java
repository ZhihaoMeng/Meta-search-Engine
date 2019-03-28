package db.dbhelpler;

import java.util.List;
import java.util.Set;

import server.commonutils.MyStringChecker;
import server.commonutils.SpringBeanFactoryUtil;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.AcaLogRecordEntity;
import db.dao.AcaLogDao;

public class AcaLogHelper {
	private AcaLogDao logdao;

	private static AcaLogHelper instance;

	private static AcaLogHelper getInstance() {
		if (null == instance) {
			synchronized (AcaLogHelper.class) {
				if (null == instance) {
					instance = new AcaLogHelper();
				}
			}
		}
		return instance;
	}

	private AcaLogHelper() {
		logdao = (AcaLogDao) SpringBeanFactoryUtil
				.getBean(SpringBeanNames.ACA_LOG_DAO_BEAN_NAME);
	}

	private static AcaLogDao getLogDao() {
		return getInstance().logdao;
	}

	public static void getAcaResults(List<AcaLogRecordEntity> ret,
			Set<Integer> uidSet, String query) {

		// 参数检查
		if (null == ret || null == uidSet || uidSet.isEmpty()
				|| MyStringChecker.isBlank(query))
			return;

		// 从数据库中取到所有用户的日志
		getLogDao().getAcaLogOfUser(ret, uidSet, query);

	}

	public static void getAcaResults(List<AcaLogRecordEntity> ret, int userid,
			String query) {

		// 参数检查
		if (null == ret || !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;

		// 从数据库中取到所有用户的日志
		getLogDao().getAcaLogOfUser(ret, userid, query);

	}

	public static void getAcaResults(List<AcaLogRecordEntity> ret,
			Set<Integer> uidSet) {

		// 参数检查
		if (null == ret || null == uidSet || uidSet.isEmpty())
			return;

		// 从数据库中取到所有用户的日志
		getLogDao().getAcaLogOfUser(ret, uidSet);

	}

	public static void putAcaResult(AcaLogRecordEntity ret) {
		// 参数检查
		if (null == ret)
			return ;
		// 从数据库中取到所有用户的日志
		getLogDao().add(ret);
	}
}
