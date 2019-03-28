package struts.actions.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import arq.examples.filter.classify;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import server.commonutils.MyStringChecker;
import server.engine.api.ACMScientificAPI;
import server.engine.api.BingAcaScientificAPI;
import server.engine.api.CiteSeerXScientificAPI;
import server.engine.api.EngineFactory;
import server.engine.api.SpringerScientificAPI;
import server.engine.api.EngineFactory.AcaSourceName;
import server.info.config.SessionAttrNames;
import server.info.config.LangEnvironment.LangEnv;
import server.info.entities.communication.RecommQueryAndPercent;
import server.threads.AcaRelateSearchThread;
import common.entities.searchresult.AcaResultPool;
import common.entities.searchresult.AcaResultPoolItem;
import common.entities.searchresult.AcademicResult;
import common.entities.searchresult.RelateSearchWord;
import common.functions.resultmerge.MergeAca;
import common.utils.querypreprocess.DealingWithQuery;
import db.dao.UserDao;
import db.dbhelpler.UserHelper;

public class AcademicSearchAction {
	private static final int NUM = 10;// 智搜网页中一页显示的结果数量，用于判断搜索结果是否已经足够
	private static final int TIMEOUT = 50000;
	private static final int SINGLE_NUM = 5;
	private int curPage;
	private String start,end;
	private UserDao userDao;// 用户数据库操作对象，其值由Spring通过setUserDao()注入，在beans.xml中关于bean
							// id="SearchAction"的bean中配置

	private CountDownLatch mDoneSignal;// 多线程互斥信号
	// private ResultPoolItem m_sdcResultCache;//搜索结果缓存

	/* 下面这些值是直接保存客户端传来的数据，这些值在相应的set函数中不解析，因为有时候这种解析是可以推迟的，例如查询词为空 */
	private String m_strQuery;// 查询词，值由struts2通过调用setQuery()函数来设置
	private String m_strPage;// 客户端传来的页码信息
	private String m_strLang;
	private String rankFilter;
	private String autoRank;
	private boolean m_bSearchResultEngouth;

	private int m_nPage;
	private LangEnv m_enuLang;
	boolean m_bIsLogin;// 用户是否已经登录
	private int m_nUserid;// 通过用户名确定的ID
	private String m_strUsername;
	
	private List<AcademicResult> results;// 用来存放最终要显示的结果，包括点击推荐结果及普通结果
	private List<AcademicResult> allResult;// 包括了从第1条到最后1条的所有搜索引擎的结果（结果合成之后的）
	private List<RelateSearchWord> m_lsRelateSearchResult;// 相关搜索结果
	private List<RecommQueryAndPercent> m_lsQueryRecomResult;// 查询词推荐结果
	private List<AcademicResult> acmresults;
	private List<AcademicResult> springerresults;
	private List<AcademicResult> bingacaresults;
	private List<AcademicResult> citeSeerXresults;
	private HttpSession m_httpCurSession;
	private AcaResultPoolItem m_sdcResultCache;//搜索结果缓存
	private TreeMap<String, Integer> timeFilter;
	public void setQuery(String query) {
		/*
		 * 解决查询串中包含特殊字符时搜索导致系统抛出异常的问题，但未解决输入单个字符造成异常的问题
		 * 这里主要是因为系统某些地方（涉及对查询词的处理，例如把查询词用来构造JSON对象等）操作不规范，导致相应的出现问题。
		 * 如果能够把系统其余地方对查询词的处理做正确的检查，这种强行修改查询词的做法是不应该使用的
		 */
		m_strQuery = DealingWithQuery.CorrectQuery(query);
	}

	public void setPage(String page) {
		m_strPage = page;
		parseClientParams();
	}

	public void setLang(String lang) {
		m_strLang = lang;
	}

	/* 由struts2在执行jsp页面时调用 */
	public String getUsername() {
		return m_strUsername;
	}

	public int getUserid() {
		return m_nUserid;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		if(!MyStringChecker.isWhitespace(start)){
			start = exactNfromS(start);
		}
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		if(!MyStringChecker.isWhitespace(end)){
			end = exactNfromS(end);
		}
		this.end = end;
	}

	public String getQuery() {
		return m_strQuery;
	}

	public String getRankFilter() {
		return rankFilter;
	}

