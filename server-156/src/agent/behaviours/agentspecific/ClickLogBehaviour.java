package agent.behaviours.agentspecific;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.transform.Source;

import agent.data.inblackboard.ClickLogData;
import agent.data.inblackboard.MergeCommonData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.ClickLogDataBlackboard;
import common.entities.searchresult.Result;
import common.function.result_merge_methods.ResultMergeMethods;
import common.functions.userinterest.UserClickLogger;
import db.dbhelpler.ClickResultSourceHelper;
import db.dbhelpler.FeedbackForResultHelper;
import db.dbhelpler.UserHelper;
import db.hibernate.tables.isearch.ClickResultSource;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClickLogBehaviour  extends Behaviour{
	
	protected DataFromInterfaceAgent mData;
	protected int userId;
	protected String query;
	protected String title;
	protected String date;
	protected String clickAddr;
	protected String source;
	protected String abstr;
	protected int mBlackboardIndex;
	
	private int baiduCount = 0;
	private int sogouCount = 0;
	private int youdaoCount = 0;
	private int bingCount = 0;
	private int yahooCount = 0;
	
	@Override
	public void action() {
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			ClickLogData data = null;
			try {
				Object obj=msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(obj)) return;
				mData=null;
				mData=(DataFromInterfaceAgent)obj;
				if(null==mData) return;
				mBlackboardIndex=mData.getIndex();
				data=(ClickLogData)ClickLogDataBlackboard.getData(mBlackboardIndex);
				userId = data.getUserid();
				query = data.getQuery();
				title = data.getTitle();
				date = data.getData();
				source = data.getSource();
				clickAddr = data.getClickAddr();
				abstr = data.getAbstr();
				// 更新click_log与user_favor_word
				UserClickLogger idb = new UserClickLogger();
				idb.record(userId, query, title, abstr, clickAddr, date, source);

				//更新用户对成员搜索引擎次数的点击记录
				splitSource(source);
				ClickResultSourceHelper crsHelper = new ClickResultSourceHelper();
				crsHelper.UpdateSourceClick(userId, baiduCount, sogouCount, youdaoCount, bingCount, yahooCount, query.trim(), clickAddr, abstr,date);
			
				//如果是登录用户才记录用户的点击信息
				MergeCommonData instance = MergeCommonData.getInstance();
				List<Result> targetList = instance.getTargetList();
				if (userId != 1 && instance.flag) {
					//获取点击的位置信息
					int position = userClickResultPosition(clickAddr, targetList);					
					if (position == 0) {
						return;
					}
					FeedbackForResultHelper ffrHelper = new FeedbackForResultHelper();
					String result_method = ResultMergeMethods.SDM;					
					int methodItem = ffrHelper.queryClickRecord(userId, result_method);
										
					if (methodItem >= instance.methodCounts) {
						result_method = ResultMergeMethods.MC1;
						methodItem = ffrHelper.queryClickRecord(userId, result_method);
						
						if (methodItem >= 2 * instance.methodCounts) {
							result_method = ResultMergeMethods.PlaceRank;
							methodItem = ffrHelper.queryClickRecord(userId, result_method);							
						}
					}
					ffrHelper.insertClickRecord(userId, query, clickAddr, position, result_method, date);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(null!=data){
					data.done();
				}
			}
		}else{
			block();
		}
	}
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private int userClickResultPosition(String url, List<Result> targetList)
	{	
		System.out.println("url: "+url);		
		int position = 0;
		
		for (int i = 0; i < targetList.size(); i++) {
			String temp = targetList.get(i).getLink();
			
			if (temp.trim().equals(url.trim())) {
				System.out.println("temp: "+targetList.get(i).getLink());
				position = i + 1;
			}
		}
		return position;
	}
	
	private void splitSource(String source)
	{
		if (source.length() == 0) {
			return;
		}
		if (source.contains("百度")||source.contains("baidu")) {
			baiduCount = 1;
		}
		if (source.contains("搜狗")||source.contains("sogou")) {
			sogouCount = 1;
		}
		if (source.contains("有道")||source.contains("youdao")) {
			youdaoCount = 1;
		}
		if (source.contains("必应")||source.contains("bing")) {
			bingCount = 1;
		}	
		if (source.contains("雅虎")||source.contains("yahoo")) {
			yahooCount = 1;
		}
	}
}
