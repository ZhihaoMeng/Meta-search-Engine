package common.functions.recommendation.click;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JaccardSimilarity {

	public static double userSimilarity(List<String> s1,List<String> s2)
	{
		if (s1.size() != s2.size()) {
			return Double.NaN;
		}
		//计算重合部分
		double ret = 0.0;
		List<String> intersection = new ArrayList<String>();
		for (String str1 : s1) {
			for (String str2 : s2) {
				if (! intersection.contains(str1)) {
					if (str1.equals(str2)) {
						intersection.add(str1);
					}
				}
			}
		}
		
		int unionSize = 1;
		
		if (s1.size() >= s2.size()) {
			unionSize = s1.size();
		}else{
			unionSize = s2.size();
		}
		
		ret = (double)intersection.size()/(double)unionSize;
		
		return ret;
	}
	
	public static double userSimilarity(Map<String,String> u1, Map<String,String> u2){
		if (u1.values().size() != u2.values().size()) {
			return Double.NaN;
		}
		List<String> userinfo1 = new ArrayList<>(u1.values());
		List<String> userinfo2 = new ArrayList<>(u2.values());
		//计算重合部分
		double ret = 0.0;
		List<String> intersection = new ArrayList<String>();
		for (String str1 : userinfo1) {
			for (String str2 : userinfo2) {
				if (! intersection.contains(str1)) {
					if (str1.equals(str2)) {
						intersection.add(str1);
					}
				}
			}
		}
		
		int unionSize = 1;
		
		if (u1.values().size() >= u2.values().size()) {
			unionSize = u1.size();
		}else{
			unionSize = u2.size();
		}
		
		ret = (double)intersection.size()/(double)unionSize;
		
		return ret;
	}
	
}
