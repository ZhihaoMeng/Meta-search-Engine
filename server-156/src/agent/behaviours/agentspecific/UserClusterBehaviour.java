package agent.behaviours.agentspecific;

import agent.data.inblackboard.GroupDivideData;
import agent.data.inmsg.DataFromInterfaceAgent;
import agent.entities.blackboard.GroupDivideDataBlackboard;
import common.entities.blackboard.UserAgentThread;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class UserClusterBehaviour extends Behaviour {
	
	protected DataFromInterfaceAgent mData;
	protected int mBlackboardIndex;
	private int userId;
	@Override
	public void action() {
		
		ACLMessage msg=myAgent.receive();
		if(null!=msg){
			GroupDivideData data = null;
			try {
				Object obj=msg.getContentObject();
				if(!DataFromInterfaceAgent.class.isInstance(obj)) return;
				mData=null;
				mData=(DataFromInterfaceAgent)obj;
				if(null==mData) return;
				mBlackboardIndex=mData.getIndex();
				data=(GroupDivideData)GroupDivideDataBlackboard.getData(mBlackboardIndex);
				userId = data.getUserId();
				UserAgentThread uat = new UserAgentThread();
				uat.setUserThreadId(userId);
				uat.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			block();
		}
	}

	@Override
	public boolean done() {
		return false;
	}

}
