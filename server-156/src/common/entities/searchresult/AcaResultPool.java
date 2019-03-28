package common.entities.searchresult;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AcaResultPool {
private final static int MAX_POOL_SIZE = 1<<10;
	
	private AcaResultPoolItem[] m_arrPool;//缓存池
	private int m_nCurIndex;
	private ReadWriteLock m_objLock;
	
	private AcaResultPool(){
		
		m_arrPool=new AcaResultPoolItem[MAX_POOL_SIZE];
		m_objLock=new ReentrantReadWriteLock();
		m_nCurIndex=0;
	}
	
	private static AcaResultPool instance;
	
	private static AcaResultPool getInstance(){
		if(null==instance){
			synchronized (AcaResultPool.class) {
				if(null==instance) instance=new AcaResultPool();
			}
		}
		return instance;
	}
	
	private final void lockForRead(){
		m_objLock.readLock().lock();
	}
	private final void lockForWrite(){
		m_objLock.writeLock().lock();
	}
	private final void unlockForRead(){
		m_objLock.readLock().unlock();
	}
	private final void unlockForWrite(){
		m_objLock.readLock().lock();
		m_objLock.writeLock().unlock();
		m_objLock.readLock().unlock();
	}
	
	/**
	 * 搜索一个已存在的Item
	 * @param userid 用户ID
	 * @param query 查询词
	 * @return
	 */
	private AcaResultPoolItem searchForHistoryItem(int userid, String query){
		
		AcaResultPoolItem ret=null;
		
		lockForRead();
		for (int i = 0; i < m_arrPool.length; ++i) {
			AcaResultPoolItem tmp = m_arrPool[i];
			if (null == tmp || tmp.isInUsed() || tmp.getUserId() != userid
					|| !tmp.getQuery().equals(query))
				continue;
			ret = tmp;
			ret.use(userid,query);
			break;
		}
		unlockForRead();
		return ret;
		
	}
	
	/**
	 * 参数指定用户ID，与用户查询词，提供一个可用的结果列表项
	 * 
	 * @param userid
	 *            用户的ID
	 * @param query
	 *            用户查询词
	 * @return 返回userid对应的结果列表项
	 */
	public static AcaResultPoolItem getResultListItem(int userid, String query) {

		AcaResultPool ins=AcaResultPool.getInstance();
		AcaResultPoolItem ret= ins.searchForHistoryItem(userid, query);
		if(null!=ret) return ret;

		//池中没有该用户搜索当前查询词的历史记录（或者因为池的竞争被替换掉了）
		//为其新生成一个Item对象并返回
		ret=new AcaResultPoolItem();
		ret.use(userid, query);
		ins.addItem(ret);
		return ret;
	}

	private void addItem(AcaResultPoolItem item){
		
		if(null==item) return;
		
		lockForWrite();
		m_arrPool[m_nCurIndex]=item;
		m_nCurIndex=(1+m_nCurIndex)&(MAX_POOL_SIZE-1);
		unlockForWrite();
		
	}
	
	/**
	 * 释放一个缓存项，参数中的用户ID及查询词用来验证；
	 * 当item中的用户ID及查询词与参数不匹配时（可能性不大），将不会释放
	 * @param item 要释放的项
	 * @param userid 用户ID
	 * @param query 查询词
	 */
	public static void releaseItem(AcaResultPoolItem item, int userid, String query){
		
		if(null==item) return;
		item.unUse(userid, query);
	}
}
