package server.usedfordebug;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import common.entities.searchresult.AcademicResult;
import server.commonutils.SpringBeanFactoryUtil;
import server.engine.api.CiteSeerXScientificAPI;
import server.engine.api.infactget;
import server.info.config.SpringBeanNames;
import server.info.entites.transactionlevel.AcaLogRecordEntity;
import db.dao.UserDao;
import db.dbhelpler.AcaLogHelper;
import db.dbhelpler.UserHelper;

/**
 * 调试专用listener
 * 用这个类来做调试工作，在提交的时候，把自己修改过的部分删除，不要上传；
 * 否则容易出现冲突
 * 特别留意，上面的import也会因为自动添加新的内容
 * @author zcl
 */
public class DebugEntryListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try{
		/*******************************开始****************************/
//		MyClass.AStaticFuncion(***);
//		
//		MyClass mc=new MyClass(****);
//		mc.funcion();
		/*infactget fact = new infactget();
		fact.getfacts();*/
		/*CiteSeerXScientificAPI cite = new CiteSeerXScientificAPI();
		List<AcademicResult> resultList = new ArrayList<AcademicResult>();
		cite.getMyResults(resultList, "meta-search", 2, 10000, 0);
		for (AcademicResult academicResult : resultList) {
			System.out.println(academicResult.toString());
		}*/
		/*******************************结束****************************/
		}catch(Throwable t){
			t.printStackTrace();
		}
		
		return;
	}

}
