package db.dao;

import java.util.List;

public interface CompanyDao {

	public List<String> getCompaniesByJobAndAddress(int jid, int aid);
}
