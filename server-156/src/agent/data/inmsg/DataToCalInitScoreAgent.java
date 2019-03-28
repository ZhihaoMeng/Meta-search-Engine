package agent.data.inmsg;

import java.io.Serializable;

public class DataToCalInitScoreAgent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6619488911285877647L;
	
	private int mBlackboardIndex;
	
	public DataToCalInitScoreAgent(){}
	
	private int userid = 0;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}
		
	public int getIndex(){
		return mBlackboardIndex;
	}
	
	public void setIndex(int index){
		mBlackboardIndex=index;
	}
}
