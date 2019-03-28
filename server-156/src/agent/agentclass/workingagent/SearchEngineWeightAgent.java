package agent.agentclass.workingagent;

import server.info.config.agent.ServiceNamesOfAgent;
import agent.behaviours.agentspecific.SearchEngineWeightBehavior;
import agent.utils.DFServiceUtil;
import jade.core.Agent;

public class SearchEngineWeightAgent extends Agent{

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		DFServiceUtil.registerService(this, ServiceNamesOfAgent.SEARCH_ENGINE_WEIGHT);
		this.addBehaviour(new SearchEngineWeightBehavior());
	}

	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
		DFServiceUtil.deRegisterAllService(this);
	}

}
