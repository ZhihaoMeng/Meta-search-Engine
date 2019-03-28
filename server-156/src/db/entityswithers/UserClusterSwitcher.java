package db.entityswithers;

import java.util.ArrayList;
import java.util.List;


import db.hibernate.tables.isearch.GroupInfo;
import db.hibernate.tables.isearch.User;
import db.hibernate.tables.isearch.UserCluster;
import server.info.entites.transactionlevel.UserClusterEntity;

public class UserClusterSwitcher {
	
	public static List<UserCluster> userclusterEntityToPojo(UserClusterEntity entity) {

		if (null == entity)
			return null;
		List<Integer> uidlist = entity.getUidlist();
		if (null == uidlist || uidlist.isEmpty())
			return null;

		List<UserCluster> ret = new ArrayList<UserCluster>();

		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setRemark(entity.getClusterId());

		for (Integer uid : uidlist) {
			if (uid <= 0)
				continue;
			User user = new User();
			user.setUserid(uid);

			UserCluster u2g = new UserCluster();
			u2g.setUser(user);
			u2g.setCid(Integer.parseInt(entity.getClusterId()));
			ret.add(u2g);
		}
		
		return ret;
	}
	
	public static UserClusterEntity usergroupPojoToEntity(List<UserCluster> pojo){
		
		if(null==pojo) return null;
		
		UserClusterEntity ret=new UserClusterEntity();
		if(pojo.isEmpty()) return ret;
//		GroupInfo group=pojo.get(0).getGroupInfo();
//		if(null!=group) ret.setClusterId(group.getRemark());
		
		List<Integer> uidlist=ret.getUidlist();
		for(UserCluster groupInfo:pojo){
			if(null==pojo) continue;
			uidlist.add(groupInfo.getUser().getUserid());
		}
		return ret;
	}
}
