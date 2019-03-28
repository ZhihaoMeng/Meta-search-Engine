package common.function.result_merge_methods;

import java.util.List;

import common.entities.searchresult.Result;

public class CalculateOverlap {
	private double overlap_rate;
	/**
	 * 计算文档的重叠率，公式如下：((D1+D2+D3...) - Dall)/((n-1)*Dall)
	 * @param Dall n个成员搜索引擎返回的文档总数去重后的文档总数)
	 * @param DiList 每个成员搜索引擎的文档数 (相当于每个成员搜索引擎的和)
	 * @return 返回计算出来的文档重叠率
	 */
	public double calOverlapRate(int Dall, List<Integer> DiList)
	{
		int n = DiList.size();
		int m = 0;
		for (int i = 0; i < n; i++) {
			m += DiList.get(i);
		}
		
		overlap_rate = (m - Dall)/((n-1)*Dall);
		return overlap_rate;		
	}
}
