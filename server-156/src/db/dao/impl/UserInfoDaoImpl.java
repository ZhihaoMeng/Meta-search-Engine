package db.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import db.dao.UserInfoDao;
import db.entityswithers.UserInfoSwitcher;
import db.hibernate.tables.isearch.UserInfo;
import server.info.entites.transactionlevel.UserInfoEntity;

public class UserInfoDaoImpl implements UserInfoDao {

	private SessionFactory sessionFactory;

	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	@Override
	public int add(UserInfoEntity user) {
		if (null == user) {
			return -1;
		}
		UserInfo tarUserInfo = UserInfoSwitcher.userEntityToPojo(user);
		return this.add(tarUserInfo);
	}

	

	@Override
	public void delete(int uid) {
		if (uid <= 0)
			return;

		this.sessionFactory.getCurrentSession()
				.createQuery("delete from UserInfo u where u.userid = :uid")
				.setParameter("uid", uid).executeUpdate();
		return;

	}

	@Override
	public int update(UserInfoEntity user) {
		int uid = null != user ? user.getUid() : -1;
		if (uid <= 0)
			return -1;

		UserInfo tarUserInfo = UserInfoSwitcher.userEntityToPojo(user);
		this.sessionFactory.getCurrentSession().update(tarUserInfo);
		return tarUserInfo.getUserid();
	}

	@Override
	public UserInfoEntity get(int uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserInfoEntity> findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserInfoEntity> findUserByEmailAdress(String emaildress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setPasswd(int uid, String passwd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getUserIDByUserName(String username) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, String> getUserInfosById(int uid) {
		if (uid<=0) {
			return null;
		}
		Map<String, String> ret = new HashMap<>();
		List<Object[]> infoList= this.sessionFactory.getCurrentSession()
				.createQuery("select u.home,u.address,u.job,u.company from UserInfo u where u.userid = :uid")
				.setParameter("uid", uid).list();
		if (null != infoList && !infoList.isEmpty()) {
			Object[] info = infoList.get(0);
			String[] key = new String[] {"home","address","job","company"};
			for (int i = 0; i < key.length; i++) {
				ret.put(key[i], (String) info[i]);
			}
		}
		return ret;
	}
	
	private int add(UserInfo tarUserInfo) {
	
		int ret = -1;
		if (null != tarUserInfo) {
			this.sessionFactory.getCurrentSession().save(tarUserInfo);
			ret = tarUserInfo.getUserid();
		}
		return ret;
	}

}