	public void setRankFilter(String rankFilter) {
//		System.out.println("rankFilter:"+rankFilter);
		this.rankFilter = rankFilter;
	}

	public String getAutoRank() {
		return autoRank;
	}

	public void setAutoRank(String autoRank) {
		this.autoRank = autoRank;
	}

	public List<RelateSearchWord> getRelatedSearch() {
		if (null == m_lsRelateSearchResult)
			m_lsRelateSearchResult = new LinkedList<RelateSearchWord>();
		return m_lsRelateSearchResult;
	}
	
	public List<RelateSearchWord> setRelatedSearch(List<RelateSearchWord> nset) {
		getRelatedSearch();
		if (null == nset)
			return m_lsRelateSearchResult;
		m_lsRelateSearchResult.addAll(nset);
		return m_lsRelateSearchResult;
	}

	public int getPage() {
		return m_nPage;
	}

	public LangEnv getLangEnv() {
		return m_enuLang;
	}

	public List<AcademicResult> getResults() {
		if (null == results)
			results = new LinkedList<AcademicResult>();
		return results;
	}
	public List<AcademicResult> getAllResult() {
		if(null==allResult) allResult=new LinkedList<AcademicResult>();
		return allResult;
	}
	public List<RecommQueryAndPercent> getQueryRecomResult() {
		if (null == m_lsQueryRecomResult)
			m_lsQueryRecomResult = new LinkedList<RecommQueryAndPercent>();
		return m_lsQueryRecomResult;
	}

