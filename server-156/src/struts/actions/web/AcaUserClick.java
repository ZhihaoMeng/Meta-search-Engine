package struts.actions.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import server.commonutils.MyStringChecker;
import server.info.config.SessionAttrNames;
import server.info.entites.transactionlevel.AcaLogRecordEntity;

import com.opensymphony.xwork2.ActionSupport;

import db.dao.UserDao;
import db.dbhelpler.AcaLogHelper;
import db.entityswithers.AcaLogSwitcher;

public class AcaUserClick extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserDao userDao;
	private String query;
	private String title;
	private String authors;
	private String abstr;
	private String link;
	private String year;
	private int m_year;
	private double m_score;
	private String score;
	private String sources;
	private int citeCount;
	private String likeOrDislike;// -1:不喜欢；0：普通的点击记录；1：喜欢

	/**
	 * 用户点击后执行，记录点击信息
	 * 
	 * @throws IOException
	 */
	public String execute() throws Exception {

		// 获取用户名

		HttpSession session = ServletActionContext.getRequest().getSession();
		String usernameinpage = (String) session
				.getAttribute(SessionAttrNames.USERNAME_ATTR);
		// 获取用户ID——即使未登录用户也获取了1
		int userid = 1;
		if (usernameinpage != null) {
			if (userDao.findUserByUsername(usernameinpage).size() > 0) {
				userid = userDao.findUserByUsername(usernameinpage).get(0)
						.getUid();
			}
		}
		
		AcaLogRecordEntity ret = new AcaLogRecordEntity(userid+"", query, title, abstr, link, m_year, citeCount, authors, sources);
		ret.setScore(m_score);
		AcaLogHelper.putAcaResult(ret);
		System.out.println("您点击的条目是：" + likeOrDislike);
		System.out.print("query:" + query + " title:" + title + " abstr:"
				+ abstr + " source:" + sources + " link:" + link);
		String responseText = "{'flag':'true','url':'" + link + "'}";
		sendResponse(responseText);

		return null;
	}

	/**
	 * 发送响应
	 * 
	 * @param respText
	 *            要响应的字符串
	 */
	private void sendResponse(String respText) {
		HttpSession session = ServletActionContext.getRequest().getSession();
		HttpServletResponse res = ServletActionContext.getResponse();
		res.reset();
		res.setContentType("text/html;charset=utf-8");
		PrintWriter pw = null;
		try {
			pw = res.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.print(respText);

		pw.flush();
		pw.close();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstr() {
		return abstr;
	}

	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
		if(MyStringChecker.isWhitespace(year)){
			m_year = 0;
		}else{
			m_year = Integer.valueOf(year);
		}
	}

	public String getLikeOrDislike() {
		return likeOrDislike;
	}

	public void setLikeOrDislike(String likeOrDislike) {
		this.likeOrDislike = likeOrDislike;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
		if(MyStringChecker.isWhitespace(score)){
			m_score = 0;
		}else{
			m_score = Double.valueOf(score);
		}
		System.out.println("score is:"+score);
		System.out.println("m_score is:"+m_score);
	}

	public int getCiteCount() {
		return citeCount;
	}

	public void setCiteCount(int citeCount) {
		this.citeCount = citeCount;
	}

}
