package agent.data.inmsg;

import java.io.Serializable;

public class DataToRemoveDuplicateAgent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9145749362576337326L;
	public DataToRemoveDuplicateAgent(){}
	
	private int mBlackboardIndex;
	
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
}
