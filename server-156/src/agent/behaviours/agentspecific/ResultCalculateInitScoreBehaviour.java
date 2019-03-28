package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import common.entities.searchresult.Result;
import common.function.result_merge_methods.CalculateInitialScore;
import common.function.result_merge_methods.LwyMergeResult;
import agent.agentclass.workingagent.ResultCalculateInitScoreAgent;
import agent.agentclass.workingagent.ResultMergeAgent;
import agent.data.inblackboard.MergeCommonData;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToCalInitScoreAgent;
import agent.data.inmsg.DataToRemoveDuplicateAgent;
import agent.data.inmsg.DataToResultMergeAgent;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.AgentFactory;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ResultCalculateInitScoreBehaviour extends Behaviour{
	private List<Result> orignalList;
	private List<Result> targetList;
	private List<HashMap<Integer, Result>> mapList;
	
	//从消息中收到的数据
	protected DataToCalInitScoreAgent m_sdcDataToMe;
	protected ACLMessage m_msgToRDA;
	protected int m_nBlackboardIndex;
	
	protected DataToRemoveDuplicateAgent m_DataToRDA;
	
	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	protected List<Result> m_lsForMerge;
	protected List<Result> m_lsTargetResult;
	protected int m_nUserid;
		
	
	private int userid = 0;
	
	public ResultCalculateInitScoreBehaviour(){
		m_sdcDataToMe = new DataToCalInitScoreAgent();
		m_msgToRDA = new ACLMessage(ACLMessage.INFORM);
		m_DataToRDA = new DataToRemoveDuplicateAgent();
	}
	
	protected boolean getDataFromMessage(ACLMessage msg){
		boolean ret=false;		
		try {
			m_sdcDataToMe = (DataToCalInitScoreAgent) msg.getContentObject();
			userid = m_sdcDataToMe.getUserid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != m_sdcDataToMe) {
			m_nBlackboardIndex = m_sdcDataToMe.getIndex();
			ret = true;
		}			
		return ret;
	}
	
	protected boolean getDataFromBlackboard(){
		
		boolean ret=false;
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
	
	private void sendMsgToRDAgent(){		
		try {
			m_DataToRDA.setIndex(m_nBlackboardIndex);
			m_msgToRDA.setContentObject(m_DataToRDA);
			m_msgToRDA.clearAllReceiver();
			m_msgToRDA.addReceiver(((ResultCalculateInitScoreAgent)myAgent).getRemoveDuplicateReceiver());
			m_msgToRDA.setSender(myAgent.getAID());
			myAgent.send(m_msgToRDA);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		ACLMessage msg = myAgent.receive();
		if (msg != null) {	
			/*if (getDataFromMessage(msg) && getDataFromBlackboard()) {
				//计算每一文档的初始分值
				CalculateInitialScore calScore = new CalculateInitialScore();
				calScore.calInitScoreList(0.6, -0.9, m_lsForMerge);
				
				//统计当前结果中list的来源
				int searchEngineAmount = LwyMergeResult.memberSearchNum(userid,m_lsForMerge);
				MergeCommonData instance = MergeCommonData.getInstance();
				instance.setSearcnEngineNum(searchEngineAmount);
										
				//将计算分值后的List重新写入共享内存中
				//instance.setOrignalList(orignalList);						
			}*/
			if(!getDataFromMessage(msg)) return;
			//从共享内存中读取数据 不用这种方法是因为查询数量不够的时候会出现问题
			MergeCommonData instance = MergeCommonData.getInstance();
			orignalList = instance.getOrignalList();
			//计算每一文档的初始分值
			CalculateInitialScore calScore = new CalculateInitialScore();
			calScore.calInitScoreList(0.6, -0.9, orignalList);
			
			//统计当前结果中list的来源
			int searchEngineAmount = LwyMergeResult.memberSearchNum(userid,orignalList);
			instance.setSearcnEngineNum(searchEngineAmount);
									
			//将计算分值后的List重新写入共享内存中
			instance.setOrignalList(orignalList);						
			
			//这个Agent的功能结束了，给另一个agent发消息
			sendMsgToRDAgent();	
		}
	}

	private void replyToMsg(String AIDString) {
		// TODO Auto-generated method stub
		ACLMessage msgToScore = new ACLMessage(ACLMessage.INFORM);
		msgToScore.addReceiver(new AID(AIDString, AID.ISGUID));
		msgToScore.setSender(myAgent.getAID());
		msgToScore.setContent("OK");
		myAgent.send(msgToScore);
	}
	
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
