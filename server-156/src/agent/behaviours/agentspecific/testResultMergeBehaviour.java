package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.hp.hpl.jena.sparql.function.library.date;

import server.engine.api.infactget;
import agent.agentclass.workingagent.ResultMergeAgent;
import agent.data.inblackboard.MergeCommonData;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToCalInitScoreAgent;
import agent.data.inmsg.DataToRemoveDuplicateAgent;
import agent.data.inmsg.DataToResultMergeAgent;
import agent.data.inmsg.DataToSearchEngineWeightAgent;
import agent.data.inmsg.DataToSearchEntryAgent;
import agent.data.inmsg.TransactionType.ResultMergeAgentTxType;
import agent.data.inmsg.TransactionType.SearchEntryAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.AgentFactory;
import agent.utils.AgentInitDescriptor;
import common.entities.searchresult.Result;
import common.function.result_merge_methods.EvaluateResultMergeCommon;
import common.function.result_merge_methods.LwyMergeResult;
import common.function.result_merge_methods.ResultMergeMethods;
import common.functions.resultmerge.Merge;
import db.dbhelpler.FeedbackForResultHelper;
import db.dbhelpler.UserHelper;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class testResultMergeBehaviour extends Behaviour {

	//从消息中收到的数据
	protected DataToResultMergeAgent m_sdcDataToMe;
	protected int m_nBlackboardIndex;
	protected Iterator<AID> iterator;

	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	protected List<Result> m_lsForMerge;
	protected List<Result> m_lsTargetResult;
	protected int m_nUserid;
	
	//用于发送响应
	protected ACLMessage m_msgReply;
	protected DataToSearchEntryAgent m_sdcDataReply;
	
	//工作数据
	protected DataToCalInitScoreAgent m_DataToCISA;
	protected DataToRemoveDuplicateAgent m_DataToRDA;
	protected DataToSearchEngineWeightAgent m_DataToSEWA;
	protected ACLMessage m_msgToCISA;
	protected ACLMessage m_msgToRDA;
	protected ACLMessage m_msgToSEWA;
	
	public testResultMergeBehaviour()
	{
		m_DataToCISA = new DataToCalInitScoreAgent();
		m_DataToRDA = new DataToRemoveDuplicateAgent();
		m_DataToSEWA = new DataToSearchEngineWeightAgent();
		m_msgToCISA = new ACLMessage(ACLMessage.INFORM);
		m_msgToRDA = new ACLMessage(ACLMessage.INFORM);
		m_msgToSEWA = new ACLMessage(ACLMessage.INFORM);
	}
	
	protected boolean checkType() {
		ResultMergeAgentTxType type = m_sdcDataToMe.getTransactionType();
		if (!ResultMergeAgentTxType.resultTask.equals(type) && !ResultMergeAgentTxType.calInitScore.equals(type)
				&& !ResultMergeAgentTxType.removeDuplicate.equals(type)) {
			return false;
		}
		return true;
	}
	
	protected boolean getDataFromMessage(ACLMessage msg){
		boolean ret=false;		
		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataToResultMergeAgent) msg.getContentObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != m_sdcDataToMe) {			
			ret = true;
		}			
		return ret;
	}
	
	protected boolean getDataFromBlackboard(){
		
		boolean ret=false;
		m_nBlackboardIndex = m_sdcDataToMe.getIndex();
		m_sdcSearchData=SearchDataBlackboard.getData(m_nBlackboardIndex);
		if(null!=m_sdcSearchData){
			m_strQuery=m_sdcSearchData.getQuery();
			m_lsForMerge=m_sdcSearchData.getMergeResultBuffer();
			m_lsTargetResult=m_sdcSearchData.getTargetListForMerge();
			m_nUserid=m_sdcSearchData.getUserid();
			ret=true;
		}
		return ret;
	}
	
	protected DataToSearchEntryAgent getReplyData(){
		if(null==m_sdcDataReply){
			m_sdcDataReply=new DataToSearchEntryAgent(SearchEntryAgentTxType.resultMergeDone);
		}
		return m_sdcDataReply;
	}
	
	protected ACLMessage getReplyMsg(){
		
		if(null==m_msgReply){
			m_msgReply=new ACLMessage(ACLMessage.INFORM);
			AID myAID=myAgent.getAID();
			m_msgReply.setSender(myAID);
			m_msgReply.addReplyTo(myAID);
		}
		m_msgReply.clearAllReceiver();
		return m_msgReply;
	}
	
	private void sendMsgToScoreAgent()
	{
		try {
			m_DataToCISA.setUserid(m_nUserid);
			m_DataToCISA.setIndex(m_nBlackboardIndex);
			m_msgToCISA.setContentObject(m_DataToCISA);
			m_msgToCISA.clearAllReceiver();
			m_msgToCISA.addReceiver(((ResultMergeAgent)myAgent).getCalculateScoreReceiver());
			m_msgToCISA.setSender(myAgent.getAID());
			myAgent.send(m_msgToCISA);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void sendMsgToRDAgent(){
		try {
			m_msgToRDA.setContentObject(m_DataToRDA);
			m_msgToRDA.clearAllReceiver();
			m_msgToRDA.addReceiver(((ResultMergeAgent)myAgent).getRemoveDuplicateReceiver());
			m_msgToRDA.setSender(myAgent.getAID());
			myAgent.send(m_msgToRDA);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	public void action() {
		
		ACLMessage msg = myAgent.receive();
		if (null != msg) {
			
			if (!getDataFromMessage(msg)) return;
			if (!checkType()) {
				myAgent.postMessage(msg);
				return;
			}
			switch (m_sdcDataToMe.getTransactionType()) {
			//表示这条消息是从search-entry-agent发送过来的
			case resultTask:
				iterator = msg.getAllReplyTo();
				if (!getDataFromBlackboard() || m_lsForMerge == null || m_lsTargetResult == null) {
					return;
				}
				if(!m_lsForMerge.isEmpty()){
					//boolean isLogin = UserHelper.isLoginUser(m_nUserid);						
					//将数据写到共享内存中
					MergeCommonData instance = MergeCommonData.getInstance();
					//在进行新的搜索的时候将flag设置为false
					instance.flag = false;
					//agent每次搜索获得的数目
					instance.setOrignalList(m_lsForMerge);
					//总的条目（此时是未去重前）
					instance.setTargetList(m_lsTargetResult);
					//给计算初始分值的Agent发消息
					sendMsgToScoreAgent();
				}
				reply(msg);
				break;
			case calInitScore:
				
				break;
			case removeDuplicate:
				MergeCommonData data = MergeCommonData.getInstance();
				m_lsTargetResult = data.getTargetList();
				//for (int i = 0; i < data.getTargetList().size(); i++) {
				//	System.out.println("最终打分： "+ data.getTargetList().get(i).getLastScore());
				//}
				List<HashMap<Integer, Result>> mapList = data.getMapList();
				double overlap_rate = data.getOverlap_rate();
				int searchEngineNum = data.getSearcnEngineNum();
				
				FeedbackForResultHelper ffrHelper = new FeedbackForResultHelper();
				String result_method = ResultMergeMethods.SDM;
				List<Integer> rankListSDM = new ArrayList<Integer>();
				List<Integer> rankListMC1 = new ArrayList<Integer>();
				List<Integer> rankListPlaceRank = new ArrayList<Integer>();
								
				if (m_nUserid != 1) {
					if (data.getPreferMethod() != null) {
						result_method = data.getPreferMethod();
					}
					else {
						int methodItem = ffrHelper.queryClickRecord(m_nUserid, result_method);		
						
						if (methodItem >= data.methodCounts) {
							result_method = ResultMergeMethods.MC1;
							methodItem = ffrHelper.queryClickRecord(m_nUserid, result_method);  
							
							if (methodItem >= 2 * data.methodCounts) {
								result_method = ResultMergeMethods.PlaceRank;
								methodItem = ffrHelper.queryClickRecord(m_nUserid, result_method);
								
								if (methodItem >= 3 * data.methodCounts) {
									//data.methodCounts = data.methodCounts + MergeCommonData.ONECOUNTS;
											
									//统计用户对某种算法的偏好，选择某种算法
									rankListSDM = ffrHelper.queryRank(m_nUserid,ResultMergeMethods.SDM);
									rankListMC1 = ffrHelper.queryRank(m_nUserid, ResultMergeMethods.MC1);
									rankListPlaceRank = ffrHelper.queryRank(m_nUserid, ResultMergeMethods.PlaceRank);
									int result[] = new int[3];
									for (int i = 0; i < rankListSDM.size(); i++) {
										result[0] += rankListSDM.get(i);
										result[1] += rankListMC1.get(i);
										result[2] += rankListPlaceRank.get(i);
									}
									if (result[0] <= result[1] && result[0] <= result[2]) {
										result_method = ResultMergeMethods.SDM;
										
									}else if (result[1] <= result[0] && result[1] <= result[2]) {
										result_method = ResultMergeMethods.MC1;
										
									}else if (result[2] <= result[0] && result[2] <= result[1]) {
										result_method = ResultMergeMethods.PlaceRank;
										
									}
									ffrHelper.updatePreferMethod(m_nUserid, result_method);
								}
							}	
						}
					}		
				}
				
				//LwyMergeResult.lwyResultMerge(m_lsTargetResult, mapList, overlap_rate, searchEngineNum, result_method);									
				LwyMergeResult.compareResultMerge(m_lsTargetResult, mapList, searchEngineNum);								
				
				//for (int i = 0; i < m_lsTargetResult.size(); i++) {
				//	System.out.println("lastPos: "+ m_lsTargetResult.get(i).getLastScore());
				//}
				
				//for (int i = 0; i < m_lsTargetResult.size(); i++) {
				//	System.out.println("结果摘要： "+m_lsTargetResult.get(i).getAbstr());
				//}
				
				replyToSearchEntryAgent(iterator);
				break;
			default:
				break;
			}						
		}
	}

	//这个是之前ResultMergeAgent只接收SearchEntryAgent发消息时的回复
	protected void reply(ACLMessage msg){
		
			Iterator<AID> it=msg.getAllReplyTo();
			if(!it.hasNext()) return;
			
			ACLMessage msgReply=getReplyMsg();
			while(it.hasNext()) msgReply.addReceiver(it.next());
			
			DataToSearchEntryAgent data=getReplyData();
			data.setIndex(m_nBlackboardIndex);
			try {
				msgReply.setContentObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			myAgent.send(msgReply);
	}
	
	protected void replyToSearchEntryAgent(Iterator<AID> iterator) {
		if(!iterator.hasNext()) return;
		
		ACLMessage msgReply=getReplyMsg();
		while(iterator.hasNext()) msgReply.addReceiver(iterator.next());
		
		DataToSearchEntryAgent data=getReplyData();
		data.setIndex(m_nBlackboardIndex);
		try {
			msgReply.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		myAgent.send(msgReply);
	}	
	
	@Override
	public boolean done() {
		return false;
	}

}
