package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.ResultRemoveDuplicateBehaviour;
import agent.behaviours.common.AIDPoolUpdateBehaviour;
import agent.utils.AIDPoolUpdateUtil;
import agent.utils.AIDUpdator;
import agent.utils.AgentIDPool;
import agent.utils.DFServiceUtil;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ResultRemoveDuplicateAgent extends Agent implements AIDUpdator{

	protected AgentIDPool mResultMergeAIDPool;
	
	private DFAgentDescription dfd;
	private ServiceDescription sd;

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		mResultMergeAIDPool = new AgentIDPool();
		dfd = new DFAgentDescription();
		sd = new ServiceDescription();
		
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.RESULT_REMOVE_DUPLICATE,dfd,sd);
		addBehaviour(new ResultRemoveDuplicateBehaviour());
		addBehaviour(new AIDPoolUpdateBehaviour(this));
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
		AIDPoolUpdateUtil.updatePool(ServiceNamesOfAgent.RESULT_MREGE,mResultMergeAIDPool,this,dfd,sd);
		return false;
	}

	public AID getResultMergeReceiver(){
		return mResultMergeAIDPool.getNext();
	}
}
