package db.dao.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import db.dao.UserClusterDao;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;
import db.entityswithers.UserClusterSwitcher;
import db.entityswithers.UserGroupSwitcher;
import db.hibernate.tables.isearch.GroupInfo;
import db.hibernate.tables.isearch.UserCluster;
import db.hibernate.tables.isearch.UserGroups;
import server.info.entites.transactionlevel.UserClusterEntity;

public class UserClusterDaoImpl implements UserClusterDao {

	
	private SessionFactory sessionFactory;
	private UserDao userDao;
	
	
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public UserDao getUserDao() {
		return userDao;
	}
	
	

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public int add(UserClusterEntity entity) {
		if (null == entity)
			return -1;

		List<Integer> uidlist = entity.getUidlist();
		if (null == uidlist || uidlist.isEmpty())
			return -1;

		List<UserCluster> u2clist = UserClusterSwitcher
				.userclusterEntityToPojo(entity);
		if (null == u2clist || u2clist.isEmpty())
			return -1;

		
		Session session = this.sessionFactory.getCurrentSession();
		for (UserCluster cluster : u2clist) {
			if (null == cluster)
				continue;
			session.save(cluster);
		}

		return Integer.parseInt(entity.getClusterId());
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateUserCluster(int id, List<Integer> uidList) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int addUsertoCluster(int gid, Collection<Integer> uidSet) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getUsersIdClustered(int uid, Set<Integer> ret) {
		// TODO Auto-generated method stub
		if (!UserHelper.isLegalUserID(uid))
			return false;
		if(null==ret) return true;
		boolean retval=false;
		
		Set<Integer> gid = new HashSet<Integer>();
		retval=getUserClusterID(uid, gid);
		if(!retval) return retval;
		
		if (null != gid && !gid.isEmpty())
		retval=getUserIDInGroup(gid, ret);
		
		return retval;
	}
	
	private boolean getUserClusterID(int uid, Set<Integer> ret) {

		if(!UserHelper.isLegalUserID(uid)) return false;
		if(null==ret) return true;
		
		Iterator<UserCluster> iterUc = sessionFactory.getCurrentSession()
				.createQuery("from UserCluster uc where uc.user.id = :uid")
				.setParameter("uid", uid).list().iterator();
		while (iterUc.hasNext()) {
			ret.add(iterUc.next().getId());
		}
		return true;
	}

	private boolean getUserIDInGroup(Set<Integer> gidSet, Set<Integer> ret) {

		if(null==gidSet||gidSet.isEmpty()) return false;
		if(null==ret) return true;
		
		Iterator<UserCluster> iterUc = sessionFactory.getCurrentSession()
				.createQuery(	"from UserCluster uc where uc.groupInfo.id in (:gidSet)")
				.setParameterList("gidSet", gidSet).list().iterator();
//		Set<Integer> res=new HashSet<Integer>();
		while(iterUc.hasNext()){
			ret.add(iterUc.next().getUser().getUserid());
		}
		return true;
	}

	@Override
	public boolean clear() {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().createSQLQuery("truncate table user_cluster").executeUpdate();
		
		return true;
	}
	
}
