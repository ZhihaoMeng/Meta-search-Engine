package db.dao;

import java.util.List;

public interface CitiesDao {
	public List<Object[]> getAllCitiesByProvinceId(String pid);
	public String getCityById(String cid);
}
