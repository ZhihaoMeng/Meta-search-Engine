package db.entityswithers;

import db.dbhelpler.UserHelper;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserInfo;
import server.commonutils.Md5;
import server.info.entites.transactionlevel.UserEntity;
import server.info.entites.transactionlevel.UserInfoEntity;

public class UserInfoSwitcher {
	
	public static UserInfo userEntityToPojo(UserInfoEntity entity) {

		if (null == entity) return null;

		UserInfo ret = new UserInfo();

		int uid = entity.getUid();
		if (uid > 0) ret.setUserid(uid);
//		ret.setEmail(entity.getEmail());
//		ret.setPassword(Md5.encrypt(entity.getPassword()));
//		ret.setUsername(entity.getUsername());
		ret.setAddress(entity.getAddress());
//		ret.setAge(entity.getAge());
		ret.setCompany(entity.getCompany());
		ret.setHome(entity.getHome());
		ret.setJob(entity.getJob());
		ret.setUser(UserSwitcher.userEntityToPojo(UserHelper.getUserById(uid)));
		return ret;
	}

	public static UserInfoEntity userPojoToEntity(UserInfo user) {

		if (null == user)
			return null;
		UserInfoEntity ret = new UserInfoEntity();
//		ret.setEmail(user.getEmail());
//		ret.setPassword(user.getPassword());
		ret.setUid(user.getUserid());
//		ret.setUsername(user.getUsername());
		
		ret.setAddress(user.getAddress());
		ret.setAge(user.getAge());
		ret.setCompany(user.getCompany());
		ret.setHome(user.getHome());
		ret.setJob(user.getJob());
		return ret;
	}

}
