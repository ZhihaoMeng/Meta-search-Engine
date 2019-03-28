package db.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import db.hibernate.tables.isearch.UserCluster;

import server.info.entites.transactionlevel.UserClusterEntity;

public interface UserClusterDao {
	
	public int add (UserClusterEntity entity);
	public boolean delete(int id);
	public boolean updateUserCluster(int id, List<Integer> uidList);
	public int addUsertoCluster(int gid, Collection<Integer> uidSet);
	
	public boolean getUsersIdClustered(int uid,Set<Integer> ret);
	
	public boolean clear();
	
	
}
