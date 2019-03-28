package common.functions.resultmerge;

import java.util.Comparator;

import common.entities.searchresult.AcademicResult;

public class MergeAcaSort implements Comparator<AcademicResult>{

	@Override
	public int compare(AcademicResult o1, AcademicResult o2) {
		double v1 = o1.getValue(),v2=o2.getValue();
		if(v1<v2){
			return 1;
		}else if(v1>v2){
			return -1;
		}else{
			return 0;
		}
	}

}
