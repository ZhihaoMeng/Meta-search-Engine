package agent.agentclass.workingagent;

import agent.behaviours.agentspecific.UserClusterBehaviour;
import agent.utils.DFServiceUtil;
import jade.core.Agent;
import server.info.config.agent.ServiceNamesOfAgent;

public class UserClusterAgent extends Agent {

	@Override
	protected void setup() {
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.USER_CLUSTER);
		this.addBehaviour(new UserClusterBehaviour());
	}

	@Override
	protected void takeDown() {
		DFServiceUtil.deRegisterAllService(this);
	}

	
}
