package agent.data.inmsg;

import agent.data.inmsg.TransactionType.ResultMergeAgentTxType;
import jade.util.leap.Serializable;

public class DataToResultMergeAgent implements Serializable {

	private static final long serialVersionUID = 5775460080965067845L;
	
	private int mBlackboardIndex;
	private ResultMergeAgentTxType mType;
	
	public DataToResultMergeAgent(ResultMergeAgentTxType type )
	{
		mType = type;
	}
	
	public DataToResultMergeAgent(int index){
		mBlackboardIndex=index;
	}
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}

	public ResultMergeAgentTxType getTransactionType() {
		return mType;
	}

	public void setTransactionType(ResultMergeAgentTxType mType) {
		this.mType = mType;
	}
	
}
