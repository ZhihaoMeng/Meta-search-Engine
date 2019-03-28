package common.entities.searchresult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AcaResultPoolItem {
	private List<AcademicResult> m_lsSearchResultList;
	private List<RelateSearchWord> m_lsRelateSearchWords;
	private int m_nUserId;
	private int m_nCurPage;
	private String m_strQuery;
	private boolean m_bUsedFlag;
	private ReadWriteLock m_objLock;
	private String rankStand;
	
	public AcaResultPoolItem() {
		m_nUserId = -1;
		m_bUsedFlag = false;
		m_nCurPage=1;
		m_objLock=new ReentrantReadWriteLock();
	}
	/**
	 * 将一个结果列表项清空为初始状态
	 */
	public void resetResultListItem() {
		m_lsSearchResultList.clear();
		m_nUserId = -1;
		m_nCurPage=1;
		m_strQuery=null;
		unUse(m_nUserId, m_strQuery);
	}
	public String getM_strQuery() {
		return m_strQuery;
	}
	public String getQuery(){
		
		return m_strQuery;
	}
	
	public void setQuery(String query){
		m_strQuery=query;
	}
	public boolean isInUsed(){
		
		boolean ret=true;
		m_objLock.readLock().lock();
		ret= m_bUsedFlag;
		m_objLock.readLock().unlock();
		return ret;
	}
	public void use(int userid, String query){
		
		m_objLock.writeLock().lock();
		m_bUsedFlag=true;
		m_nUserId=userid;
		m_strQuery=query;
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();

	}
	public void unUse(int userid, String query){
		m_objLock.writeLock().lock();
		if(m_nUserId==userid&&!(null==query&&null!=m_strQuery)&&query.equals(m_strQuery)){
			m_bUsedFlag=false;
		}
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();
	}
	public List<AcademicResult> getSearchResultList() {
		if (null == m_lsSearchResultList) {
			m_lsSearchResultList = new ArrayList<AcademicResult>();
		}
		return m_lsSearchResultList;
	}

	public void setSearchResultList(List<AcademicResult> searchResultList) {
		m_lsSearchResultList = searchResultList;
	}

	public List<RelateSearchWord> getRelateSearchWords() {
		if(null==m_lsRelateSearchWords){
			m_lsRelateSearchWords = new ArrayList<RelateSearchWord>();
		}
		return m_lsRelateSearchWords;
	}
	public void setRelateSearchWords(
			List<RelateSearchWord> m_lsRelateSearchWords) {
		this.m_lsRelateSearchWords = m_lsRelateSearchWords;
	}
	public int getUserId() {
		return m_nUserId;
	}

	public void setUserId(int userId) {
		m_nUserId = userId;
	}

	public int getCurPage(){
		return m_nCurPage;
	}
	
	public void pageIncrease(){
		++m_nCurPage;
	}
	public int resultAmount(){
		return m_lsSearchResultList.size();
	}
	public boolean hasSearchResult(){
		return null!=m_lsSearchResultList&&!m_lsSearchResultList.isEmpty();
	}
	public String getRankStand() {
		return rankStand;
	}
	public void setRankStand(String rankStand) {
		this.rankStand = rankStand;
	}
	
}
