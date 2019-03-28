package agent.behaviours.agentspecific;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;

import common.entities.searchresult.Result;
import common.function.result_merge_methods.LwyMergeResult;
import agent.agentclass.workingagent.ResultCalculateInitScoreAgent;
import agent.agentclass.workingagent.ResultRemoveDuplicateAgent;
import agent.data.inblackboard.MergeCommonData;
import agent.data.inblackboard.SearchData;
import agent.data.inmsg.DataToCalInitScoreAgent;
import agent.data.inmsg.DataToRemoveDuplicateAgent;
import agent.data.inmsg.DataToResultMergeAgent;
import agent.data.inmsg.TransactionType.ResultMergeAgentTxType;
import agent.entities.blackboard.SearchDataBlackboard;
import agent.utils.AgentFactory;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ResultRemoveDuplicateBehaviour extends Behaviour{

	private List<Result> orignalList;
	private List<Result> targetList;
	private double overlap = 0;
	private List<HashMap<Integer, Result>> mapList;
	
	//从消息中收到的数据
	protected DataToRemoveDuplicateAgent m_sdcDataToMe;
	protected ACLMessage m_msgToRMA;
	protected int m_nBlackboardIndex;
	
	protected DataToResultMergeAgent m_DataToRMA;
	
	//从黑板中取到的数据
	protected SearchData m_sdcSearchData;
	protected String m_strQuery;
	protected List<Result> m_lsForMerge;
	protected List<Result> m_lsTargetResult;
	protected int m_nUserid;
	
	public ResultRemoveDuplicateBehaviour(){
		m_sdcDataToMe = new DataToRemoveDuplicateAgent();
		m_msgToRMA = new ACLMessage(ACLMessage.INFORM);
		m_DataToRMA = new DataToResultMergeAgent(ResultMergeAgentTxType.removeDuplicate);
	}
	
	protected boolean getDataFromMessage(ACLMessage msg){
		boolean ret=false;		
		m_sdcDataToMe = null;
		try {
			m_sdcDataToMe = (DataToRemoveDuplicateAgent) msg.getContentObject();
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

	private void sendMsgToRMAgent(){		
		try {
			m_msgToRMA.setContentObject(m_DataToRMA);
			m_msgToRMA.clearAllReceiver();
			m_msgToRMA.addReceiver(((ResultRemoveDuplicateAgent)myAgent).getResultMergeReceiver());
			m_msgToRMA.setSender(myAgent.getAID());
			myAgent.send(m_msgToRMA);
			
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
			if(getDataFromMessage(msg) && getDataFromBlackboard()){
				//从共享内存中读取数据
				MergeCommonData instance = MergeCommonData.getInstance();
				if (instance.getOrignalList() != null) {
					orignalList = instance.getOrignalList();
					instance.setDallWithDuplicate(m_strQuery, orignalList.size());
				}
				
				List<Result> MC1List = null;
				if (instance.getTargetList() != null) {
					targetList = instance.getTargetList();
					MC1List = LwyMergeResult.removeDuplicate(targetList, orignalList);
					//经过上面的函数之后targetList就变为去重后的list了
					System.out.println("targetList.size(): "+ targetList.size());
					System.out.println("原始文档总数: "+ instance.getDallWithDuplicate());
				}														
	
				if (MC1List != null) {
					instance.setMC1List(MC1List);		
					mapList = instance.getMapList();
					LwyMergeResult.separateListToMulti(instance.getMC1List(), mapList);
				}
				//计算重叠率
				//overlap = LwyMergeResult.calOverlapRate(targetList.size(), instance.getDallWithDuplicate(), instance.getSearcnEngineNum());
				overlap = LwyMergeResult.calOverlapRate(instance.getDallWithDuplicate(), targetList.size());
				System.out.println("overlap" + overlap);			
				//往共享内存中写
				instance.setOverlap_rate(overlap);
				instance.setTargetList(targetList);
				instance.setMapList(mapList);
				
				//以下为调试信息
				/*for (int i = 0; i < mapList.size(); i++) {
					//Iterator<Integer> keyIterator = mapList.get(i).keySet().iterator();
					Iterator<Entry<Integer, Result>> iterator = mapList.get(i).entrySet().iterator();
					while(iterator.hasNext())
					{
						Entry<Integer, Result> entry = iterator.next();
						System.out.println("mapList.getTag :"+ entry.getKey());
						System.out.println("mapList.getSource :"+ entry.getValue().getSource());
					}
					System.out.println();
				}
													
				System.out.println("总文档数：" + instance.getDallWithDuplicate());
				System.out.println("成员搜索引擎个数："+instance.getSearcnEngineNum());
				System.out.println("去重后列表个数："+targetList.size());
				
				System.out.println("overlap" + overlap); */						
			}
			sendMsgToRMAgent();		
		}
	}
	
	private void replyMsg(ACLMessage msg)
	{
		ACLMessage msgToReply = new ACLMessage(ACLMessage.INFORM);
		//msgToReply.addReceiver(new AID(AgentFactory.RESULT_MERGE_AGENT_NAMES[0],AID.ISGUID));
		msgToReply.setSender(myAgent.getAID());
		msgToReply.addReplyTo(myAgent.getAID());
		msgToReply.setContent("OK");
		myAgent.send(msgToReply);
		msgToReply.clearAllReceiver();
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
