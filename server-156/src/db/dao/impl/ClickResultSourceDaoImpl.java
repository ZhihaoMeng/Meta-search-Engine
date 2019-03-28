package db.dao.impl;

// Generated 2016-4-7 14:45:31 by Hibernate Tools 3.4.0.CR1

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import db.hibernate.tables.isearch.ClickResultSource;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ClickResultSource.
 * @see db.hibernate.tables.isearch.ClickResultSource
 * @author Hibernate Tools
 */
public class ClickResultSourceDaoImpl {

	private static final Log log = LogFactory
			.getLog(ClickResultSourceDaoImpl.class);

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

	public void persist(ClickResultSource transientInstance) {
		log.debug("persisting ClickResultSource instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(ClickResultSource instance) {
		log.debug("attaching dirty ClickResultSource instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ClickResultSource instance) {
		log.debug("attaching clean ClickResultSource instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(ClickResultSource persistentInstance) {
		log.debug("deleting ClickResultSource instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ClickResultSource merge(ClickResultSource detachedInstance) {
		log.debug("merging ClickResultSource instance");
		try {
			ClickResultSource result = (ClickResultSource) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ClickResultSource findById(int id) {
		log.debug("getting ClickResultSource instance with id: " + id);
		try {
			ClickResultSource instance = (ClickResultSource) sessionFactory
					.getCurrentSession()
					.get("db.hibernate.tables.isearch.ClickResultSource", id);
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

	public List<ClickResultSource> findByExample(ClickResultSource instance) {
		log.debug("finding ClickResultSource instance by example");
		try {
			List<ClickResultSource> results = (List<ClickResultSource>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"db.hibernate.tables.isearch.ClickResultSource")
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
