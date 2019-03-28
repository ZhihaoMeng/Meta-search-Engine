package db.dao.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;

import server.commonutils.MyStringChecker;
import server.info.entites.transactionlevel.AcaLogRecordEntity;
import server.info.entites.transactionlevel.CategoryEntity;
import server.info.entites.transactionlevel.ClickRecordEntity;
import db.dao.AcaLogDao;
import db.dao.CategoryDao;
import db.dbhelpler.UserHelper;
import db.entityswithers.AcaLogSwitcher;
import db.entityswithers.CategorySwitcher;
import db.entityswithers.ClickLogSwitcher;
import db.hibernate.tables.isearch.AcaLogs;
import db.hibernate.tables.isearch.Category;
import db.hibernate.tables.isearch.ClickLog;
import db.hibernate.tables.isearch.User;

public class AcaLogDaoImpl implements AcaLogDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public int add(AcaLogRecordEntity log) {
		int ret = -1;
		if (null == log)
			return ret;
		AcaLogs target = AcaLogSwitcher.acaLogEntityToPojo(log);
		if (target != null) {
			this.sessionFactory.getCurrentSession().save(target);
			ret = target.getId();
		}
		return ret;
	}

	@Override
	public void delete(int id) {
		if (id <= 0)
			return;
		AcaLogs target = this.getPojoById(id);
		if (null != target)
			this.sessionFactory.getCurrentSession().delete(target);
		return;
	}

	private AcaLogs getPojoById(int id) {
		AcaLogs ret = null;
		if (id <= 0)
			return ret;
		Iterator<AcaLogs> itTarget = this.sessionFactory.getCurrentSession()
				.createQuery("from AcaLogs log where log.id = :id")
				.setParameter("id", id).list().iterator();
		if (itTarget.hasNext())
			ret = itTarget.next();
		return ret;
	}

	@Override
	public int update(AcaLogRecordEntity log) {
		int ret = -1;
		int logId = log.getId();
		if (logId <= 0)
			return ret;
		AcaLogs target = AcaLogSwitcher.acaLogEntityToPojo(log);
		if (null == target)
			return ret;
		this.sessionFactory.getCurrentSession().update(target);
		ret = target.getId();
		return ret;
	}

	@Override
	public AcaLogRecordEntity get(int id) {
		AcaLogRecordEntity ret = null;
		if (id <= 0)
			return ret;
		AcaLogs tarPojo = null;
		Iterator<AcaLogs> itLogs = this.sessionFactory.getCurrentSession()
				.createQuery("from AcaLogs clog where clog.id = :id")
				.setParameter("id", id).list().iterator();
		if (itLogs.hasNext()) {
			tarPojo = itLogs.next();
			ret = AcaLogSwitcher.acaLogPojoToEntity(tarPojo);
		}

		return ret;
	}

	@Override
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret, int userid) {
		if (!UserHelper.isLegalUserID(userid) || null == ret)
			return;

		List<AcaLogs> logPojoList = sessionFactory.getCurrentSession()
				.createQuery("from AcaLogs log where log.userid = :uid")
				.setParameter("uid", userid).list();
		if (null != logPojoList)
			AcaLogSwitcher.acaLogPojoToEntity(ret, logPojoList);
	}

	@Override
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret, int userid,
			String query) {
		if (null == ret || !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;

		List<AcaLogs> ls = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from AcaLogs log where log.userid = :userid and log.query = :query")
				.setParameter("userid", userid).setParameter("query", query)
				.list();

		AcaLogSwitcher.acaLogPojoToEntity(ret, ls);

	}

	@Override
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret,
			Set<Integer> uidSet) {
		// 参数检查
		if (null == uidSet || uidSet.isEmpty() || null == ret)
			return;

		// 查数据库
		List<AcaLogs> logPojoList = sessionFactory.getCurrentSession()
				.createQuery("from AcaLogs log where log.userid in (:uidlist)")
				.setParameterList("uidlist", uidSet).list();

		// 数据类型转换
		if (null != logPojoList)
			AcaLogSwitcher.acaLogPojoToEntity(ret, logPojoList);
	}

	@Override
	public void getAcaLogOfUser(List<AcaLogRecordEntity> ret,
			Set<Integer> uidSet, String query) {
		// 参数检查
		if (null == uidSet || uidSet.isEmpty() || null == ret
				|| MyStringChecker.isBlank(query))
			return;

		// 查数据库
		List<AcaLogs> logPojoList = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from AcaLogs log where log.userid in (:uidlist) and log.query = :query")
				.setParameterList("uidlist", uidSet)
				.setParameter("query", query).list();

		// 数据类型转换
		if (null != logPojoList)
			AcaLogSwitcher.acaLogPojoToEntity(ret, logPojoList);
	}

	@Override
	public void getAcaLogOrderbyQueryInc(List<AcaLogRecordEntity> ret,
			Set<Integer> uidSet) {
		if (null == ret || null == uidSet || uidSet.isEmpty())
			return;

		List<AcaLogs> ls = sessionFactory
				.getCurrentSession()
				.createQuery(
						"from AcaLogs log where log.userid in (:uidset) order by log.query")
				.setParameterList("uidset", uidSet).list();

		AcaLogSwitcher.acaLogPojoToEntity(ret, ls);
	}

}
