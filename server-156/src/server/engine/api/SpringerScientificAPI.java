package server.engine.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.hp.hpl.jena.sparql.pfunction.library.listIndex;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.Elements;

import common.entities.searchresult.AcademicResult;
import common.entities.searchresult.Result;

public class SpringerScientificAPI {
	private String URL_BASE="http://link.springer.com";
	private String SEARCH_URL=URL_BASE+"/search/page/";
	public int getMyResults(List<AcademicResult> resultList, String query, int page,
			int timeout, int lastamount) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException{
		
		int amount=0;
		try {
			/*final WebClient webClient = new WebClient();
			final HtmlPage startPage = webClient.getPage(URL_BASE);*/
			query = query.replaceAll(" ", "+");
			SEARCH_URL = SEARCH_URL + page + "?query=" + query;
			URL url = new URL(SEARCH_URL);  
		    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();  
		    httpConn.setConnectTimeout(timeout);
		   /* InputStreamReader input = new InputStreamReader(httpConn  
		            .getInputStream(), "Unicode"); */
		    InputStreamReader input = new InputStreamReader(httpConn.getInputStream());
		    BufferedReader bufReader = new BufferedReader(input);  
		    String line = "";  
		    StringBuilder contentBuf = new StringBuilder();  
		    while ((line = bufReader.readLine()) != null) {  
		        contentBuf.append(line);  
		    }  
		    String result = contentBuf.toString();
		    int starti = result.indexOf("<ol id=\"results-list\" class=\"content-item-list\">");
		    result = result.substring(starti);
		    int endi = result.indexOf("</ol>");
		    result = result.substring(0,endi)+"</ol>";
//		    System.out.println("result:"+result);
		    String[] results = result.split("<li>");
		    for (String elem : results) {
		    	AcademicResult resultele = new AcademicResult();
//				System.out.println(elem.toString());
				int titlei = elem.indexOf("<a class=\"title\"");
				if(titlei==-1){
					continue;
				}
				elem = elem.substring(titlei);
				titlei = elem.indexOf("href=\"");
				elem = elem.substring(titlei+"href=\"".length());
				titlei = elem.indexOf("\"");
				String link = URL_BASE;
				link = link + elem.substring(0,titlei);
				resultele.setLink(link);
				titlei = elem.indexOf(">");
				elem = elem.substring(titlei);
				titlei = elem.indexOf("</a>");
				String title = "";
				title = elem.substring(1, titlei);
				title = splitAndFilterString(title, title.length());
//				System.out.println("title:"+title);	
				resultele.setTitle(title);
				elem = elem.substring(titlei);
				
				int absti = elem.indexOf("<p class=\"snippet\"");
				if(absti!=-1){
					elem = elem.substring(absti);
					absti = elem.indexOf(">");	
					elem = elem.substring(absti);
					absti = elem.indexOf("</p>");	
					String abstr ="";
					abstr = elem.substring(1,absti);
					abstr = splitAndFilterString(abstr, abstr.length());
					elem = elem.substring(absti);
					resultele.setAbstr(abstr);
//					System.out.println("abstr:"+abstr);
				}
				
				int auti = elem.indexOf("<span class=\"authors\"");
				if (auti!=-1) {
					String authors = "";
					elem = elem.substring(auti);
					auti = elem.indexOf(">");
					elem = elem.substring(auti);
					auti = elem.indexOf("</span>");
					String[] temp = elem.substring(1, auti).split("</a>");
					for (String author : temp) {
						int t = author.indexOf("<a");
						if (t!=-1) {
							t = author.indexOf(">");
							authors = authors + author.substring(t+1) +" ";
						}
					}
//					System.out.println("author:"+authors);
					elem = elem.substring(auti);
					resultele.setAuthors(authors);
				}
				
				int areai = elem.indexOf("<span class=\"enumeration\"");
				if (areai!=-1) {
					elem = elem.substring(areai);
					areai = elem.indexOf("<a");
					if (areai!=-1) {
						elem = elem.substring(areai);
						areai = elem.indexOf(">");
						elem = elem.substring(areai);
						areai = elem.indexOf("</a>");
						String area = "";
						area = elem.substring(1,areai);
//						System.out.println("area:"+area);
						elem = elem.substring(areai);
						resultele.setArea(area);
					}
				}
				
				int yeari = elem.indexOf("<span class=\"year\"");
				if (yeari!=-1) {
					elem = elem.substring(yeari);
					yeari = elem.indexOf(">");
					elem = elem.substring(yeari);
					yeari = elem.indexOf("</span>");
					String years = "";
					years = elem.substring(1,yeari);
					if (!years.equals("")) {
						years = years.substring(1, years.length()-1);
					}
//					System.out.println("year:"+years);
					resultele.setYear(years);
					resultele.setSource("Springer");
					resultele.setCiteCount(0);//因为Springer没有出现文章引用次数
					amount++;
					resultele.setValue(0.00);//每个结果的初始分数暂时设置为该结果在源列表中的位置顺序	
				}
				resultList.add(resultele);		
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
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
