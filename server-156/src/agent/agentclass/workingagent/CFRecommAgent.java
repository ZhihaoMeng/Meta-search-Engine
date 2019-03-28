package agent.agentclass.workingagent;

import agent.behaviours.agentspecific.CFRecommBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;
import server.info.config.agent.ServiceNamesOfAgent;

public class CFRecommAgent extends Agent {

	@Override
	protected void setup() {
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.CF_RECOMMENDATION);
		this.addBehaviour(new CFRecommBehaviour());
	}

	@Override
	protected void takeDown() {
		DFServiceUtil.deRegisterAllService(this);
	}

	
}
