package common.function.result_merge_methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.proxy.map.MapLazyInitializer;

import agent.agentclass.workingagent.MemberSearchAgent;
import agent.behaviours.agentspecific.MemberSearchBehaviour;
import agent.behaviours.agentspecific.ResultMergeBehaviour;
import agent.data.inblackboard.MergeCommonData;
import agent.data.inblackboard.SearchData;
import server.commonutils.MyStringChecker;
import common.entities.searchresult.Result;
import common.entities.searchresult.ResultPoolItem;
import common.functions.resultmerge.Merge;
import common.functions.resultmerge.MergeSort;
import common.textprocess.textsegmentation.CreateWordList;
import common.textprocess.textsegmentation.WordList;
import db.dbhelpler.ClickSourcePreferenceHelper;
import db.dbhelpler.UserHelper;
/**
 * add at 20160418
 * @author elaine
 *
 */
public class LwyMergeResult {	
	
	public static void lwyPreResultMerge(List<Result> tarlist, List<Result> newRes,
			int userid, String query, boolean isLogin) {
		int DallWithDuplicate = 0;//去重以前所有文档的总数
		double overlap_rate = -1;//重叠率
		int searcnEngineNum = 0;//统计搜索引擎个数
		List<HashMap<Integer, Result>> mapList = new ArrayList<HashMap<Integer,Result>>();
		
		if (null == tarlist || null == newRes || newRes.isEmpty()
				|| !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;

		//计算每一文档的初始分值
		CalculateInitialScore calScore = new CalculateInitialScore();
		calScore.calInitScoreList(0.6, -0.9, newRes);
		
		//去重以前所有文档的总数
		DallWithDuplicate = newRes.size();
		
		//统计当前list中结果的来源
		//searcnEngineNum = memberSearchNum(newRes);
		//去重后的所有的文档结果,目标是得到处理后的tarList，副产品是MC1List为了马氏链方法做准备
		List<Result> mC1List = LwyMergeResult.removeDuplicate(tarlist, newRes);		
		LwyMergeResult.separateListToMulti(mC1List, mapList);
		
		overlap_rate = calOverlapRate(tarlist.size(), DallWithDuplicate, searcnEngineNum);
	//	overlap_rate = getCRfromDB(qid);
		for (int i = 0; i < tarlist.size(); i++) {
			System.out.println("去重后的列表: "+ tarlist.get(i).getTag());
		}
		System.out.println(overlap_rate);
		//使用某一算法计算得分，并排序
		lwyResultMerge(tarlist,mapList,overlap_rate,searcnEngineNum,ResultMergeMethods.SDM);
		for (int i = 0; i < tarlist.size(); i++) {
			System.out.println("lastPos: "+ tarlist.get(i).getLastScore());
		}
	}
	
	public static void compareResultMerge(List<Result> tarList, List<HashMap<Integer, Result>> mapList, int searchEngineNum)
	{
		ResultMergeMethods methods = new ResultMergeMethods();
		System.out.println("引擎个数： "+ searchEngineNum);
		//methods.BordaCount(tarList, searchEngineNum, mapList);
		//methods.MC1(tarList.size(), mapList, tarList);		
		methods.SDM(tarList, 0.2);
		//methods.RoundRobin(tarList);
		//methods.CombMNZ(tarList);
		
		Collections.sort(tarList, new Comparator<Result>()
		{
			@Override
			public int compare(Result o1, Result o2) {
				// TODO Auto-generated method stub
				if(o1.getLastScore() < o2.getLastScore())
					return 1;
				else if(o1.getLastScore() == o2.getLastScore())
					return 0;
				else
					return -1;
			}			
			
		});
	}
	
	public static void lwyResultMerge(List<Result> tarList, List<HashMap<Integer, Result>> mapList, 
			double overlap_rate, int searchEngineNum, String result_method)
	{
		ResultMergeMethods methods = new ResultMergeMethods();				
		if (overlap_rate < 0.15) {			
			//methods.PlaceRank(tarList);
			methods.RoundRobin(tarList);
		}
		else if (overlap_rate >= 0.15 && overlap_rate < 0.20) {
			methods.RoundRobin(tarList);			
		}
		else if (overlap_rate >= 0.20 && overlap_rate <= 0.25 ) {
			/*if(searchEngineNum < 4)
				methods.CombMNZ(tarList);
			else */methods.BordaCount(tarList, searchEngineNum,mapList);
		}
		else if (overlap_rate > 0.25 && overlap_rate <= 0.30) {
			//if(searchEngineNum > 3)
				methods.SDM(tarList, 0.5);
			//else methods.CombMNZ(tarList);
		}
		else if (overlap_rate > 0.30 && overlap_rate <= 0.40) {	
			/*if(searchEngineNum > 3)
				methods.BordaCount(tarList, searchEngineNum,mapList);
			else */methods.CombMNZ(tarList);
		}
		else if (overlap_rate > 0.4) {			
			methods.MC1(tarList.size(), mapList, tarList);		
		}
		
		Collections.sort(tarList, new Comparator<Result>()
		{
			@Override
			public int compare(Result o1, Result o2) {
				// TODO Auto-generated method stub
				if(o1.getLastScore() < o2.getLastScore())
					return 1;
				else if(o1.getLastScore() == o2.getLastScore())
					return 0;
				else
					return -1;
			}
		});
	}
	
	/**
	 * 计算文档的重叠率，公式如下：((D1+D2+D3...) - Dall)/((n-1)*Dall)
	 * @param Dall n个成员搜索引擎返回的文档总数(去重后的文档总数)
	 * @param Dduplicate 去重前的文档总数
	 * @param searchEngineAmount 成员搜索引擎总个数
	 * @return 返回计算出来的文档重叠率
	 */
	public static double calOverlapRate(int Dall, int Dduplicate, int searchEngineAmount)
	{
		double overlap = 0;		
		overlap = (double)(Dduplicate - Dall)/((searchEngineAmount-1)*Dall);
		return overlap;		
	}
	
	public static double calOverlapRate(int OrignalNum, int targetNum)
	{
		double overlap = 0;
		overlap = (double)(OrignalNum-targetNum)/(OrignalNum);
		return overlap;
	}
	/**
	 * 统计成员搜索引擎的个数
	 * @param originalList
	 * @return
	 */
	public static int memberSearchNum(int userid, List<Result> originalList)
	{
		if (originalList == null || originalList.size() == 0) {
			return 0;
		}
		ClickSourcePreferenceHelper cspHelper = new ClickSourcePreferenceHelper();
		ArrayList<Double> resultList = null;
		//if(userid != 1)
		//{
			resultList = new ArrayList<Double>();
			resultList = cspHelper.SelectUserPreference(userid);
		//}
		int count = 1;
		String temp = originalList.get(0).getSource().substring(0,2);		
				
		for (int i = 0; i < originalList.size(); i++) {			
			if (!originalList.get(i).getSource().contains(temp)) {
				count++;
				temp = originalList.get(i).getSource().substring(0, 2);
			}
			
			originalList.get(i).setSearchWeight(verifyWeight(temp));
			
			if(resultList == null)
			{
				originalList.get(i).setSearchWeight(1.0);
			}
			else {
				originalList.get(i).setSearchWeight(getWeightFromDB(userid, temp,resultList));
			}		
			
			originalList.get(i).searchWeightList.add(originalList.get(i).getSearchWeight());
			//System.out.println("searchweight--->"+originalList.get(i).getSource()+"  : "+originalList.get(i).getSearchWeight());
		}
		
		return count;
	}
	
	public static double getWeightFromDB(int userid, String temp, ArrayList<Double> resultList)
	{
		double result = 0;		
		
		if (temp.equals("baidu") || temp.equals("百度")) {
			result = resultList.get(0);
		}
		else if (temp.equals("sogou") || temp.equals("搜狗")) {
			result = resultList.get(1);
		}
		else if (temp.equals("youdao") || temp.equals("有道")) {
			result = resultList.get(2);
		}
		else if (temp.equals("bing") || temp.equals("必应")) {
			result = resultList.get(3);
		}
		else if (temp.equals("yahoo") || temp.equals("雅虎")){
			result = resultList.get(4);
		}
		return result;
	}
	
	public static double verifyWeight(String temp)
	{
		double result = 0;
		MergeCommonData instance = MergeCommonData.getInstance();
		if (temp.equals("baidu") || temp.equals("百度")) {
			result = instance.getBaiduWeight();
		}
		else if (temp.equals("sogou") || temp.equals("搜狗")) {
			result = instance.getSogouWeight();
		}
		else if (temp.equals("youdao") || temp.equals("有道")) {
			result = instance.getYoudaoWeight();
		}
		else if (temp.equals("yahoo") || temp.equals("雅虎")) {
			result = instance.getYahooWeight();
		}
		else {
			result = instance.getBingWeight();
		}
		return result;
	}
	
	/**
	 * 为了获得来自不同成员搜索引擎的HashMap,同时做些比如设置权重的处理
	 * @param MC1List removeDuplicate函数去重后得到的newRes列表，里面已经处理好了tag
	 * @param targetList 目标list，包含n个成员搜索引擎返回的结果列表，其中tag是已经处理过的了
	 */
	public static void separateListToMulti(List<Result> MC1List, List<HashMap<Integer, Result>> targetList)
	{
		if (MC1List == null) {
			return;
		}
		String temp = MC1List.get(0).getSource().substring(0, 2);
		HashMap<Integer,Result> map = new HashMap<Integer, Result>();
		map.put(MC1List.get(0).getTag(), MC1List.get(0));
	
		for (int i = 1; i < MC1List.size(); i++) {
			Result curResult = MC1List.get(i);
			if (!curResult.getSource().contains(temp)) {
				temp = curResult.getSource().substring(0, 2);
				targetList.add(map);
				map = new HashMap<Integer, Result>();
			}
			
			if (!map.containsKey(curResult.getTag())) {
				map.put(curResult.getTag(), curResult);
			}						
		}
		//收尾工作
		targetList.add(map);
		map = null;
	}
	/**
	 * 统计来自不同成员搜索引擎的结果，同时把使用的成员搜索引擎个数赋予searcnEngineNum
	 * @param originalList 原始的结果列表
	 * @param list 存储不同成员结果列表HashMap结构的列表
	 * @return 成员搜索引擎个数
	 */
	/*public static int separateResultToMultiList(List<Result> originalList, List<HashMap<Integer, Result>> list)
	{
		int count = 1, tag = 1;
		String temp = originalList.get(0).getSource().substring(0, 2);

		originalList.get(0).setTag(1);
		HashMap<Integer,Result> map = new HashMap<Integer, Result>();
		map.put(1, originalList.get(0));
		
		for (int i = 1; i < originalList.size(); i++) {
			if (!originalList.get(i).getSource().contains(temp)) {
				count++;
				temp = originalList.get(i).getSource().substring(0, 2);
				list.add(map);
				map = new HashMap<Integer, Result>();
			}
			tag = tag+1;
			originalList.get(i).setTag(tag);
			map.put(tag, originalList.get(i));
		}
		//收尾工作
		list.add(map);
		map = null;
		return count;
	}	*/
	
	/**函数功能：去重，同时为MC1算法做准备,这个函数运行后的newRes就是MC1算法要使用的
	 * 执行这个函数之后，目标列表和待合成结果列表中的Result都已经相同，因为指针共享的问题
	 * 
	 * 把两个列表中的结果合并为一个，同时把相同的结果去除，权重累加
	 * @param tarlist
	 *            目标列表，初始时候是空的
	 * @param orignalList
	 *            待合成结果列表,把所有的学校的列表都放在这个List中
	 * @return MC1算法所需的List
	 */
	public static List<Result> removeDuplicate(List<Result> tarlist, List<Result> orignalList) {
		boolean flag = false;		
		List<Result> MC1List = new ArrayList<Result>();
		List<Result> newList = orignalList;
		//newList.addAll(newRes);		
		if (null == newList || newList.isEmpty() || null == tarlist)
			return null;
		for (int i = 0; i < newList.size(); i++) {
			Result curResult = newList.get(i);
			if (curResult != null) {
				curResult.setTag(i+1);
				Result result = new Result(curResult.getTag(), curResult.getPos(), curResult.getInitScore(), curResult.getSource());
				MC1List.add(result);
			}
		}
		
		for (int j = 0; j < newList.size(); j++) {
			Result curRes = newList.get(j);
			Result mc1Res = MC1List.get(j);
			flag = false;
			
			if (null == curRes)
				continue;
			
			if (tarlist.size() == 0 ) {
				tarlist.add(curRes);
				continue;
			}
			
			for (int i = 0; i < tarlist.size(); i++) {				
				Result tarRes = tarlist.get(i);
				
				if (curRes.getLink().trim().equals(tarRes.getLink().trim()) || curRes.getTitle().trim().equals(tarRes.getTitle().trim())) {
					followUpProcess(tarRes, curRes);
					curRes.setTag(tarRes.getTag());
					mc1Res.setTag(tarRes.getTag());
					flag = true;
					break;
				}
				
				//进行分词
				/*if (orgRes.abstrWordList == null) {
					orgRes.abstrWordList = CreateWordList.abstrTermFrequency(orgRes.getAbstr());
				}
				if (curRes.abstrWordList == null) {				
					curRes.abstrWordList = CreateWordList.abstrTermFrequency(curRes.getAbstr());
				}
				
				//设定阈值为0.6？
				if (orgRes.abstrWordList != null && curRes.abstrWordList != null 
						&& cosineSimilary(orgRes.abstrWordList,curRes.abstrWordList) > 0.6) 
				{
					followUpProcess(orgRes, curRes);
					curRes.setTag(orgRes.getTag());
					mc1Res.setTag(orgRes.getTag());
					flag = true;
					break;
				}*/		
			}
			
			if (!flag) {
				tarlist.add(curRes);				
			}			
		}
		//测试数据
		/*for (int i = 0; i < MC1List.size(); i++) {
			System.out.println("最后的MC1 list的tag："+ MC1List.get(i).getTag());
			System.out.println("最后的MC1 list的source："+ MC1List.get(i).getSource());
			System.out.println("最后的MC1 list的position："+ MC1List.get(i).getPos());
			System.out.println("最后的MC1 list的initScore："+ MC1List.get(i).getInitScore());
		}*/
		return MC1List;
	}
	
	
	public static double cosineSimilary(WordList wordList1, WordList wordList2)
	{
		//创建向量空间模型，使用map实现，主键为词项，值为长度为2的数组，存放着对应词项在字符串中的出现次数
		 Map<String, int[]> vectorMap = new HashMap<String, int[]>();
		//这个数组长度为2，用来存放当前分词在各个分词组中的个数，有则为当前分词组中的该分词的个数，没有则为0
		 int[] itemCountArray = null;
		 
		//由于在分词的时候已经对每一个文本进行了去重统计每个分词个数的处理，所以这里在一个分词组中不可能出现相同的分词
		 for(int i=0; i<wordList1.getWordListSize(); ++i)
		 {
			 itemCountArray = new int[2];			 
			 itemCountArray[0] = wordList1.getWord(i).getCount();
			 itemCountArray[1] = 0;
			 vectorMap.put(wordList1.getWord(i).getword(), itemCountArray);
		 }
		 
		 for (int i = 0; i < wordList2.getWordListSize(); i++) 
		 {
			 if (!vectorMap.containsKey(wordList2.getWord(i).getword())) {
				 itemCountArray = new int[2];
				 itemCountArray[0] = 0;
				 itemCountArray[1] = wordList2.getWord(i).getCount();
				 vectorMap.put(wordList2.getWord(i).getword(), itemCountArray);
				}else {
					itemCountArray = vectorMap.get(wordList2.getWord(i).getword());
					itemCountArray[1] = wordList2.getWord(i).getCount();
					vectorMap.put(wordList2.getWord(i).getword(), itemCountArray);
				}
		 }
		 
		 //计算相似度
		 double vector1Modulo = 0.00;//向量1的模
		 double vector2Modulo = 0.00;//向量2的模
		 double vectorProduct = 0.00; //向量积
		 Iterator iter = vectorMap.entrySet().iterator();
		 
		 while(iter.hasNext())
		 {
			 Map.Entry entry = (Map.Entry)iter.next();
			 itemCountArray = (int[])entry.getValue();
			 
			 vector1Modulo += itemCountArray[0]*itemCountArray[0];
			 vector2Modulo += itemCountArray[1]*itemCountArray[1];
			 
			 vectorProduct += itemCountArray[0]*itemCountArray[1];
		 }
		 
		 vector1Modulo = Math.sqrt(vector1Modulo);
		 vector2Modulo = Math.sqrt(vector2Modulo);
		 
		 //返回相似度
		return (vectorProduct/(vector1Modulo*vector2Modulo));
	}
	
	
	private static void followUpProcess(Result orgRes, Result curRes)
	{
		//原来计算文档分值的方法，忽略不计
		orgRes.setValue(orgRes.getValue() + curRes.getValue());
		//统计文档的次数
		orgRes.setCounts(orgRes.getCounts() + curRes.getCounts());
		//如果两个文档相同，则将当前文档的位置信息添加到位置list中
		orgRes.posList.add(curRes.getPos());
		//如果两个文档相同，则将当前文档的分数信息添加到分数list中
		orgRes.initScoreList.add(curRes.getInitScore());
		
		//orgRes.setSearchWeight(curRes.getSearchWeight());
		orgRes.searchWeightList.add(curRes.getSearchWeight());
		
		orgRes.setSource(lwyMergeSource(orgRes, curRes));
		
		//orgRes.setSource(orgRes.getSource()+" "+curRes.getSource());
	}
	
	/**
	 * 这个函数一定要在真正的去重函数后调用，即lwyMergeListDistinct这个函数,这个函数专门给MC1算法用
	 * 这个函数的目的是将去重后每个result的tag更新到map的key中
	 * @param oldList 调用separateResultToMultiList函数后得到的结果，存储不同成员结果列表HashMap结构的列表
	 */
	public static void findDuplicateDocu(List<HashMap<Integer, Result>> oldList)
	{
		List<HashMap<Integer, Result>> newList = new ArrayList<HashMap<Integer,Result>>();
		Map<String, Result> url2res = new HashMap<String, Result>();
		Map<String, Result>title2res = new HashMap<String, Result>();
		
		HashMap<Integer, Result> tempMap = new HashMap<Integer, Result>();
		/*newList.add(tempMap);
		
		Iterator<Result> iterRes = tempMap.values().iterator();
		//从第一个成员搜索引擎中获得唯一的URL和title
		while (iterRes.hasNext()) {
			Result curRes = iterRes.next();
			String url = curRes.getLink(), title = curRes.getTitle();
			if (null != url && !url2res.containsKey(url))
				url2res.put(url, curRes);
			if (null != title && !title2res.containsKey(title))
				title2res.put(title, curRes);
		}*/
		
		for (int i = 0; i < oldList.size(); i++) {
			tempMap = oldList.get(i);
			HashMap<Integer,Result> map = new HashMap<Integer, Result>();
			
			for (Iterator<Entry<Integer, Result>> newIterator = tempMap.entrySet().iterator();
					newIterator.hasNext();) {
				Entry<Integer, Result> entry = (Entry<Integer, Result>) newIterator.next();
				Result curResult = entry.getValue();
				Integer curTag = entry.getKey();
				if (curResult == null) {
					continue;
				}
				// 根据URL确定原来的结果中是否存在相同的结果；
				// URL无法确定时，标题相同的也被认为是相同的结果（不太合理，但对于微博结果有效）
				Result tarResult = url2res.get(curResult.getLink());
				if (null == tarResult)
					tarResult = title2res.get(curResult.getTitle());
				
				// tarResult不为空，表示当前结果已经出现过了,则把这个出现过的文档tag赋给curTag
				if (null != tarResult) {
					curTag = tarResult.getTag();						
				}else {
					url2res.put(curResult.getLink(), curResult);
					title2res.put(curResult.getTitle(), curResult);
				}
				map.put(curTag, curResult);
			}
			newList.add(map);
		}
		
		for (int i = 0; i < newList.size(); i++) {
			tempMap = newList.get(i);
			for (Iterator<Entry<Integer, Result>> newIterator = tempMap.entrySet().iterator();
					newIterator.hasNext();) {
				Entry<Integer, Result> entry = (Entry<Integer, Result>) newIterator.next();
				Integer curTag = entry.getKey();
				System.out.println("HashMap<Integer, Result> tag: " + curTag);
			}
		
			System.out.println("List<HashMap<Integer, Result>>: "+newList.get(i).size());
		}
	}
	/**
	 * 把两个列表中的结果合并为一个，去重并统计某一结果文档的来源以及某一结果文档在哪几个成员搜索引擎中出现的个数
	 * 
	 * @param orglist
	 *            目标列表
	 * @param newRes
	 *            待合成结果列表
	 */
	public static void lwyMergeListDistinct(List<Result> orglist, List<Result> newRes) {
		if (null == newRes || newRes.isEmpty() || null == orglist)
			return;

		Map<String, Result> url2res = new HashMap<String, Result>();
		Map<String, Result>title2res = new HashMap<String, Result>();
		
		//好像没什么用
		Iterator<Result> iterRes = orglist.iterator();
		while (iterRes.hasNext()) {
			Result curRes = iterRes.next();
			String url = curRes.getLink(), title = curRes.getTitle();
			if (null != url && !url2res.containsKey(url))
				url2res.put(url, curRes);
			if (null != title && !title2res.containsKey(title))
				title2res.put(title, curRes);
		}
		//虽然没有明显的代码对重复的文档有setTag的操作，但其实在存到orgList的过程中已经舍弃了重复的tag
		List<Result> resultList = new ArrayList<Result>();
		for (Iterator<Result> itnew = newRes.iterator(); itnew.hasNext();) {
			Result curRes = itnew.next();
			resultList.clear();
			if (null == curRes)
			{
				continue;
			}
			// 根据URL确定原来的结果中是否存在相同的结果；
			// URL无法确定时，标题相同的也被认为是相同的结果（不太合理，但对于微博结果有效）
			Result orgRes = url2res.get(curRes.getLink());
			if (null == orgRes)
				orgRes = title2res.get(curRes.getTitle());

			// orgRes不为空，表示当前结果已经出现过了
			if (null != orgRes) {
				//原来计算文档分值的方法，忽略不计
				orgRes.setValue(orgRes.getValue() + curRes.getValue());
				//统计文档的次数
				orgRes.setCounts(orgRes.getCounts() + curRes.getCounts());
				//如果两个文档相同，则将当前文档的位置信息添加到位置list中
				orgRes.posList.add(curRes.getPos());
				//如果两个文档相同，则将当前文档的分数信息添加到分数list中
				orgRes.initScoreList.add(curRes.getInitScore());
				
				orgRes.setSource(lwyMergeSource(orgRes, curRes));
				//结果来源的拆分
				orgRes.setArray(orgRes.getSource());
				String orgAbstr = orgRes.getAbstr();
				
				if (null == orgAbstr || orgAbstr.isEmpty()) {
					String newAbstr = curRes.getAbstr();
					if (null != newAbstr && !newAbstr.isEmpty())
						orgRes.setAbstr(newAbstr);
				}
			} else {
				url2res.put(curRes.getLink(), curRes);
				title2res.put(curRes.getTitle(), curRes);
				orglist.add(curRes);
				// 需要把结果从新列表中删除，否则下一次迭代的it.next()可能抛异常（ConcurrentModificationException）
				// 原因是在上面的if语句中，可能对结果进行修改
				// 如果修改的那条结果指向了newRes中的元素（orglist与newRes共享），就会导致这个异常（没有通过迭代器修改了newRes的元素）
				itnew.remove();
			}
		}
			
	}
	
	
	//寻找最长公共子串
	private static int getMaxString(String str1, String str2) 
	{
		String max =null;
		String min = null;
		max=(str1.length()>str2.length()?str1:str2);
		min=max.equals(str1)?str2:str1;
		for (int i = 0; i < min.length(); i++)
		{
			for(int start=0, end=min.length()-i;end != min.length()+1;start++,end++)
			{
				String sub = min.substring(start,end);
				if(max.contains(sub))
					return sub.length(); 
			}
		}
		return 0;
	}

	private static String lwyMergeSource(Result orgres, Result newres) {

		if (null == orgres || null == newres)
			return null;

		String ret = orgres.getSource();
		Set<String> orgEnames = orgres.getSourceEngineCnName(), newEnames = newres
				.getSourceEngineCnName();
		Map<String, String> enametoEstr = findEngineStr(newres);
		Iterator<String> iternn = newEnames.iterator();
		while (iternn.hasNext()) {
			String newName = iternn.next();
			if (!orgEnames.contains(newName))
				ret += " " + enametoEstr.get(newName);
		}

		return ret;
	}	
	
	private static Map<String, String> findEngineStr(Result result) {

		Map<String, String> ret = new HashMap<String, String>();

		if (null == result)
			return ret;
		String source = result.getSource();
		if (null == source || source.isEmpty())
			return ret;
		String[] srcStr = source.split(" ");
		if (null == srcStr || srcStr.length == 0)
			return ret;
		Set<String> srcEngName = result.getSourceEngineCnName();
		if (null == srcEngName)
			return ret;
		Iterator<String> iterEname = srcEngName.iterator();
		while (iterEname.hasNext()) {
			String curename = iterEname.next();
			for (int i = 0; i < srcStr.length; ++i) {
				if (srcStr[i].contains(curename))
					ret.put(curename, srcStr[i]);
			}
		}

		return ret;
	}
	
}