	public Set<AcaSourceName> getEngineOfResult() {

		Set<AcaSourceName> ret = new HashSet<AcaSourceName>();
		if (null == results)
			return ret;
		for (Iterator<AcaSourceName> iter = EngineFactory
				.getAllAcaEngineIterator(); iter.hasNext();) {
			AcaSourceName curEng = iter.next();
			for (Iterator<AcademicResult> itRes = results.iterator(); itRes
					.hasNext();) {
				AcademicResult res = itRes.next();
				if (res.isFromTargetSource(curEng)) {
					ret.add(curEng);
					break;
				}
			}
		}
		return ret;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * 点击搜索按钮后执行，负责获取搜索结果展示页面所需要的所有数据
	 * 
	 * @return： 表示搜索执行的结果情况的字符串
	 */
	public String execute() throws Exception {

		try {		
			// 即使不是搜索请求，语言环境、用户名信息也是必须要解析的
			m_httpCurSession = ServletActionContext.getRequest().getSession();
			m_enuLang = WebParamParser.parseLang(m_strLang, m_httpCurSession);
			m_strUsername = (String) m_httpCurSession
					.getAttribute(SessionAttrNames.USERNAME_ATTR);
			m_nUserid = UserHelper.getUserIDByUsername(m_strUsername);
			m_bIsLogin = UserHelper.isLoginUser(m_nUserid);
			if (MyStringChecker.isWhitespace(m_strQuery)) {
				return "search";
			}
			
			getAndCheckSearchResultCache();
			System.out.println("m_bSearchResultEngouth is:" + m_bSearchResultEngouth);
			if (!m_bSearchResultEngouth) {
				mDoneSignal = new CountDownLatch(SINGLE_NUM);
				searchForResults();
			}else{
				mDoneSignal = new CountDownLatch(1);
			}
			
			relatedSearch();
			
			waitForDone();
			
			if (!m_bSearchResultEngouth) {
				MergeAca.acaResultMerge(getAllResult(), acmresults, m_nUserid, m_strQuery, m_bIsLogin);
				MergeAca.acaResultMerge(getAllResult(), springerresults, m_nUserid, m_strQuery, m_bIsLogin);
				MergeAca.acaResultMerge(getAllResult(), bingacaresults, m_nUserid, m_strQuery, m_bIsLogin);
				MergeAca.acaResultMerge(getAllResult(), citeSeerXresults, m_nUserid, m_strQuery, m_bIsLogin);
				List<AcademicResult> tempList = m_sdcResultCache.getSearchResultList();
	
				tempList.addAll(getAllResult());
				MergeAca.acaResultSort(tempList, m_nUserid, m_strQuery, m_bIsLogin,autoRank);
				MergeAca.filtRestultFromStartToEnd(start,end,tempList);
				if (rankFilter.equalsIgnoreCase("None")) {
					//do nothing
				}else{
					MergeAca.sortAcaResultByFilter(rankFilter, tempList);
				}
				
				m_sdcResultCache.setSearchResultList(tempList);
				m_sdcResultCache.setQuery(m_strQuery);
			}else{
				if (null==allResult) {
					allResult = new ArrayList<AcademicResult>();
				}
				allResult = m_sdcResultCache.getSearchResultList();
				MergeAca.filtRestultFromStartToEnd(start, end, allResult);
				
				if (rankFilter.equalsIgnoreCase("None")) {
					//do nothing
				}else{
					MergeAca.sortAcaResultByFilter(rankFilter, allResult);
				}
				if(MyStringChecker.isWhitespace(autoRank)){
					//do nothing
				}else{
					MergeAca.acaResultSort(allResult, m_nUserid, m_strQuery, m_bIsLogin,autoRank);
				}
				
			}
//			System.out.println("start:"+start+" end:"+end);
			
			setTimeFilt();
			setSearchResults();
//			System.out.println("academic search page is:" + m_nPage);
//			System.out.println("realpage is:"+curPage);
			AcaResultPool.releaseItem(m_sdcResultCache, m_nUserid, m_strQuery);

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	private void setTimeFilt() {
		if (null == timeFilter) {
			timeFilter = new TreeMap<String,Integer>();
		}
		List<AcademicResult> tempList = getAllResult();
		timeFilter.put("2016年以来", 0);
		timeFilter.put("2015年以来", 0);
		timeFilter.put("2014年以来", 0);
		timeFilter.put("2013年之前", 0);
		for (AcademicResult elem : tempList) {
			String year = elem.getYear();
			int yeari = 0;
			if (!MyStringChecker.isWhitespace(year)) {
				yeari = Integer.valueOf(year);
			}
			
			if(yeari>=2016){
				timeFilter.put("2016年以来", timeFilter.get("2016年以来")+1);
			}else if(yeari>=2015){
				timeFilter.put("2015年以来", timeFilter.get("2015年以来")+1);
			}else if(yeari>=2014){
				timeFilter.put("2014年以来", timeFilter.get("2014年以来")+1);
			}else{
				timeFilter.put("2013年之前", timeFilter.get("2013年之前")+1);
			}
		}
		timeFilter = sortMapByKey(timeFilter);
		System.out.println("timeFilter size is:"+timeFilter.size());
	}
	
	public TreeMap<String, Integer> getTimeFilter() {
		if (null == timeFilter) {
			timeFilter = new TreeMap<String, Integer>();
		}
		
		return timeFilter;
	}

	private TreeMap<String, Integer> sortMapByKey(
			TreeMap<String, Integer> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		TreeMap<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());

		sortMap.putAll(map);

		return sortMap;
	}

	private void setSearchResults() {
		List<AcademicResult> tmpRlist = m_sdcResultCache.getSearchResultList();
		
		int startIndex = (m_nPage - 1) * NUM, outOfEndIndex = startIndex + NUM, resAmount=tmpRlist.size();
		if(outOfEndIndex>=resAmount){
			outOfEndIndex=resAmount;
			if(outOfEndIndex<startIndex){
				m_nPage=resAmount/NUM+1;
				startIndex=(m_nPage-1)*NUM;
			}
		}
		
		getResults().addAll(tmpRlist.subList(startIndex, outOfEndIndex));
		
	}

	private void getAndCheckSearchResultCache(){
		m_sdcResultCache=AcaResultPool.getResultListItem(m_nUserid,m_strQuery);
		
		if (m_nPage*NUM <= 0)
			m_bSearchResultEngouth = true;
		List<AcademicResult> tempList = m_sdcResultCache.getSearchResultList();
		if (null == tempList || tempList.isEmpty()){
			m_bSearchResultEngouth = false;
		}else{
			if (tempList.size()>=m_nPage*NUM) {
				m_bSearchResultEngouth = true;
			}else{
				m_bSearchResultEngouth = false;
			}
		}
		curPage = m_sdcResultCache.getCurPage();
	}

	private void searchForResults() {
		// TODO Auto-generated method stub
		if(!m_bSearchResultEngouth){
			
			acmresults = new ArrayList<AcademicResult>();
			springerresults = new ArrayList<AcademicResult>();
			bingacaresults = new ArrayList<AcademicResult>();
			citeSeerXresults = new ArrayList<AcademicResult>();
			
			SearchFromSpringer thread1 = new SearchFromSpringer();
			thread1.run();
			
			SearchFromACM thread2 = new SearchFromACM();
			thread2.run();
			
			SearchFromBingAca thread3 = new SearchFromBingAca();
			thread3.run();
			
			SearchFromCiteSeerXAca thread4 = new SearchFromCiteSeerXAca();
			thread4.run();
			
			m_sdcResultCache.pageIncrease();
		}else{
			//搜索缓存中的数量已经足够了，不再向搜索Agent发消息
			getAllResult().addAll(m_sdcResultCache.getSearchResultList());
			
		}
		
	}

	private void parseClientParams() {
		/*
		 * 注： 网页版中，用户可以随意修改HTML元素及URL，
		 * 因此，网页版解析搜索引擎名字之类客户端传来的信息时，必须用tryParseString方式；
		 * 同时，URL中相同名字的参数不一定只出现一次，如果多次出现，传入的字符串将是多次出现的值以逗号拼接而成（struts2做的），
		 * 为了服务器端稳定，这里解析时必须做好异常处理，考虑客户端参数异常时应如何对待，
		 * 保证系统内部工作时，客户端的数据已经被正确的转换为内部数据类型（如枚举）
		 * 对于安卓端，虽然用户改动传递的参数没有网页版这么容易，不过还是应该做同样的异常处理
		 */
		parsePage();
	}

	/**
	 * 解析客户端传递的页码参数，如果存在多个page参数，以第一个能够非空且正确的值为准
	 */
	private void parsePage() {

		m_nPage = 1;
		if (null == m_strPage)
			return;
		String arr[] = m_strPage.split(",");
		if (null != arr && arr.length >= 1) {
			for (int i = 0; i < arr.length; ++i) {
				String curPage = arr[i].trim();
				if (MyStringChecker.isBlank(curPage))
					continue;
				try {
					m_nPage = Integer.parseInt(arr[0].trim());
				} catch (Exception e) {
					continue;
				}
				break;
			}
		}
	}

	private void relatedSearch() {
		List<RelateSearchWord> relatewords = m_sdcResultCache.getRelateSearchWords();
		if(relatewords.size()>0){
			setRelatedSearch(relatewords);
			mDoneSignal.countDown();
		}else{
			AcaRelateSearchThread relSThread = new AcaRelateSearchThread(
					getRelatedSearch(), mDoneSignal, m_strQuery);
			relSThread.start();
		}
	}

	private void waitForDone() {

		if (null != mDoneSignal)
			try {
				mDoneSignal.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	class SearchFromSpringer extends Thread{
		 @Override
	        public void run() {
			 SpringerScientificAPI springer = new SpringerScientificAPI();
			 try {
				int amount = springer.getMyResults(springerresults, m_strQuery, curPage, TIMEOUT, 0);
//				System.out.println("the result amount is:" + amount);
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 mDoneSignal.countDown();
	        }
	}
	class SearchFromACM extends Thread{
		 @Override
	        public void run() {
			 ACMScientificAPI springer = new ACMScientificAPI();
			 try {
				int amount = springer.getMyResults(acmresults, m_strQuery, curPage, TIMEOUT, 0);
//				System.out.println("the result amount is:" + amount);
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 mDoneSignal.countDown();
	        }
	}
	class SearchFromBingAca extends Thread{
		 @Override
	        public void run() {
			 BingAcaScientificAPI springer = new BingAcaScientificAPI();
			 try {
				int amount = springer.getMyResults(bingacaresults, m_strQuery, curPage, TIMEOUT, 0);
//				System.out.println("the result amount is:" + amount);
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 mDoneSignal.countDown();
	        }
	}
	class SearchFromCiteSeerXAca extends Thread{
		@Override
        public void run() {
		 CiteSeerXScientificAPI citeSeerX = new CiteSeerXScientificAPI();
		 try {
			int amount = citeSeerX.getMyResults(bingacaresults, m_strQuery, curPage, TIMEOUT, 0);
//			System.out.println("the result amount is:" + amount);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 mDoneSignal.countDown();
        }
	}
	private String exactNfromS(String s){
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(s);   
		return m.replaceAll("").trim();
	}
}

class MapKeyComparator implements Comparator<String>{

	@Override
	public int compare(String str1, String str2) {
		
		return str2.compareTo(str1);
	}
}