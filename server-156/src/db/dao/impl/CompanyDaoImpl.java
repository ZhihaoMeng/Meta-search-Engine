package db.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import db.dao.CompanyDao;

public class CompanyDaoImpl implements CompanyDao {
	
	private SessionFactory sessionFactory;
	
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}



	@Override
	public List<String> getCompaniesByJobAndAddress(int jid, int aid) {
		// TODO Auto-generated method stub
		List<String> ret = new ArrayList<>();
		
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select c.companyName from Company c where c.job.id = :jid and c.addressId = :aid")
				.setParameter("jid", jid)
				.setParameter("aid", aid)
				.list();
		
		return ret;
	}

}
