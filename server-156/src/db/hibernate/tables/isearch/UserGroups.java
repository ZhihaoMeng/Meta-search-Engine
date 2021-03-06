package db.hibernate.tables.isearch;

// Generated 2016-4-12 17:44:14 by Hibernate Tools 3.4.0.CR1

/**
 * UserGroups generated by hbm2java
 */
public class UserGroups implements java.io.Serializable {

	private Integer id;
	private User user;
	private GroupInfo groupInfo;

	public UserGroups() {
	}

	public UserGroups(User user, GroupInfo groupInfo) {
		this.user = user;
		this.groupInfo = groupInfo;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GroupInfo getGroupInfo() {
		return this.groupInfo;
	}

	public void setGroupInfo(GroupInfo groupInfo) {
		this.groupInfo = groupInfo;
	}

}
