package common.functions.resultmerge;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import common.entities.searchresult.AcademicResult;
import db.dbhelpler.UserHelper;
import server.commonutils.MyStringChecker;
import server.info.config.ConfigFilePath;

public class MergeAca {
	private static Map<String, Double> impact_factor = new HashMap<String, Double>();
	private final static String FILEPATH = ConfigFilePath.getProjectRoot()+"WEB-INF/classes/impact-factor.xml";
	static {
		File factFile = new File(FILEPATH);
		if (factFile.exists()) {
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(factFile);
				Element root = doc.getRootElement();
				Element foo;
				for (Iterator i = root.elementIterator("ImpactFactor"); i.hasNext();) {
						foo = (Element) i.next();
						String value = foo.elementText("value");
						impact_factor.put(foo.elementText("name"), Double.valueOf(value));
				}
			} catch (Exception e) {

			}
		}
	}
	public static void acaResultMerge(List<AcademicResult> tarlist,
			List<AcademicResult> newRes, int userid, String query,
			boolean isLogin) {

		if (null == tarlist || null == newRes || newRes.isEmpty()
				|| !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;
		
		MergeAca.mergeAcaListDistinct(tarlist, newRes);
	}
	
	public static void acaResultSort(List<AcademicResult> tarlist,int userid, String query,
			boolean isLogin,String autoRank) {

		if (null == tarlist || !UserHelper.isLegalUserID(userid)
				|| MyStringChecker.isBlank(query))
			return;
		if(MyStringChecker.isWhitespace(autoRank)){
			//计算年份分值（T=d2*year）,当year=0，时，T=0，
			MergeAca.getYearScore(tarlist);
			//计算引用分值（T=d3*citenum）
			MergeAca.getCiteScore(tarlist);
			//计算影响因子分值（T=d1*M）
			MergeAca.getFactorScore(tarlist);
			//计算相似度分值（利用查询串与标题之间的编辑距离）
			MergeAca.getLevenShtein(query,tarlist);
		}else{
			/*
			 * 1:引用次数
			 * 2:影响因子
			 * 3:发表年限
			 * 4:相似性
			 */
			//为防止之前分数的影响，需要先对分数归0化
			MergeAca.initResultList(tarlist);
			if (autoRank.contains("1")) {
				//计算引用分值（T=d3*citenum）
				MergeAca.getCiteScore(tarlist);
			}
			if (autoRank.contains("2")) {
				//计算影响因子分值（T=d1*M）
				MergeAca.getFactorScore(tarlist);
			}
			if (autoRank.contains("3")) {
				//计算年份分值（T=d2*year）,当year=0，时，T=0，
				MergeAca.getYearScore(tarlist);
			}
			if (autoRank.contains("4")) {
				//计算相似度分值（利用查询串与标题之间的编辑距离）
				MergeAca.getLevenShtein(query,tarlist);
			}
			
		}
		
		Collections.sort(tarlist,new MergeAcaSort());
		/*for (AcademicResult academicResult : tarlist) {
			System.out.println("title:"+academicResult.getTitle()+" score:"+academicResult.getValue());
		}*/
	}

	private static void initResultList(List<AcademicResult> tarlist) {
		for (AcademicResult academicResult : tarlist) {
			academicResult.setValue(0.0);
		}
	}

	private static void getLevenShtein(String query, List<AcademicResult> tarlist) {
		for (AcademicResult academicResult : tarlist) {
			String title = academicResult.getTitle();
			double value = academicResult.getValue();
			value = value + levenshtein(query, title);
			academicResult.setValue(value);
		}
	}

	private static void getFactorScore(List<AcademicResult> tarlist) {
		for (AcademicResult academicResult : tarlist) {
			String jcname = academicResult.getArea();
			double value = 0;
			if(MyStringChecker.isWhitespace(jcname)){
				//do nothing
			}else if(jcname.contains("conference")||jcname.contains("Conference")||jcname.contains("CONFERENCE")){
				//conference
				value = MergeAca.getConferenceYear(jcname);
				value = value*0.1;
			}else{
				//journal
				value = MergeAca.getFactorvaluebyName(jcname);
				value = value*2;
			}			
			
//			System.out.println("value"+value);
			academicResult.setValue(academicResult.getValue()+value);
		}
	}
	private static double getConferenceYear(String jcname) {
		double value = 1;
		String jcitems [] = jcname.split(" ");
		String regEx = "st|nd|th|rd";
		Pattern pattern = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE);
		for (int i = 0; i < jcitems.length; i++) {	
			Matcher matcher = pattern.matcher(jcitems[i]);
			boolean result = matcher.find();
			if(result){
				String sv = abstractNumFromString(jcitems[i]);
				if(null==sv || sv.equals("")){
				}else{
					value = Double.valueOf(sv);
					value = value;
				}
			}
		}
		return value;
	}

	private static double getFactorvaluebyName(String name){
		Set<Entry<String, Double>> entries = impact_factor.entrySet();
		for (Entry<String, Double> entry : entries) {
			if(entry.getKey().equalsIgnoreCase(name) || isContainsignoreupper(entry.getKey(), name)){				
				return entry.getValue();
			}
		}
		return 0;
	}

	private static void getCiteScore(List<AcademicResult> tarlist) {
		for (AcademicResult academicResult : tarlist) {
			double value = academicResult.getValue();
			double citei = 0.01*academicResult.getCiteCount();
			academicResult.setValue(value+citei);
		}
	}

	private static void getYearScore(List<AcademicResult> tarlist) {
		for (AcademicResult academicResult : tarlist) {
			double value = academicResult.getValue();
	
			String year = academicResult.getYear();
			
			double yeari = MyStringChecker.isWhitespace(year)?0:Double.valueOf(year);
			yeari = 0.001*yeari;
			academicResult.setValue(value+yeari);
		}
	}
	private static void mergeAcaListDistinct(List<AcademicResult> tarlist,
			List<AcademicResult> newRes) {
		if (null == newRes || newRes.isEmpty() || null == tarlist)
			return;

		Map<String, AcademicResult> url2res = new HashMap<String, AcademicResult>(), title2res = new HashMap<String, AcademicResult>();
		getUrlTitleToAcaResultMap(tarlist, url2res, title2res);
		for (Iterator<AcademicResult> itnew = newRes.iterator(); itnew
				.hasNext();) {
			AcademicResult curRes = itnew.next();
			if (null == curRes)
				continue;

			// 根据URL确定原来的结果中是否存在相同的结果；
			// URL无法确定时，标题相同的也被认为是相同的结果（不太合理，但对于微博结果有效）
			AcademicResult orgRes = url2res.get(curRes.getLink());
			if (null == orgRes)
				orgRes = title2res.get(curRes.getTitle());

			// 当前结果已经出现过了
			if (null != orgRes) {
				orgRes.setValue(orgRes.getValue() + curRes.getValue());
				orgRes.setSource(orgRes.getSource() + " " + curRes.getSource());
				String orgAbstr = orgRes.getAbstr();
				if (null == orgAbstr || orgAbstr.isEmpty()) {
					String newAbstr = curRes.getAbstr();
					if (null != newAbstr && !newAbstr.isEmpty())
						orgRes.setAbstr(newAbstr);
				}
			} else {
				url2res.put(curRes.getLink(), curRes);
				title2res.put(curRes.getTitle(), curRes);
				tarlist.add(curRes);
				// 需要把结果从新列表中删除，否则下一次迭代的it.next()可能抛异常（ConcurrentModificationException）
				// 原因是在上面的if语句中，可能对结果进行修改
				// 如果修改的那条结果指向了newRes中的元素（orglist与newRes共享），就会导致这个异常（没有通过迭代器修改了newRes的元素）
				itnew.remove();
			}
		}
	}
	private static void getUrlTitleToAcaResultMap(List<AcademicResult> rlist,
			Map<String, AcademicResult> url2res,
			Map<String, AcademicResult> title2res) {

		if (null == rlist || rlist.isEmpty() || url2res == null
				|| null == title2res)
			return;

		Iterator<AcademicResult> iterRes = rlist.iterator();
		while (iterRes.hasNext()) {
			AcademicResult curRes = iterRes.next();
			String url = curRes.getLink(), title = curRes.getTitle();
			if (null != url && !url2res.containsKey(url))
				url2res.put(url, curRes);
			if (null != title && !title2res.containsKey(title))
				title2res.put(title, curRes);
		}

		return;
	}
	public static void sortAcaResultByFilter(final String filter, List<AcademicResult> list){
		
		
		Comparator<AcademicResult> comparator = new Comparator<AcademicResult>() {
            public int compare(AcademicResult s1, AcademicResult s2) {
            	String ss1 = "",ss2="";
            	if (filter.equalsIgnoreCase("Relevance")) {
            		return s2.getCiteCount()-s1.getCiteCount();
        		}else if (filter.equalsIgnoreCase("Newest First")) {
        			ss1 = s2.getYear();
        			ss2 = s1.getYear();
        		}else if (filter.equalsIgnoreCase("Oldest First")) {
        			ss1 = s1.getYear();
        			ss2 = s2.getYear();
        		}
            	return ss1.compareTo(ss2);
            }
        };
        Collections.sort(list, comparator);
	}

	public static void filtRestultFromStartToEnd(String start, String end,
			List<AcademicResult> tempList) {
		int starti = 0;
		int endi = 0;
		if(MyStringChecker.isWhitespace(start) && MyStringChecker.isWhitespace(end)){
			return;
		}else if(MyStringChecker.isWhitespace(start)){
			starti = 0;
			endi = Integer.valueOf(end);
		}else if(MyStringChecker.isWhitespace(end)){
			endi = 0;
			starti = Integer.valueOf(start);
		}else{
			starti = Integer.valueOf(start);
			endi = Integer.valueOf(end);
		}
		for(int i=0;i<tempList.size();i++){
			AcademicResult academicResult = tempList.get(i);
			String year = academicResult.getYear();
			int yeari = Integer.valueOf(year);
			if(yeari>=starti && yeari<=endi){
				// do nothing
			}else{
				tempList.remove(academicResult);
				i--;
			}
		}
	}
	private static boolean isContainsignoreupper(String s,String t){
		boolean answer = false;
		//这里需要注意有些s 或者t是空串的情况
		if(!MyStringChecker.isWhitespace(s) && !MyStringChecker.isWhitespace(t)){
			Pattern pattern = Pattern.compile(t,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(s);
			if(matcher.find()){
				answer = true;
			}
		}		
		return answer;
	}
	public static String abstractNumFromString(String s){
		String regEx="[^0-9]";   //使用正则表达式提取字符串中的数字部分
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(s);   
		s = m.replaceAll("").trim();
		return s;
	}
	//求两个字符串之间的编辑距离
	public static float levenshtein(String str1, String str2) {

		int len1 = str1.length();
		int len2 = str2.length();

		int[][] dif = new int[len1 + 1][len2 + 1];

		for (int a = 0; a <= len1; a++) {
			dif[a][0] = a;
		}
		for (int a = 0; a <= len2; a++) {
			dif[0][a] = a;
		}
		
		int temp;
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					temp = 0;
				} else {
					temp = 1;
				}
				// 取三个值中最小的
				dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
						dif[i - 1][j] + 1);
			}
		}
	
		// 计算相似度
		float similarity = 1 - (float) dif[len1][len2]
				/ Math.max(str1.length(), str2.length());
		return similarity;
	}
	private static int min(int... is) {
		int min = Integer.MAX_VALUE;
		for (int i : is) {
			if (min > i) {
				min = i;
			}
		}
		return min;
	}

}
