package agent.data.inblackboard;

import java.util.concurrent.CountDownLatch;

import server.info.config.MyEnums.RegisterResult;

public class RegistData extends BlackboardBaseData {
	
	private String username;
	private String password;
	private String emailadress;
	private String home;
	private String city;
	private String job;
	private String company;
	private String cookieid;
	private RegisterResult m_enuRegResult;
	
	public RegistData(CountDownLatch doneSig, String username,String password,String email) {
		super(doneSig);
		this.username = username;
		this.password = password;
		this.emailadress = email;
	}

	public RegistData(CountDownLatch doneSig,String username, String password, String emailadress, String home, String city, String job,
			String company) {
		super(doneSig);
		this.username = username;
		this.password = password;
		this.emailadress = emailadress;
		this.home = home;
		this.city = city;
		this.job = job;
		this.company = company;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailadress() {
		return emailadress;
	}

	public void setEmailadress(String emailadress) {
		this.emailadress = emailadress;
	}

	public String getCookieid() {
		return cookieid;
	}

	public void setCookieid(String cookieid) {
		this.cookieid = cookieid;
	}

	public RegisterResult getRegisterResult() {
		return m_enuRegResult;
	}

	public void setRegistState(RegisterResult res) {
		m_enuRegResult = res;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
