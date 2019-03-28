package db.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import db.dao.CitiesDao;

public class CitiesDaoImpl implements CitiesDao {

	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@Override
	public List<Object[]> getAllCitiesByProvinceId(String pid) {
		List<Object[]> ret = new ArrayList<>();
		
		ret = this.sessionFactory.getCurrentSession()
				.createQuery("select c.cityid, c.city from Cities c where c.provinceid = :pid")
				.setParameter("pid", pid)
				.list();
		return ret;
	}

	@Override
	public String getCityById(String cid) {
		String city;
		city = this.sessionFactory.getCurrentSession()
				.createQuery("select c.city from Cities c where c.cityid = :cid")
				.setParameter("cid", cid)
				.list().get(0).toString();
		return city;
	}

	

}
