package server.info.entites.transactionlevel;

import java.util.Date;

public class AcaLogRecordEntity {
	private int id;
	private String uid;
	private String query;
	private String title;
	private String abstr;
	private String link;
	private int year;
	private int citeCount;
	private String authors;
	private String sources;
	private double score;
	
	public AcaLogRecordEntity(){
		this.citeCount=0;
		this.year=0;
		this.score = 0;
	}

	public AcaLogRecordEntity(String uid, String query, String title,
			String abstr, String link, int year, int citeCount, String authors,
			String sources) {
		super();
		this.uid = uid;
		this.query = query;
		this.title = title;
		this.abstr = abstr;
		this.link = link;
		this.year = year;
		this.citeCount = citeCount;
		this.authors = authors;
		this.sources = sources;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getCiteCount() {
		return citeCount;
	}

	public void setCiteCount(int citeCount) {
		this.citeCount = citeCount;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "AcaLogRecordEntity [id=" + id + ", uid=" + uid + ", query=" + query + ", title=" + title
				+ ", abstr=" + abstr + ", link=" + link + ", year=" + year
				+ ", citeCount=" + citeCount + ", authors=" + authors
				+ ", sources=" + sources + ", score=" + score + "]";
	}
	
}
