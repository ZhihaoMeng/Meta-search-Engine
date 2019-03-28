package server.threads;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import server.commonutils.MyStringChecker;
import server.engine.api.AcaRelateSearchWordFromBaiduAca;
import common.entities.searchresult.RelateSearchWord;

public class AcaRelateSearchThread extends Thread{
	private final CountDownLatch mDoneSignal;	//上层函数（SearchAction）设置的线程信号量，在本线程工作完成之后，使其减1即可
	private String query;	//查询词
	private List<RelateSearchWord> relateResult;
	public AcaRelateSearchThread(List<RelateSearchWord> resultList,
			CountDownLatch doneSignal, String query) {

		this.query = query;
		this.mDoneSignal = doneSignal;
		this.relateResult=resultList;
	}

	
	public List<RelateSearchWord> getRelateResult() {
		//如果上层Action设置了这个值，这个列表不仅被设置到缓存对象中，还可以做为向Action的返回值
		//如果Action没有设置，表示Action要进行相关搜索，但是暂时不需要返回结果值，这里仍然新建一个列表，得到结果并放到缓存中
		if(null==relateResult) relateResult=new LinkedList<RelateSearchWord>();
		return relateResult;
	}


	@Override
	public void run() {
		this.doWork();
		mDoneSignal.countDown();
	}

	private void doWork() {

		try {
			
			if (MyStringChecker.isBlank(query)) return;
			AcaRelateSearchWordFromBaiduAca b = new AcaRelateSearchWordFromBaiduAca();
			List<RelateSearchWord> rList=getRelateResult();
			b.getRelatedSearch(rList, query);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}
}
