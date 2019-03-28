package common.functions.recommendation.group;

import java.util.ArrayList;
import java.util.List;

import agent.data.inblackboard.ClusterUserData;

public class Cluster {

	private int id;
	private List<Integer> usersId = new ArrayList<>();
	private List<Double> center = new ArrayList<>();
	private List<ClusterUserData> users = new ArrayList<>();

	public List<ClusterUserData> getUsers() {
		return users;
	}
	public void setUsers(List<ClusterUserData> users) {
		this.users = users;
	}
	//	private int len;
	private int size;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Integer> getUsersId() {
		return usersId;
	}
	public void setUsersId(List<Integer> usersId) {
		this.usersId = usersId;
	}
	public List<Double> getCenter() {
		return center;
	}
	public void setCenter(List<Double> center) {
		this.center = center;
	}
//	public int getLen() {
//		return len;
//	}
//	public void setLen(int len) {
//		this.len = len;
//	}
	
	public int getSize() {
		return usersId.size();
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Cluster() {
		usersId = new ArrayList<>();
//		center = new ArrayList<>();
	}
	
	public List<Integer> calCenter()
	{
				
		return null;
	}
	public void add (ClusterUserData user){
		users.add(user);
	}
	
}
