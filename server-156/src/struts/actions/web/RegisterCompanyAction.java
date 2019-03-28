package struts.actions.web;

import java.util.List;

import db.dbhelpler.CompanyHelper;
import net.sf.json.JSONArray;

public class RegisterCompanyAction {
	
	private int jobid;
	private int addressid;
	
	private JSONArray jsonArray = new JSONArray();
	
	
	
	public int getJobid() {
		return jobid;
	}



	public void setJobid(int jobid) {
		this.jobid = jobid;
	}



	public int getAddressid() {
		return addressid;
	}



	public void setAddressid(int addressid) {
		this.addressid = addressid;
	}



	public JSONArray getJsonArray() {
		return jsonArray;
	}



	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}



	public String execute(){
		List<String> list = CompanyHelper.getCompaniesByJobAndAddress(jobid, addressid);
		
		if(list == null)
			System.out.println("Empty List");
		else
			System.out.println(list);
		
		jsonArray = JSONArray.fromObject(list);
		
		return "success";
	}
}
