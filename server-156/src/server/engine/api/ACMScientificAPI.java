package server.engine.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import common.entities.searchresult.AcademicResult;

public class ACMScientificAPI {
	private String URL_BASE="http://dl.acm.org/";
	private String SEARCH_URL = URL_BASE + "results.cfm?";
	public int getMyResults(List<AcademicResult> resultList, String query, int page,
			int timeout, int lastamount) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException{
		
		int amount=0;
		try {
			/*final WebClient webClient = new WebClient();
			final HtmlPage startPage = webClient.getPage(URL_BASE);*/
			query = query.replaceAll(" ", "+");
			SEARCH_URL = SEARCH_URL+"query="+query+"&start="+20*(page-1);
			URL url = new URL(SEARCH_URL);  
		    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(); 
		    httpConn.setRequestProperty("User-Agent",
	                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

	        InputStreamReader input = new InputStreamReader(httpConn  
		            .getInputStream(), "utf-8");  
		    BufferedReader bufReader = new BufferedReader(input);  
		    String line = "";  
		    StringBuilder contentBuf = new StringBuilder();  
		    while ((line = bufReader.readLine()) != null) {  
		        contentBuf.append(line);  
		    }  
		    
		    String results = contentBuf.toString();
		    
		    int resulti = contentBuf.indexOf("<div id=\"results\">");
		    if(resulti!=-1){
		    	results = results.substring(resulti);
		    	resulti = results.indexOf("<div class=\"footerbody\"");
		    	if (resulti!=-1) {
		    		results = results.substring(0,resulti);
				}
		    }
//		    System.out.println("resultlist:"+results);
		    String[] result_li = results.split("<div class=\"details\">");
		    for (String elem : result_li) {
//		    	System.out.println("elem:"+elem);
		    	AcademicResult result = new AcademicResult();
				int titlei = elem.indexOf("<div class=\"title\"");
				if(titlei==-1){
					continue;
				}
				elem = elem.substring(titlei);
				titlei = elem.indexOf("<a");
				elem = elem.substring(titlei);
				titlei = elem.indexOf("href=\"");
				elem = elem.substring(titlei+"href=\"".length());
				titlei = elem.indexOf("\"");
				String link = URL_BASE;
				link = link+elem.substring(0,titlei);
				result.setLink(link);
				
				titlei = elem.indexOf(">");
				elem = elem.substring(titlei);
				titlei = elem.indexOf("</a>");
				String title = "";
				title = elem.substring(1,titlei).trim();
				result.setTitle(title);
//				System.out.println("title:"+title);
				
				//作者信息
				int authi = elem.indexOf("<div class=\"authors\"");
				if(titlei==-1){
					continue;
				}
				elem = elem.substring(authi);
				authi = elem.indexOf("<div class=\"source\">");
				String author = elem.substring(0,authi);
				String authors = "";
				String[] alist = author.split("</a>");
				for (String au : alist) {
					int t = au.indexOf("<a");
					if (t!=-1) {
						au = au.substring(t);
						t = au.indexOf(">");
						au = au.substring(t);
						authors = authors + au.substring(1)+" ";
					}
				}	
				result.setAuthors(authors);
//				System.out.println("authors:"+authors);
				
				//year
				authi = elem.indexOf("<span class=\"publicationDate\"");
				String year = "";
				if (authi!=-1) {
					elem = elem.substring(authi);
					authi = elem.indexOf(">");
					elem = elem.substring(authi);
					authi = elem.indexOf("</span>");
					
					year = elem.substring(1,authi); 
					year = abstractNumFromString(year);
//					System.out.println("year:"+year);
				}
				result.setYear(year);
				
				//area
				authi = elem.indexOf("<span style=\"padding-left:10px\"");
				String area = "";
				if (authi!=-1) {
					elem = elem.substring(authi);
					authi = elem.indexOf(">");
					elem = elem.substring(authi);
					authi = elem.indexOf("</span>");
					
					area = elem.substring(1,authi); 
//					System.out.println("area:"+area);
				}
				result.setArea(area);
				
				//citation count
				authi = elem.indexOf("Citation Count:");
				int cou = 0;
				if (authi!=0) {
					elem = elem.substring(authi);
					authi = elem.indexOf("</span>");
					String count = "";
					count = elem.substring(0,authi); 
					count = abstractNumFromString(count);
					
					if (!count.equals("")) {
						cou = Integer.valueOf(count);
					}
					
//					System.out.println("citation count:"+cou);
				}
				result.setCiteCount(cou);
				
				//abstr
				authi = elem.indexOf("<div class=\"abstract\"");
				String abstr = "";
				if (authi!=-1) {
					elem = elem.substring(authi);
					authi = elem.indexOf(">");
					elem = elem.substring(authi);
					authi = elem.indexOf("</div>");
					
					abstr = elem.substring(1,authi).trim(); 
					
//					System.out.println("abstr:"+abstr);
				}
				result.setAbstr(abstr);
				result.setSource("ACM");
				amount++;
				result.setValue(0.0);
				resultList.add(result);
		    }
		    
//		    System.out.println("results:"+results);    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}
	public static String abstractNumFromString(String s){
		String regEx="[^0-9]";   //使用正则表达式提取字符串中的数字部分
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(s);   
		s = m.replaceAll("").trim();
		return s;
	}
	public static String splitAndFilterString(String input, int length) {  
        if (input == null || input.trim().equals("")) {  
            return "";  
        }  
        // 去掉所有html元素,  
        String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(  
                "<[^>]*>", "");  
        str = str.replaceAll("[(/>)<]", "");  
        int len = str.length();  
        if (len <= length) {  
            return str;  
        } else {  
            str = str.substring(0, length);  
            str += "......";  
        }  
        return str;  
    }  
}
