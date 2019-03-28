package struts.actions.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.annotations.JSON;

import db.dbhelpler.CitiesHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class InfoAction {

	private List<String[]> cities = new ArrayList<>();
	private String pid;
	private Map<String, Object> dataMap = new HashMap<>();; 
	private JSONArray jsonArray = new JSONArray();
	
	
	public JSONArray getJsonArray() {
		return jsonArray;
	}



	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}



	public Map<String, Object> getDataMap() {
		return dataMap;
	}



	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}


	@JSON(serialize=false)
	public List<String[]> getCities() {
		return cities;
	}



	public void setCities(List<String[]> cities) {
		this.cities = cities;
	}



	public String getPid() {
		return pid;
	}



	public void setPid(String pid) {
		this.pid = pid;
	}



	public String execute(){

		cities = CitiesHelper.getCitiesByProvinceId(pid);
//		dataMap.put("cities", cities);
//		dataMap.put("success", true);

//		for (String city : cities) {
//			JSONObject jo = new JSONObject();
//			jo.put(city, city);
//			jsonArray.add(jo);
//		}
		jsonArray = JSONArray.fromObject(cities);
		System.out.println(jsonArray.toString());
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setCharacterEncoding("UTF-8");
//		try {
//			response.getWriter().write(jsonArray.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return "success";
	}
	
	
}

