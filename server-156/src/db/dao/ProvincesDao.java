package db.dao;

import java.util.List;

public interface ProvincesDao {
	public List<Object[]>	 getAllProvinces();
	public String getProvinceById(String pid);
}
