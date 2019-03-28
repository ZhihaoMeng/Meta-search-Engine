package db.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import db.dao.ProvincesDao;

public class ProvincesDaoImpl implements ProvincesDao {

	
	private SessionFactory sessionFactory ;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public List<Object[]> getAllProvinces() {
		// TODO Auto-generated method stub
		List<Object[]> ret = new ArrayList<>();
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select p.provinceid, p.province from Provinces p")
				.list();
		
		return ret;
	}

	@Override
	public String getProvinceById(String pid) {
		String ret ;
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select p.province from Provinces p where p.provinceid = :pid")
				.setParameter("pid", pid)
				.list().get(0).toString();
		return ret;
	}

	
}
