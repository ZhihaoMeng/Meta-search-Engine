package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ResultCalculateInitScoreBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ResultCalculateInitScoreAgent extends Agent implements AIDUpdator{
	protected AgentIDPool mRemoveDupAIDPool;
	
	private DFAgentDescription dfd;
	private ServiceDescription sd;
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		mRemoveDupAIDPool = new AgentIDPool();
		dfd = new DFAgentDescription();
		sd = new ServiceDescription();
		
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.RESULT_CALCULATE_SCORE,dfd,sd);
		this.addBehaviour(new ResultCalculateInitScoreBehaviour());
		this.addBehaviour(new AIDPoolUpdateBehaviour(this));
	}

	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
		DFServiceUtil.deRegisterAllService(this);
	}

	@Override
	public boolean updateAIDPool() {
		// TODO Auto-generated method stub
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_REMOVE_DUPLICATE, mRemoveDupAIDPool,this,dfd,sd);
		return false;
	}
	public AID getRemoveDuplicateReceiver(){
		return mRemoveDupAIDPool.getNext();
	}
	

}
