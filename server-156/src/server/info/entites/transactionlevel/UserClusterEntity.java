package server.info.entites.transactionlevel;

import java.util.ArrayList;
import java.util.List;

public class UserClusterEntity {

	protected List<Integer> uidlist;
	protected String clusterId;
	
	
	
	public void setUidlist(List<Integer> uidlist) {
		this.uidlist = uidlist;
	}

	public List<Integer> getUidlist(){
		if(null==uidlist) uidlist=new ArrayList<Integer>();
		return uidlist;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	
	
}
