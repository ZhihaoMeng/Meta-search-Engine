package common.function.result_merge_methods;

import jade.util.leap.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.hp.hpl.jena.sparql.pfunction.library.concat;
import com.sun.org.apache.regexp.internal.recompile;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import agent.data.inblackboard.SearchData;
import common.entities.searchresult.Result;

/**
 * @author wenyuanliu 
 * @date 20160328
 * modify at 0421
 */
public class ResultMergeMethods {
	
	public static String SDM = "SDM";
	public static String CombMNZ = "CombMNZ";
	public static String BordaCount = "BordaCount";
	public static String MC1 = "MC1";
	public static String PlaceRank = "PlaceRank";

	/**
	 * @param k 影子文档加权系数,初始定义为0.2
	 * @param searchNum 成员搜索引擎总数
	 * @param m 出现该文档的成员搜索数量
	 * @param scoreList 每一成员搜索引擎对该文档的初始打分
	 * @return 该文档的总得分
	 */
	public double SDM(double k, int searchNum, int m, ArrayList<Float> scoreList)
	{
		double totalScore = 0;
		for(int i = 0; i < m; i++)
		{
			totalScore += scoreList.get(i);
		}
		totalScore = totalScore + k*(searchNum - m)/m*totalScore;
		return totalScore;
	}
	/**
	 * 调用该函数可以一次计算所有的文档
	 * @param targetList 去重后的文档
	 * @param k 影子文档加权系数,目前假设为0.2
	 */
	public void SDM(List<Result> targetList, double k)
	{
		System.out.println("使用了SDM法");
		double totalScore = 0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		Iterator<Result> iterator = targetList.iterator();
		while(iterator.hasNext())
		{
			Result res = iterator.next();
			for (int i = 0; i < res.initScoreList.size(); i++) {
				totalScore += res.initScoreList.get(i);
			}
			totalScore = totalScore + (k*(SearchData.searchEngineCount - res.getCounts())*1.0)/res.getCounts()*totalScore;
			res.setLastScore(totalScore);
			totalScore = 0.0;
		}
	}
	
	/**
	 * 得到单个文档的分数
	 * @param m 出现该文档的m个成员搜索引擎
	 * @param scoreList 每一成员搜索引擎对该文档的初始打分，注意此处的分数可能是归一化后的分数
	 * @return 该文档总得分
	 */
	public double CombSum(int m, ArrayList<Float> scoreList)
	{
		double totalScore = 0;
		for(int i = 0; i < m; i++)
		{
			totalScore += scoreList.get(i);
		}
		return totalScore;
	}
	/**
	 * 调用该函数可以一次计算所有的文档
	 * @param targetList 去重后的文档
	 */
	public void CombSum(List<Result> targetList)
	{
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		Iterator<Result> iterator = targetList.iterator();
		while(iterator.hasNext())
		{
			Result res = iterator.next();
			for (int i = 0; i < res.initScoreList.size(); i++) {
				totalScore += res.initScoreList.get(i);				
			}
			res.setLastScore(totalScore);
			totalScore = 0.0;
		}
	}
	
	/**
	 * 只能计算单个文档的分数
	 * @param m 打分为非0的m个成员搜索引擎
	 * @param sumScore 利用CombSum方法计算得到的总分
	 * @return 分数
	 */
	public double CombMNZ(int m, int sumScore)
	{
		double totalScore = 0.0;
		totalScore = m*sumScore;
		return totalScore;
	}
	/**
	 * 调用该函数可以一次计算所有的文档
	 * @param targetList 去重后的文档
	 */
	public void CombMNZ(List<Result> targetList)
	{
		System.out.println("使用了CombMNZ法");
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		CombSum(targetList);
		Iterator<Result> iterator = targetList.iterator();
		while(iterator.hasNext())
		{
			totalScore = 0.0;
			Result res = iterator.next();
			totalScore = res.getCounts()*res.getLastScore();
			res.setLastScore(totalScore);
		}
	}	
	
