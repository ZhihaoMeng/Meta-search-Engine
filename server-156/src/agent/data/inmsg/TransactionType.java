package agent.data.inmsg;

public class TransactionType {

	public static enum InterfaceAgentTxType {login,regist,search,clicklog,queryRecomm,clickRecomm,groupDivide,cfRecomm};
	public static enum SearchEntryAgentTxType {taskDispatch, memberSearchDone, resultMergeDone,searchContinue};
	public static enum QueryRecommEntryAgentTxType {taskDispatch, groupRecommDone, qfgRecommDone};
	public static enum ResultMergeAgentTxType{receiveOriginalResult,calInitScore,searchEngineWeight,removeDuplicate,resultTask};
	
}
