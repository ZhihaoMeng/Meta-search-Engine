package server.engine.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import server.commonutils.JsoupUtil;
import server.commonutils.MyStringChecker;

public class infactget {
	private String URL_BASE="http://www.letpub.com.cn/index.php?page=journalapp&fieldtag=15&firstletter=&currentpage=15#journallisttable";

	public void getfacts(){
		Document doc = null;
		try {
			doc = JsoupUtil.getHtmlDocument(URL_BASE);
			if (null == doc) return;
			Elements tables = doc.select("table.table_yjfx");
			if (null == tables || tables.isEmpty()) return;
			Elements contentlist = tables.get(1).getElementsByTag("tr");
			if (contentlist != null && contentlist.size() > 0) {
				for (Element relQuery : contentlist) {
					Elements tds = relQuery.getElementsByTag("td");
					if(tds!=null && tds.size()>0){
						
						for (Element elem : tds) {
							System.out.print(elem.text()+"\t");
						}
						System.out.println("");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