	/*
	 *马氏链MC1算法的计算过程如下：
	 *1、首先得到成员搜索引擎个数searchNum与每一成员搜索引擎返回的当前页的文档排名
	 *2、从第一个成员搜索引擎的1号文档开始，无重复的遍历每一文档，得到比当前文档排名靠前的文档索引集合，如U1={2,3,5}，其中2表示2号文档
	 *3、从上述文档索引集合中计算得到该文档的转移概率
	 */
	/**
	 * @param totalNum 所有的文档总数（去重后的文档总数）
	 * @param map HashMap<Integer,Integer>表示<tag,position>对,表示标识和位置
	 * @param targetList 去重后变为一列的文档，待计算
	 * @return double类型的有序数组，已经按照从大到小排列，存储最后计算得到的概率，该数组从1开始
	 */
	public void MC1(int totalNum, List<HashMap<Integer,Result>> maplist, List<Result> targetList)
	{
		System.out.println("使用了MC1法");
		int matrix = 100;
		double[] lastPro = new double[matrix];//每一文档最终的概率,数组开大一点
		double[][] tempPro = new double[matrix][matrix];//从集合中直接得到的概率矩阵，数组开大一点
		List<Integer> docuSet = new ArrayList<Integer>(); //可跳转页面集合
		int tempPosition = 0;

		for (int j = 0; j < targetList.size(); j++) {			
			Result curResult = targetList.get(j);
			docuSet.clear();
			
			for (int i = 0; i < maplist.size(); i++) {
				HashMap<Integer, Result> map = maplist.get(i);
				
				if(map.containsKey(curResult.getTag()))
				{
					tempPosition = map.get(curResult.getTag()).getPos();
					Iterator<Entry<Integer, Result>> iterator = map.entrySet().iterator();
					while(iterator.hasNext())
					{
						Entry<Integer, Result> entry = iterator.next();
						if (entry.getValue().getPos() <= tempPosition) {
							docuSet.add(entry.getKey());
						}
					}					
				}
			}
			//符合的文档全部放到Set中以后，接着进行概率计算
			for (int i = 0; i < docuSet.size(); i++) {
				tempPro[curResult.getTag()][docuSet.get(i)] += 1.0/(docuSet.size());
			}						
		}
		
		//tempPro[][]概率矩阵构造完毕后，计算最后的概率
		for(int i = 1; i < matrix; i++)
		{
			for(int j = 1; j < matrix; j++)
			{
				lastPro[i] += tempPro[j][i];				
			}
		}
		
		for (int i = 0; i < targetList.size(); i++) {
			Result curResult = targetList.get(i);
			curResult.setLastScore(lastPro[curResult.getTag()]);
		}

	}
	
	public void RoundRobin(List<Result> targetList)
	{
		System.out.println("使用了轮询法");
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		Iterator<Result> iterator = targetList.iterator();
		while(iterator.hasNext())
		{
			totalScore = 0.0;
			Result res = iterator.next();
			for (int i = 0; i < res.initScoreList.size(); i++) {
				totalScore = totalScore + (0.5)*res.initScoreList.get(i)+(0.5)*res.searchWeightList.get(i);
				//totalScore = totalScore + res.initScoreList.get(i);
			}
			res.setLastScore(totalScore);
		}
	}
	/**
	 * 只能计算单个文档的分数
	 * @param m 该文档出现在m个成员搜索引擎中
	 * @param posList 该文档在m个成员搜索引擎中的位置
	 * @param preferWeightList 成员搜索引擎的用户偏好度
	 * @return 该文档的分值
	 */
	public double PlaceRank(int m, ArrayList<Integer> posList, ArrayList<Float> preferWeightList)
	{
		double totalScore = 0;
		for(int i = 0; i < m; i++)
		{
			totalScore += (1.0/posList.get(i))*preferWeightList.get(m-i-1);
		}
		return totalScore;
	}
	
