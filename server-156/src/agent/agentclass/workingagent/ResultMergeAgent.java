package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ResultMergeBehaviour;
import agent.behaviours.agentspecific.testResultMergeBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ResultMergeAgent extends Agent implements AIDUpdator{
	
	protected AgentIDPool mRemoveDupAIDPool;
	protected AgentIDPool mCalInitScoreAIDPool;
	
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	
	protected void setup(){
		mCalInitScoreAIDPool = new AgentIDPool();
		mRemoveDupAIDPool = new AgentIDPool();
		dfd = new DFAgentDescription();
		sd = new ServiceDescription();
		
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.RESULT_MREGE, dfd, sd);
		this.addBehaviour(new testResultMergeBehaviour());
		//this.addBehaviour(new ResultMergeBehaviour());
		this.addBehaviour(new AIDPoolUpdateBehaviour(this));
	}
	
	protected void takeDown(){
		DFServiceUtil.deRegisterAllService(this);
	}

	@Override
	public boolean updateAIDPool() {
		// TODO Auto-generated method stub
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_CALCULATE_SCORE, mCalInitScoreAIDPool,this,dfd,sd);
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_REMOVE_DUPLICATE, mRemoveDupAIDPool,this,dfd,sd);
		return false;
	}
	
	public AID getRemoveDuplicateReceiver(){
		return mRemoveDupAIDPool.getNext();
	}
	
	public AID getCalculateScoreReceiver(){
		return mCalInitScoreAIDPool.getNext();
	}
}
