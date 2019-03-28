package server.listener;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import db.dbhelpler.ClickResultSourceHelper;


public class SearchEngineWeightListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		//实现服务器启动时获取热门词汇，并于之后的每一个小时更新一次
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {	
			@Override
			public void run() {
				//查询数据库，获得各成员搜索引擎的权重值
				//SearchEngineWeight seWeight = new SearchEngineWeight();
				//seWeight.searchSourceWeight();
				ClickResultSourceHelper crsHelper = new ClickResultSourceHelper();
				crsHelper.queryAllSourceClicks(1);
			}
		}, 1000, 3600000);//在1秒后执行此任务,每次间隔1小时.
	}

}
