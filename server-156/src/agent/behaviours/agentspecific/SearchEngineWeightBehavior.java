package agent.behaviours.agentspecific;

import java.util.ArrayList;

import com.hp.hpl.jena.sparql.function.library.date;

import db.dbhelpler.ClickResultSourceHelper;
import db.hibernate.tables.isearch.ClickResultSource;
import agent.data.inblackboard.ClickLogData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.ClickLogDataBlackboard;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


public class SearchEngineWeightBehavior extends Behaviour{

	protected DataFromInterfaceAgent mData = null;
	protected int blackbordIndex;
	protected int userid;
	protected String query;
	protected String url;
	protected String abstr;
	protected String source;
	protected ArrayList<String> sourceList = new ArrayList<String>();
	protected String[] sourceArray = new String[10];
	//目前的这种方法没有考虑结果排在成员搜索引擎的位置，而是只考虑结果是否在这个成员搜索引擎中
	protected int baiduCount;
	protected int sogouCount;
	protected int youdaoCount;
	protected int bingCount;
	protected int yahooCount;
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage msg = myAgent.receive();
		if (msg != null) {
			ClickLogData clickLogData = null;
			try {
				Object object = msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(object)) 
					return;
				mData = (DataFromInterfaceAgent)object;
				if(mData == null) return;
				blackbordIndex = mData.getIndex();
				clickLogData = (ClickLogData)ClickLogDataBlackboard.getData(blackbordIndex);
				userid = clickLogData.getUserid();
				query = clickLogData.getQuery();
				url = clickLogData.getClickAddr();
				abstr = clickLogData.getAbstr();
				checkSource(clickLogData.getSource());
				
				//每获取一个用户的点击就更新一次点击源记录的数据库
				//ClickResultSourceHelper updateClickSource = new ClickResultSourceHelper();
				//updateClickSource.UpdateSourceClick(userid, baiduCount, sogouCount, youdaoCount, bingCount, yahooCount, query, url, abstr);
				
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (clickLogData != null) {
					clickLogData.done();				
				}
			}			
		}else {
			block();
		}
	}
	
	private void checkSource(String source)
	{
		if(source.contains("baidu") || source.contains("百度")){
			baiduCount++;
		}
		if (source.contains("yahoo") || source.contains("雅虎")) {
			yahooCount++;
		}
		if (source.contains("bing") || source.contains("必应")) {
			bingCount++;
		}
		if(source.contains("sogou") || source.contains("搜狗")){
			sogouCount++;
		}
		if (source.contains("youdao") || source.contains("有道")) {
			youdaoCount++;
		}
	}
	
	private void splitSource(String source)
	{
		this.sourceArray = source.split(" ");
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
