package server.engine.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.omg.CORBA.TIMEOUT;

import common.entities.searchresult.RelateSearchWord;
import server.commonutils.JsoupUtil;
import server.commonutils.MyStringChecker;

public class AcaRelateSearchWordFromBaiduAca {
	private final static String URL_BASE = "http://xueshu.baidu.com/s?ie=utf-8&tn=SE_baiduxueshu_c1gjeupa";
	private static int TIMEOUT = 30000;

	// wd=academic&
	public static int getRelatedSearch(List<RelateSearchWord> ret, String query) {

		int amount = 0;
		if (null == ret || MyStringChecker.isBlank(query))
			return amount;
		query = query.replaceAll(" ", "+");
		String url = URL_BASE + "&wd=" + query;
		Document doc = null;
		try {
			doc = JsoupUtil.getHtmlDocument(url);
			if (null == doc)
				return amount;
			Elements tables = doc.select("div#con-ar");
			if (null == tables || tables.isEmpty())
				return amount;
			Element content = tables.first();
			if (null == content)
				return amount;
			Elements contentlist = content.getElementsByTag("a");
			if (contentlist != null && contentlist.size() > 0) {
				for (Element relQuery : contentlist) {
					if(amount>=10)
						return amount;
					Elements childs = relQuery.children();
					if (null == childs || childs.isEmpty())
						return amount;
					Element numbere = childs.first();
					String num = JsoupUtil.getTextInHtml(numbere);
					int finNum = -1;
					if (isNumeric(num) || !MyStringChecker.isWhitespace(num)) {//在搜索某些关键词时，百度学术的相关搜索后面会出现相关学者的信息，这时候num拿到的是学者的所在学校或者单位，并不是检索的数目，因此需要判断一下
						finNum = Integer.valueOf(num.split(" ")[0]);
					}
					Element titlee = childs.last();
					String title = JsoupUtil.getTextInHtml(titlee);
					RelateSearchWord elem = new RelateSearchWord(MyStringChecker.isBlank(title)?"":title,
							MyStringChecker.isBlank(num)?-1:finNum);
					ret.add(elem);
					++amount;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return amount;
	}
	/**
	 *判断给定字符串是否是数字字符串
	 * @param str
	 * @return 是纯数字字符串返回true，否则返回false
	 */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches(); 
	}

}
