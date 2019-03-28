package common.function.result_merge_methods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import common.entities.searchresult.Result;

/**
 * 通过文档排名来计算单个文档的初始分值
 * @author wenyuanliu
 *目前假设a=0.6 b =-0.9
 */
public class CalculateInitialScore {
	public double calInitScore(double a, double b, int rank)
	{
		double initScore = Math.pow(Math.E, (a + b*Math.log(rank)))/(1 + Math.pow(Math.E, (a + b*Math.log(rank))));
		return initScore;
	}
	
	public void calInitScoreList(double a, double b, List<Result> ls)
	{
		Iterator<Result> iterator = ls.iterator();
		while(iterator.hasNext())
		{
			Result curResult = iterator.next();
			if (curResult != null) {
				double score = calInitScore(a, b, curResult.getPos());
				curResult.setInitScore(score);
				curResult.initScoreList.add(score);
			}
		}
	}
}
