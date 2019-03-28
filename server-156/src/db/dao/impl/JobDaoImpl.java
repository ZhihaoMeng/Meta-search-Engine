package db.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import db.dao.JobDao;

public class JobDaoImpl implements JobDao {
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}




	public List<Object[]> getAllJobs() {
		List<Object[]> ret = new ArrayList<>();
		
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select j.id,j.job from Job j")
				.list();
		
		
		return ret;
	}



	@Override
	public String getJobById(int jid) {
		String ret;
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select j.job from Job j where j.id = :jid")
				.setParameter("jid", jid)
				.list().get(0).toString();
		return ret;
	}

}
