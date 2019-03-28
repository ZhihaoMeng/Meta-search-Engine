package common.entities.searchresult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import server.commonutils.MyStringChecker;
import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.AcaSourceName;
import server.engine.api.EngineFactory.EngineName;

public class AcademicResult implements Serializable, Comparable<AcademicResult>{
	private String title;
	private String abstr;
	private String year;
	private int citeCount;
	private String source;
	private String authors;//以空格隔开
	private String area;
	private String link;
	private Set<String> m_authors;
	private Set<String> m_source;
	private Double value;
	
	public AcademicResult() {
		super();
		
	}
	public AcademicResult(String title, String abstr, String year,
			String source, String authors, String area,String link) {
		super();
		this.title = title;
		this.abstr = abstr;
		this.year = year;
		this.source = source;
		this.authors = authors;
		this.area = area;
		this.link = link;
	}
	public AcademicResult(String title, String abstr, String year,
			String source, String authors, String area, Double value) {
		super();
		this.title = title;
		this.abstr = abstr;
		this.year = year;
		this.source = source;
		this.authors = authors;
		this.area = area;
		this.value = value;
	}

	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
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
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
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
		setAuthorsName();
	}
	private void setAuthorsName() {
		if (MyStringChecker.isBlank(authors))
			return;
		m_authors = new HashSet<String>();
		String []tempauthor = authors.split(" ");//anthors之間使用空格隔開
		for (int i = 0; i < tempauthor.length; i++) {
			m_authors.add(tempauthor[i]);
		}
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
		setSourceEngineName();
	}
	private void setSourceEngineName() {
		if (MyStringChecker.isBlank(source))
			return;

		m_source = new HashSet<String>();
		Set<String> allEngName = EngineFactory.getAllAcaSourceCnName();
		Iterator<String> iterAllSourceName = allEngName.iterator();
		String source = this.getSource();
		while (iterAllSourceName.hasNext()) {
			String curSname = iterAllSourceName.next();
			if (source.contains(curSname)) {
				m_source.add(curSname);
			}
		}
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	/**
	 * 检查当前这个结果是不是可用的（标题与URL是必须的，如果没有就认为是错误的结果）
	 * 
	 * @return
	 */
	public boolean isUsable() {

		return !MyStringChecker.isBlank(title) && !MyStringChecker.isBlank(abstr);
	}
	public boolean isFromTargetSource(AcaSourceName enuName){
		return isFromTargetSource(enuName.toString());
	}
	public boolean isFromTargetSource(String engName) {

		if (MyStringChecker.isBlank(engName))
			return false;
		return isFromTargetSource(m_source, engName);

	}

	public boolean isFromTargetSource(Set<String> sourceNames) {
		boolean ret = false;
		if (null == sourceNames || sourceNames.isEmpty())
			return ret;
		Iterator<String> it = sourceNames.iterator();
		while (it.hasNext()) {
			String tarSource = it.next();
			if (MyStringChecker.isBlank(tarSource))
				continue;
			if ((isFromTargetSource(sourceNames, tarSource))) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	private boolean isFromTargetSource(Set<String> sourceNames, String tarSource) {
		return sourceNames.contains(tarSource);
	}
	@Override
	public String toString() {
		return "AcademicResult [title=" + title + ", abstr=" + abstr
				+ ", year=" + year + ", citeCount=" + citeCount + ", source="
				+ source + ", authors=" + authors + ", area=" + area
				+ ", value=" + value + ", link=" + link+"]";
	}
	@Override
	public int compareTo(AcademicResult o) {
		return this.value.compareTo(o.getValue());
	}

	public Iterator<AcaSourceName> getSourceEngineIterator(){
		Set<AcaSourceName> srcSet=new HashSet<AcaSourceName>();
		for(Iterator<String> it=m_source.iterator();it.hasNext();){
			String eng=it.next();
//			System.out.println("acaresult eng:"+eng);
//			System.out.println("acaresult eng AcaEngineName:"+EngineFactory.getInnerAcaEngineName(eng));
			srcSet.add(EngineFactory.getInnerAcaEngineName(eng));
		}
		return srcSet.iterator();
	}
}
