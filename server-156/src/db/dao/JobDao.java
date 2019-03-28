package db.dao;

import java.util.List;

public interface JobDao {
	
	public List<Object[]> getAllJobs();
	
	public String getJobById(int jid);
}
