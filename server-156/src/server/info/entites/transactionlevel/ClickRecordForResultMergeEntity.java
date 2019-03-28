package server.info.entites.transactionlevel;

public class ClickRecordForResultMergeEntity {

	private int userid;
	private Integer baiduCount;
	private Integer sogouCount;
	private Integer youdaoCount;
	private Integer bingCount;
	private Integer yahooCount;
	private String query;
	private String url;
	private String abstr;
	private Integer resultLocation;


	public ClickRecordForResultMergeEntity(){
		this.userid = -1;
		this.baiduCount = -1;
		this.sogouCount = -1;
		this.youdaoCount = -1;
		this.bingCount = -1;
		this.yahooCount = -1;
		this.resultLocation = -1;
	}
	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public Integer getBaiduCount() {
		return this.baiduCount;
	}

	public void setBaiduCount(Integer baiduCount) {
		this.baiduCount = baiduCount;
	}

	public Integer getSogouCount() {
		return this.sogouCount;
	}

	public void setSogouCount(Integer sogouCount) {
		this.sogouCount = sogouCount;
	}

	public Integer getYoudaoCount() {
		return this.youdaoCount;
	}

	public void setYoudaoCount(Integer youdaoCount) {
		this.youdaoCount = youdaoCount;
	}

	public Integer getBingCount() {
		return this.bingCount;
	}

	public void setBingCount(Integer bingCount) {
		this.bingCount = bingCount;
	}

	public Integer getYahooCount() {
		return this.yahooCount;
	}

	public void setYahooCount(Integer yahooCount) {
		this.yahooCount = yahooCount;
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAbstr() {
		return this.abstr;
	}

	public void setAbstr(String abstr) {
		this.abstr = abstr;
	}

	public Integer getResultLocation() {
		return this.resultLocation;
	}

	public void setResultLocation(Integer resultLocation) {
		this.resultLocation = resultLocation;
	}
}
