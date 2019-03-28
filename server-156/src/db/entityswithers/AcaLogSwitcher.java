package db.entityswithers;

import java.util.List;

import server.info.entites.transactionlevel.AcaLogRecordEntity;
import db.hibernate.tables.isearch.AcaLogs;


public class AcaLogSwitcher {
	public static AcaLogs acaLogEntityToPojo(AcaLogRecordEntity entity) {

		if (null == entity)
			return null;

		AcaLogs ret = new AcaLogs();

		int logid = entity.getId();
		if (logid > 0)
			ret.setId(logid);
		ret.setUserid(entity.getUid());
		ret.setQuery(entity.getQuery());
		ret.setTitle(entity.getTitle());
		ret.setAuthors(entity.getAuthors());
		ret.setAbstr(entity.getAbstr());
		ret.setLink(entity.getLink());
		ret.setYear(entity.getYear());
		ret.setCitecount(entity.getCiteCount());
		ret.setSources(entity.getSources());
		ret.setScore(entity.getScore());

		return ret;
	}
	
	public static void acaLogPojoToEntity(List<AcaLogRecordEntity>ret, List<AcaLogs> pojolist){
		
		if(null==pojolist||pojolist.isEmpty()||null==ret) return;
		
		for(AcaLogs logPojo:pojolist) ret.add(AcaLogSwitcher.acaLogPojoToEntity(logPojo));
		
	}
	
	public static AcaLogRecordEntity acaLogPojoToEntity(AcaLogs pojo) {

		AcaLogRecordEntity ret = null;
		if (null == pojo)
			return ret;

		ret = new AcaLogRecordEntity();
		ret.setId(pojo.getId());
		ret.setYear(pojo.getYear());
		ret.setCiteCount(pojo.getCitecount());		
		ret.setQuery(pojo.getTitle());
		ret.setTitle(pojo.getTitle());
		ret.setAuthors(pojo.getAuthors());
		ret.setAbstr(pojo.getAbstr());
		ret.setSources(pojo.getSources());
		ret.setScore(pojo.getScore());
		ret.setLink(pojo.getLink());
		ret.setUid(pojo.getUserid());
		return ret;
	}
}
