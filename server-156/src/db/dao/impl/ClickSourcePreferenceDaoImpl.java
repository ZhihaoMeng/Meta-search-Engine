package db.dao.impl;

// Generated 2016-4-7 14:45:31 by Hibernate Tools 3.4.0.CR1

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import db.hibernate.tables.isearch.ClickSourcePreference;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ClickSourcePreference.
 * @see db.hibernate.tables.isearch.ClickSourcePreference
 * @author Hibernate Tools
 */
public class ClickSourcePreferenceDaoImpl {

	private static final Log log = LogFactory
			.getLog(ClickSourcePreferenceDaoImpl.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(ClickSourcePreference transientInstance) {
		log.debug("persisting ClickSourcePreference instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(ClickSourcePreference instance) {
		log.debug("attaching dirty ClickSourcePreference instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ClickSourcePreference instance) {
		log.debug("attaching clean ClickSourcePreference instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(ClickSourcePreference persistentInstance) {
		log.debug("deleting ClickSourcePreference instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ClickSourcePreference merge(ClickSourcePreference detachedInstance) {
		log.debug("merging ClickSourcePreference instance");
		try {
			ClickSourcePreference result = (ClickSourcePreference) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ClickSourcePreference findById(int id) {
		log.debug("getting ClickSourcePreference instance with id: " + id);
		try {
			ClickSourcePreference instance = (ClickSourcePreference) sessionFactory
					.getCurrentSession()
					.get("db.hibernate.tables.isearch.ClickSourcePreference",
							id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ClickSourcePreference> findByExample(
			ClickSourcePreference instance) {
		log.debug("finding ClickSourcePreference instance by example");
		try {
			List<ClickSourcePreference> results = (List<ClickSourcePreference>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"db.hibernate.tables.isearch.ClickSourcePreference")
					.add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
