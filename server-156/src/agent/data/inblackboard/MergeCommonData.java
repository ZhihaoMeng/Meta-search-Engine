package agent.data.inblackboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.entities.searchresult.Result;

public class MergeCommonData extends BlackboardBaseData{

	//private static List<Result> orignalList = new ArrayList<Result>();
	//private static List<Result> targetList = new ArrayList<Result>();
	private List<Result> orignalList;
	private List<Result> targetList;
	private List<Result> MC1List;
	//当前查询
	public static String query = null;
	
	private int DallWithDuplicate = 0;//去重以前所有文档的总数
	private double overlap_rate = -1;//重叠率
	private int searcnEngineNum = 0;//统计搜索引擎个数
	private List<HashMap<Integer, Result>> mapList = new ArrayList<HashMap<Integer,Result>>();	
	
	//成员搜索引擎的偏好度
	private double baiduWeight = 0;
	private double sogouWeight = 0;
	private double youdaoWeight = 0;
	private double bingWeight = 0;
	private double yahooWeight = 0;
		
	public int methodCounts = 50;
	public static final int ONECOUNTS = 50;
	//登录用户对某一算法的偏好
	private String preferMethod = null;
	//当前的条件是否符合登录用户要使用数据库存储那些算法的场景
	public boolean flag = false;		
	
	public final int targetResultNum = 20;
	public int currentResultNum = 0;
	
	private static MergeCommonData instance = null;
	public static synchronized MergeCommonData getInstance()
	{					
		if (instance == null) {
			instance = new MergeCommonData();
		}								
		return instance;
	}	
	public double getOverlap_rate() {
		return overlap_rate;
	}

	public void setOverlap_rate(double overlap_rate) {
		this.overlap_rate = overlap_rate;
	}	
	
	public String getPreferMethod() {
		return preferMethod;
	}
	public void setPreferMethod(String preferMethod) {
		this.preferMethod = preferMethod;
	}
	public double getBaiduWeight() {
		return baiduWeight;
	}
	public void setBaiduWeight(double baiduWeight) {
		this.baiduWeight = baiduWeight;
	}

	public double getSogouWeight() {
		return sogouWeight;
	}
	public void setSogouWeight(double sogouWeight) {
		this.sogouWeight = sogouWeight;
	}

	public double getYoudaoWeight() {
		return youdaoWeight;
	}
	public void setYoudaoWeight(double youdaoWeight) {
		this.youdaoWeight = youdaoWeight;
	}

	public double getBingWeight() {
		return bingWeight;
	}
	public void setBingWeight(double bingWeight) {
		this.bingWeight = bingWeight;
	}

	public double getYahooWeight() {
		return yahooWeight;
	}
	public void setYahooWeight(double yahooWeight) {
		this.yahooWeight = yahooWeight;
	}

	public List<Result> getMC1List() {
		return MC1List;
	}

	public void setMC1List(List<Result> MC1List) {
		this.MC1List = MC1List;
	}

	public void setOrignalList(List<Result> orignalList)
	{
		this.orignalList = orignalList;
	}
	
	public List<Result> getOrignalList()
	{
		return orignalList;
	}
	
	public void setTargetList(List<Result> targetList)
	{
		this.targetList = targetList;
	}
	
	public List<Result> getTargetList()
	{
		return targetList;
	}

	public int getDallWithDuplicate() {
		return DallWithDuplicate;
	}

	public void setDallWithDuplicate(String curQuery, int dallWithDuplicate) {
		if(query == null || !query.equals(curQuery)){
			System.out.println("不同！");
			query = curQuery;
			DallWithDuplicate = dallWithDuplicate;
		}else if(query.equals(curQuery)){
			System.out.println("相同！");
			DallWithDuplicate += dallWithDuplicate;
		}		
	}	

	public int getSearcnEngineNum() {
		return searcnEngineNum;
	}

	public void setSearcnEngineNum(int searcnEngineNum) {
		this.searcnEngineNum = searcnEngineNum;
	}

	public List<HashMap<Integer, Result>> getMapList() {
		return mapList;
	}

	public void setMapList(List<HashMap<Integer, Result>> mapList) {
		this.mapList = mapList;
	}
	
}
