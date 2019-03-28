package common.functions.resultanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import server.engine.api.EngineFactory;
import server.engine.api.EngineFactory.AcaSourceName;
import server.engine.api.EngineFactory.EngineName;
import common.entities.searchresult.AcademicResult;
import common.entities.searchresult.Result;

public class ResultProportionAnalysis {

	private Set<EngineName> m_setAllEng;
	private Set<AcaSourceName> m_setAllAcaEng;

	private ResultProportionAnalysis() {
		m_setAllEng = new HashSet<EngineFactory.EngineName>();
		m_setAllAcaEng = new HashSet<EngineFactory.AcaSourceName>();
		EngineFactory.getAllEngineNames(m_setAllEng);
		EngineFactory.getAllAcaEngineNames(m_setAllAcaEng);
	}

	private static ResultProportionAnalysis ins;

	private static ResultProportionAnalysis getInstance() {
		if (null == ins) {
			synchronized (ResultProportionAnalysis.class) {
				if (null == ins)
					ins = new ResultProportionAnalysis();
			}
		}
		return ins;
	}

	private void initResultMap(Map<EngineName, Double> data) {
		if (null == data)
			return;
		data.clear();
		for (Iterator<EngineName> it = m_setAllEng.iterator(); it.hasNext();) {
			data.put(it.next(), 0.0);
		}
	}

	private void getResultDistributionRateIns(Map<EngineName, Double> ret,
			List<Result> ls) {

		if (null == ret || null == ls || ls.isEmpty())
			return;

		int sum = 0;

		initResultMap(ret);
		for (Iterator<Result> it = ls.iterator(); it.hasNext();) {
			Result res = it.next();
			if (null == res)
				continue;
			Iterator<EngineName> iterSrc = res.getSourceEngineIterator();
			while (iterSrc.hasNext()) {
				EngineName srcEng = iterSrc.next();
				ret.put(srcEng, 1.0 + ret.get(srcEng));
				++sum;
			}
		}

		Iterator<Entry<EngineName, Double>> itMap = ret.entrySet().iterator();
		while (itMap.hasNext()) {
			Entry<EngineName, Double> ent = itMap.next();
			ent.setValue(ent.getValue() / sum);
		}

	}

	/**
	 * 获取搜索结果分布率
	 * 
	 * @param ret
	 *            返回结果，内容为：搜索引擎名-百分比值，搜索引擎名称为页面显示的内容
	 * @param ls
	 *            搜索结果列表
	 */
	public static void getResultDistributionRate(Map<EngineName, Double> ret,
			List<Result> ls) {

		if (null == ret)
			return;
		getInstance().getResultDistributionRateIns(ret, ls);
	}

	private void initAcaResultMap(Map<AcaSourceName, Double> data) {
		if (null == data)
			return;
		data.clear();
		for (Iterator<AcaSourceName> it = m_setAllAcaEng.iterator(); it
				.hasNext();) {
			data.put(it.next(), 0.0);
		}
	}

	private void getAcaResultDistributionRateIns(
			Map<AcaSourceName, Double> ret, List<AcademicResult> ls) {

		if (null == ret || null == ls || ls.isEmpty())
			return;

		int sum = 0;

		initAcaResultMap(ret);
		for (Iterator<AcademicResult> it = ls.iterator(); it.hasNext();) {
			AcademicResult res = it.next();
			if (null == res)
				continue;
			Iterator<AcaSourceName> iterSrc = res.getSourceEngineIterator();
			while (iterSrc.hasNext()) {
				AcaSourceName srcEng = iterSrc.next();
				ret.put(srcEng, 1.0 + ret.get(srcEng));
				++sum;
			}
		}

		Iterator<Entry<AcaSourceName, Double>> itMap = ret.entrySet()
				.iterator();
		while (itMap.hasNext()) {
			Entry<AcaSourceName, Double> ent = itMap.next();
			ent.setValue(ent.getValue() / sum);
			// System.out.println("prportion analysis ent value:"+ent.getValue());
		}

	}

	public static void getAcaResultDistributionRate(
			Map<AcaSourceName, Double> data, List<AcademicResult> result) {
		if (null == data)
			return;
		getInstance().getAcaResultDistributionRateIns(data, result);
	}
}
