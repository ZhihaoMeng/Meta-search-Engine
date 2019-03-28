package db.dao;

import java.util.List;
import java.util.Map;

import server.info.entites.transactionlevel.UserInfoEntity;

public interface UserInfoDao {

	public int add(UserInfoEntity user);
	
	public void delete(int uid);
	
	public int update(UserInfoEntity user);
	
	public UserInfoEntity get(int uid);
	
	public List<UserInfoEntity> findUserByUsername(String username);
	
	public List<UserInfoEntity> findUserByEmailAdress(String emaildress);
	
	public boolean setPasswd(int uid, String passwd);
	
	public int getUserIDByUserName(String username);
	
	public Map<String, String> getUserInfosById(int uid);
}
