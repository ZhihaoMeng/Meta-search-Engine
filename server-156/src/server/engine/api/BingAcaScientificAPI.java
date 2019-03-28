package server.engine.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import server.commonutils.HtmlSpecialCharUtil;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import common.entities.searchresult.AcademicResult;

public class BingAcaScientificAPI {
	private String URL_BASE = "http://cn.bing.com";
	private String SEARCH_URL = URL_BASE + "/academic/search?";

	// 必应学术用first参数描述页数，first=10*(page-1)+1;page表示第几页，first表示page页使用的页面请求参数的数值
	public int getMyResults(List<AcademicResult> resultList, String query,
			int page, int timeout, int lastamount)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {

		int amount = 0;
		Document doc = null;
		int curpage = 1 + 10 * (page - 1);
		try {
			doc = Jsoup.connect(SEARCH_URL).data("q", query)
					.data("first", curpage + "").timeout(timeout).get();
			if (null == doc)
				return amount;
			Elements tables = doc.select("li.b_algo");
			if (tables != null && tables.size() > 0) {

				// System.out.println("num:"+tables.size());
				for (Element relQuery : tables) {
					AcademicResult result = new AcademicResult();
					Element title1 = relQuery.getElementsByTag("h2").first();
					Element title2 = title1.getElementsByTag("a").first();
					String title3 = title2.text();
					String link = title2.attributes().get("href");
					link = URL_BASE + link;
					result.setTitle(title3);
					result.setLink(link);
					// System.out.println("title:"+title3);
					// System.out.println("link:"+link);

					Elements snippet = relQuery.select("div.b_snippet");
					Elements abstr = snippet.first().getElementsByTag("p");
					String abstr2 = "No abstract is available for this item.";
					if (abstr != null) {
						Element abstr1 = abstr.first();
						if (abstr1 != null) {
							abstr2 = "";
							abstr2 = abstr1.text();
						}
					}
					result.setAbstr(abstr2);
					// System.out.println("abstr:"+abstr2);

					Elements facts = relQuery.select("div.aca_dev4_facts");
					// System.out.println("facts size:"+facts.size());
					String auths = "";
					String year = "";
					String area = "";
					int cit = -1;
					for (int i = 0; i < facts.size(); i++) {
						Elements cur4facts = facts.get(i).children();
						// System.out.println("cur4facts size:"+cur4facts.size());
						for (int j = 0; j < cur4facts.size(); j++) {
							Element item = cur4facts.get(j);
							if (j == 0) {
								Element author = item.select("div.b_factrow")
										.first();
								Elements authors = author.select("a");

								for (int k = 0; k < authors.size() && k < 3; k++) {// 这里只取前三个作者
									Element element = authors.get(k);
									auths = auths + element.text() + " ";
								}
								// System.out.println("auths:"+auths);
							}
							if (j == 1) {
								Element areaall = item.select("div.b_factrow")
										.first();

								area = areaall.text();

								year = abstractNumFromString(area);
								int index = area.indexOf('·');
								area = area.substring(0, index);
								// System.out.println("area:"+area);
								// System.out.println("year:"+year);
							}
							if (j == 2) {
								// 不提取关键字
							}
							if (j == 3) {
								Element cite = item.select("div.b_hPanel")
										.first();
								String citation = "";
								citation = cite.text();
								citation = abstractNumFromString(citation);
								cit = Integer.valueOf(citation);
								// System.out.println("citation:"+citation);
							}
						}
					}
					++amount;
					result.setAuthors(auths);
					result.setArea(area);
					result.setYear(year);
					result.setCiteCount(cit);
					result.setSource("必应学术");
					result.setValue(0.0);
					resultList.add(result);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return amount;
	}

	/**
	 * Jsoup获取到的Html文档中某个元素下的文本（包括子元素）
	 * 
	 * @param doc
	 *            Html文件中的某一个元素结点
	 * @return 如果doc为null，将返回空字符串
	 */
	public static String getTextInHtml(Element element) {

		if (null == element)
			return "";

		String ret = null;
		ret = element.hasText() ? element.text() : "";
		ret = Jsoup.clean(ret, Whitelist.simpleText());
		ret = HtmlSpecialCharUtil.unEscapeHtml(ret);

		return ret;
	}

	public static String abstractNumFromString(String s) {
		String regEx = "[^0-9]"; // 使用正则表达式提取字符串中的数字部分
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s);
		s = m.replaceAll("").trim();
		return s;
	}

	public static String deleteNumFromString(String s) {
		s = s.replaceAll("\\d+", "");
		;
		return s;
	}
}