	/**
	 * 调用该函数可以一次计算所有的文档
	 * @param targetList 去重后的文档
	 */
	public void PlaceRank(List<Result> targetList)
	{
		System.out.println("使用了位置排序法");
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		Iterator<Result> iterator = targetList.iterator();
		while(iterator.hasNext())
		{
			totalScore = 0.0;
			Result res = iterator.next();
			for (int i = 0; i < res.posList.size(); i++) {
				//现在的偏好度都假设为1，改进的时候再设置一个hashmap来映射
				int temp = res.posList.get(i);
				totalScore = totalScore + (1.0/temp);				
			}
			res.setLastScore(totalScore);
			
		}
	}
	
	
	/**
	 * 计算单个文档的算法
	 * @param m 该文档出现在m个成员搜索引擎中
	 * @param totalNum 文档总数，如果每次只取每个成员搜索引擎的一页结果进行排序，则该变量表示一页的所有成员搜索引擎的所有文档数（去重后的）
	 * @param posList 该文档在m个成员搜索引擎中的位置
	 * @return 该文档的分值
	 */
	public double BordaCount(int m, int totalNum, ArrayList<Integer>posList)
	{
		double totalScore = 0;
		for(int i = 0; i < m; i++)
		{
			totalScore += (totalNum - posList.get(i) + 1);
		}
		return totalScore;
	}
	
	/*public void BordaCount(List<Result> targetList, int searchEngineNum){
		System.out.println("使用了bordacount法");
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		
		for(int i = 0; i < targetList.size(); i++)
		{
			totalScore = 0.0;
			Result res = targetList.get(i);			
			for(int j = 0; j < res.posList.size(); j++){
				totalScore += (targetList.size() - res.posList.get(j) + 1);
			}
			
			if(searchEngineNum > res.posList.size())
				totalScore += targetList.size()-
				(searchEngineNum - res.posList.size())*(targetList.size()/searchEngineNum)/2;											
			res.setLastScore(totalScore);			
		}
	}*/
	
	/**
	 * 计算所有文档
	 * @param targetList 去重后的文档列表
	 * @param searchEngineNum 搜索引擎个数
	 * @param maplist map个数
	 */
	public void BordaCount(List<Result> targetList, int searchEngineNum,List<HashMap<Integer,Result>> maplist){
		System.out.println("使用了bordacount法");
		double totalScore = 0.0;
		if (targetList == null || targetList.size() == 0) {
			return;
		}
		
		for(int i = 0; i < targetList.size(); i++)
		{
			totalScore = 0.0;
			Result res = targetList.get(i);
			String[] source = res.getSource().split(" ", -1);
			
			for(int j = 0; j < res.posList.size(); j++){//重复的结果在不同引擎的位置
				totalScore += (targetList.size() - res.posList.get(j) + 1);
			}
			
			if(source.length != searchEngineNum) {//所有引擎个数
				int size = 0;
				for (int m = 0; m < source.length; m++) 
				{
					for(int k = 0; k < maplist.size(); k++)
					{
						Iterator<Entry<Integer, Result>> iterator = maplist.get(k).entrySet().iterator();
						Entry<Integer, Result> entry = iterator.next();
						if(source[m].substring(0, 2).equals(entry.getValue().getSource().substring(0, 2)))
						{
							//找到该来源的搜索引擎本次搜到的文档总数
							size += maplist.get(k).size();
							break;
						}
					}//未出现的搜索引擎的总条目/2
					totalScore += (targetList.size() - (targetList.size() - size) + 1)/2;				
				}
			}			
			res.setLastScore(totalScore);			
		}
	}
	/*public void BordaCount(List<HashMap<Integer,Result>> maplist, List<Result> targetList)
	{
		System.out.println("使用了bordacount法");
		double totalScore = 0;
		if (maplist == null || maplist.size() == 0) {
			return;
		}
		for(int j = 0; j < targetList.size(); j++)
		{
			totalScore = 0.0;
			Result res = targetList.get(j);
			String[] source = res.getSource().split(" ", -1);
			int size = 0;
			//int location = 0;
			for (int i = 0; i < source.length; i++) 
			{
				for(int k = 0; k < maplist.size(); k++)
				{
					Iterator<Entry<Integer, Result>> iterator = maplist.get(k).entrySet().iterator();
					Entry<Integer, Result> entry = iterator.next();
					if(source[i].substring(0, 2).equals(entry.getValue().getSource().substring(0, 2)))
					{
						size = maplist.get(k).size();
						break;
					}
				}
				totalScore = totalScore + (size - res.posList.get(i) + 1);				
			}
			res.setLastScore(totalScore);			
		}
	}*/
}
