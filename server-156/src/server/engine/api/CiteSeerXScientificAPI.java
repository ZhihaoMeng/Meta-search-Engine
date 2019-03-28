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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.MyStringChecker;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.sun.org.apache.xpath.internal.operations.Div;

import common.entities.searchresult.AcademicResult;

public class CiteSeerXScientificAPI {
	private String URL_BASE="http://citeseerx.ist.psu.edu";
	private String SEARCH_URL = URL_BASE + "/search?";
	public int getMyResults(List<AcademicResult> resultList, String query, int page,
			int timeout, int lastamount) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException{
		Document doc = null;
		int amount=0;
		try {
			/*final WebClient webClient = new WebClient();
			final HtmlPage startPage = webClient.getPage(URL_BASE);*/
			query = query.replaceAll(" ", "+");
			SEARCH_URL = SEARCH_URL+"q="+query+"&start="+10*(page-1)+"&submit=Search";
			URL url = new URL(SEARCH_URL);  
			doc = Jsoup.connect(SEARCH_URL).timeout(timeout).get();
			if (null == doc)
				return amount;
			Element list = doc.select("div#result_list").first();
			Elements tables = list.getElementsByClass("result");
			if (tables != null && tables.size() > 0) {
				for (Element relQuery : tables) {
					AcademicResult result = new AcademicResult(); 
					amount++;
					//title
					//link
					Element titleElement = relQuery.select("a.doc_details").first();
					String link = URL_BASE;
					link = link+titleElement.attr("href");
					String title = "";
					title = titleElement.text();
					result.setTitle(title);
					result.setLink(link);
//					System.out.println("link:"+link);
//					System.out.println("title:"+title);
					
					//abstr
					titleElement = null;
					String abstr = "No abstract is available for this item.";
					titleElement = relQuery.select("div.snippet").first();
					if(null!=titleElement){
						abstr = titleElement.text();
						abstr = abstr.replaceFirst("... ", "");
					}
//					System.out.println("abstr:"+abstr);
					result.setAbstr(abstr);
					
					//authors
					titleElement = null;
					String authors = "";
					titleElement = relQuery.select("span.authors").first();
					if(null!=titleElement){
						authors = titleElement.text();
						authors = authors.replaceFirst("by ", "");
					}
//					System.out.println("authors:"+authors);
					result.setAuthors(authors);
					
					//year
					titleElement = null;
					String year = "";
					titleElement = relQuery.select("span.pubyear").first();
					if(null!=titleElement){
						year = titleElement.text();
						year = year.replaceFirst(", ", "");
					}
//					System.out.println("year:"+year);
					result.setYear(year);
					
					//area
					titleElement = null;
					String area = "";
					titleElement = relQuery.select("span.pubvenue").first();
					if(null!=titleElement){
						area = titleElement.text();
						area = area.replaceFirst("- ", "");
						/**
						 * //该引擎的文章发表期刊往往后面是“in ... ENGINEERING (WISE.02 , 2000”
						 * 如果不对这个单引号进行处理，那么就会在正则表达式匹配的时候出现异常，因为没有完整的成对括号
						 */
						if(area.contains("(")){
							int index = area.indexOf("(");
							area = area.substring(0,index);
						}
					}
//					System.out.println("area:"+area);
					result.setArea(area);
					
					//citenum
					titleElement = null;
					String citenum = "";
					int cite = 0;
					titleElement = relQuery.select("a.citation").first();
					if(null!=titleElement){
						citenum = titleElement.text();//Cited by 1 (n self)
						if(citenum.contains("(")){
							citenum = citenum.substring(0, citenum.indexOf("("));
						}
						citenum = abstractNumFromString(citenum);
						if(MyStringChecker.isWhitespace(citenum)){
							//do nothing
						}else{
							cite = Integer.valueOf(citenum);
						}
					}
//					System.out.println("cite:"+cite);
					result.setCiteCount(cite);
					
					result.setSource("CiteSeerX");
					result.setValue(0.0);
					
					resultList.add(result);
				}
			}
		    /*String results = doc.toString();
		    System.out.println("results:"+results);    */
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
}
