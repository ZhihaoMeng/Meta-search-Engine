package agent.data.inblackboard;

import java.util.List;

public class ClusterUserData {

	private String userId;
	private List<String> querys;
	private boolean isKey;
	private boolean isBorder;
	private boolean isNoise;
	public boolean clustered;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getQuerys() {
		return querys;
	}
	public void setQuerys(List<String> querys) {
		this.querys = querys;
	}
	public boolean isKey() {
		return isKey;
	}
	public void setKey() {
		this.isKey = true;
		this.clustered = true;
		this.isBorder = false;
		this.isNoise = false;
	}
	public boolean isBorder() {
		return isBorder;
	}
	public void setBorder() {
		this.isBorder = true;
		this.isKey = false;
	}
	public boolean isNoise() {
		return isNoise;
	}
		public boolean isClustered() {
		return clustered;
	}
	public void setClusterd() {
		clustered = true;
	}
	public void setNoise() {
		this.isNoise = true;
		this.isKey = false;
	}
	public ClusterUserData(String id, List<String> querys) {
		// TODO Auto-generated constructor stub
		this.userId = id;
		this.querys = querys;
		this.clustered = false;
		this.isBorder = false;
		this.isKey = false;
		this.isNoise = false;
	}
}
