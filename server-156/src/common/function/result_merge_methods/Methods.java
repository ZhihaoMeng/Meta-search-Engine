package common.function.result_merge_methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.math.*;

import org.ejml.alg.dense.decomposition.eig.EigenPowerMethod;
import org.ejml.data.DenseMatrix64F;

import common.entities.searchresult.Letor;
import common.functions.io.CommonFunction;
import common.functions.io.ReadFromFile;

public class Methods {
	int maxcrnumber;
	int maxfilenumber;
	int countnumber;
	int searchEngineNum=21;
	//D:\\文件娱乐\\文件和照片\\2017spring\\元搜索\\合成算法代码\\MQ2008-agg\\Fold1\\bordacount.txt
	String fileborda="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold5\\bordacount.txt";
	String fileRoundRobin="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold5\\RoundRobin.txt";
	String filecombMNZ="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold5\\COMBmnz.txt";
	String fileSDM="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold5\\sdm.txt";
	String filenewMethod="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\newmethod.txt";
	String fileMC1="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold5\\mc1.txt";
	String filecr="D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\cr.txt";
	String fileLambdaMerge = "/Users/wenyangzheng/Downloads/Test/LambdaMerge.rtf";
	
	public void BordaCount(String[][] docQid, int[][] partkey,
			double[] partcolumn, int[] preference, double partcr) {
		// TODO Auto-generated method stub
		double scorearraay[] ;
		scorearraay=new double[partkey.length];
		ArrayList<Letor> letorlist=new ArrayList<Letor>();
		for(int i=0;i<partkey.length;i++){
			double score=0;	
			for(int m=0;m<21;m++){
				if(partkey[i][m]!=0){
					score+=partkey.length-Integer.parseInt(docQid[i][m+1])+1;
					//System.out.println("if score:"+score+" partkey[i][m]"+partkey[i][m]);
				}else{
					score=score+(partcolumn[m]/2);
					//System.out.println("else score:"+score+" partkey[i][m]"+partkey[i][m]);
				}
			}
			scorearraay[i]=score;
		//	System.out.println("end coloumn for score:"+score+" qid:"+docQid[i][0]+"doc:"+docQid[i][26]);
			Letor letor=new Letor();
			
			letor.setTotalscore(score);
			letor.setMethod("BodaCount");
			letor.setQid(docQid[i][0]);
			letor.setDoc(docQid[i][22]);//26
			letor.setPreference(preference[i]);
			letor.setCr(partcr);
			letorlist.add(letor);
		}
		Print(scorearraay,letorlist);
	}
	/**
	 * LambdaMerge结果合成算法
	 */
	public void LambdaMerge(String[][] docQid, int[][] partkey, int[] preference, double partcr) {
		//softmax ak
		double [][] searchResult = new double[21][searchEngineNum];
		for(int j=0; j<searchEngineNum; ++j) {
			double sum = 0.0;
			for(int i=0; i<21; ++i) {
				//判断d是否在D中
				if(partkey[i][j]==0) {
					int temporary = 99999;
					for(int q = 0; q<21; ++q) {
						if(partkey[q][j]<temporary&&partkey[q][j]!=0)
							temporary = partkey[q][j];
					}
					partkey[i][j]=temporary;
				}
				sum += Math.exp(Math.PI*partkey[i][j]);
			}
	
			for(int i=0;i<21;++i) {
				searchResult[i][j]=(Math.exp(Math.PI*partkey[i][j]))/sum;
			}
		}
		
	}
	/**
	* SDM结果合成方法
	*/
	public void SDM(String[][] docQid, int[][] partkey, int[] preference, double partcr){
		//k为权重
		double k = 0.5;
		int len = partkey.length;
		double scorearray[] = new double[len];
		ArrayList<Letor> letorlist=new ArrayList<Letor>();
		for (int i = 0; i < len; i++) {
			double score = 0.0;
			//m记录所有检索到该结果的引擎数量
			int m = 0;
			for (int j = 0; j < searchEngineNum; j++) {
				if (partkey[i][j] != 0) {
					m++;
				}
				score += partkey[i][j];
			}
			if(m!=0){
			score += k * (searchEngineNum - m) * score / m;}
			else{
				score=score+0;
			}
			scorearray[i] = score;
			Letor letor = new Letor();
			letor.setTotalscore(score);
			letor.setMethod("SDM");
			letor.setQid(docQid[i][0]);
			letor.setDoc(docQid[i][22]);//26
			letor.setPreference(preference[i]);
			letor.setCr(partcr);
			letorlist.add(letor);
		}
		Print(scorearray,letorlist);
	}
	private void Print(double[] scorearraay, ArrayList<Letor> letorlist) {
		// TODO Auto-generated method stub
		//Letor sort=new Letor();
		Collections.sort(letorlist, new Comparator<Letor>() {
            public int compare(Letor o1, Letor o2) {
                return new Double(o2.getTotalscore()).compareTo(new Double(o1.getTotalscore()));
            }
        });
		int totalpreference=0;
		for(int j=0;j<scorearraay.length;j++){
			totalpreference+=letorlist.get(j).getPreference();
		}
		if(totalpreference!=0){
		CommonFunction f=new CommonFunction();
		for(int i=0;i<scorearraay.length;i++){
			String a=letorlist.get(i).getPreference()+" "+letorlist.get(i).getQid()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getCr()+" "+letorlist.get(i).getMethod();	
			//String a=letorlist.get(i).getPreference()+" "+letorlist.get(i).getQid()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getTotalscore();
			/*if(letorlist.get(i).getMethod().equals("combMNZ")){
				f.writeFile(filecombMNZ, a);
				System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
				}
			else if(letorlist.get(i).getMethod().equals("SDM")){
				f.writeFile(fileSDM, a);
				System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
				}
			else if(letorlist.get(i).getMethod().equals("BodaCount")){
				f.writeFile(fileborda, a);
				System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
				}
			else if(letorlist.get(i).getMethod().equals("RoundRobin")){
				f.writeFile(fileRoundRobin, a);
				System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
				}*/
			f.writeFile(filenewMethod, a);
			System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
			}
		}
		
	}
	public void read(){
		//D:\\文件娱乐\\文件和照片\\2017spring\\元搜索\\合成算法代码\\MQ2008-agg\\Fold4\\train.txt
		LinkedList<LinkedList<String>> result =ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\train.txt" );
		//LinkedList<LinkedList<String>> result =  ReadFromFile.getInfo("E:\\元搜索\\MQ2008-agg\\Fold1\\train.txt");
		maxfilenumber=result.size();	
		LinkedList<LinkedList<String>> result1 =  ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\cr-1.txt");
		//LinkedList<LinkedList<String>> result1 =  ReadFromFile.getInfo("E:\\元搜索\\MQ2008-agg\\Fold1\\CR1.txt");
		maxcrnumber=result1.size();
		LinkedList<LinkedList<String>> r2=ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\countOfnumber.txt");
		//LinkedList<LinkedList<String>> r2=ReadFromFile.getInfo("E:\\元搜索\\MQ2008-agg\\Fold1\\countOfnumber.txt");
		countnumber=r2.size();
		LinkedList<LinkedList<String>> r3=ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017summer\\合成排序\\MQ2007-agg\\Fold1\\changed.txt");
		//LinkedList<LinkedList<String>> r3=ReadFromFile.getInfo("E:\\元搜索\\MQ2008-agg\\Fold1\\changed.txt");
		int[] count;
		double[] cr;
		count =new int[maxcrnumber];
		cr=new double[maxcrnumber];
		String alllist[][];
		alllist =new String[maxfilenumber][32];
		String listvalue[][];
		listvalue=new String[maxfilenumber][21];//25
		int[][] column;
		column=new int[countnumber][21];
		for( int i=0;i<result.size();i++){
			for(int k=0;k<=20;k++){//24
	  			listvalue[i][k]=result.get(i).get(k+2);
	  		 }
			for(int m=0;m<32;m++){//36
					alllist[i][m]= result.get(i).get(m);
				 } 
			}
		//处理存有结果重合率的文件
		for( int i=0;i<result1.size();i++){
				count[i]=Integer.parseInt(result1.get(i).get(2));
				cr[i]=Double.parseDouble(result1.get(i).get(1));
		}
		//处理存储计数个数的文件
		for(int i=0;i<r2.size();i++){
			for(int m=0;m<21;m++){//25
				column[i][m]=Integer.parseInt(r2.get(i).get(m+1));
			}
		}
		//处理changed文件
		String changed[][];
		changed=new String[maxfilenumber][23];//27
		for(int i=0;i<r3.size();i++){
			for(int m=0;m<23;m++){
				changed[i][m]=r3.get(i).get(m);
			}
		}
		String[][] t;
		int[][] keyt;
		t =new String[maxfilenumber][21];//25
		keyt=new int[maxfilenumber][21];
		for(int i=0;i<maxfilenumber;i++){
			for(int j=0;j<9;j++){
				String temp=listvalue[i][j].toString();
				temp=temp.substring(2,temp.length());//将1:NULL 2:NULL 3:33 4:NULL 5:NULL 6:148这样的形式规范化，只存内容
				t[i][j]=temp;
				if(t[i][j].equals("NULL")){t[i][j]="0";}
				//System.out.println(t[i][j]+"IJ:"+i+" "+j);
				keyt[i][j]=Integer.parseInt(t[i][j]);
			}
			for(int j=9;j<21;j++){//25
				String temp=listvalue[i][j].toString();
				temp=temp.substring(3,temp.length());//将10:NULL 12:NULL 13:33 14:NULL 15:NULL 16:148这样的形式规范化，只存内容
				t[i][j]=temp;
				if(t[i][j].equals("NULL")){t[i][j]="0";}
				keyt[i][j]=Integer.parseInt(t[i][j]);
			}
		}
		RunToBorda(changed,keyt,count,column,alllist,cr);
	}
	private void RunToBorda(String[][] changed, int[][] keyt, int[] count,
			int[][] column,String[][] alllist,double[] cr) {
		// TODO Auto-generated method stub
		int initial=0;
		String[][] DocQid;
		int[][] partkey;
		int[] preference;
		double[] partcolumn;
		double partcr;
		int[] mnumber;
		partcolumn=new double[21];//25
		for(int i=0;i<maxcrnumber;i++){
			int sum=initial+count[i];
			partcr=cr[i];
			preference=new int[count[i]];
			mnumber =new int[count[i]];
			DocQid=new String[count[i]][23];//27
			partkey=new int[count[i]][21];
			for(int j=initial;j<sum;j++){	
				DocQid[j-initial][22]=alllist[j][25];//changed[26]
				DocQid[j-initial][0]=changed[j][0];
				preference[j-initial]=Integer.parseInt(alllist[j][0]);
				int a=0;
				for(int m=0;m<21;m++){//25
					partkey[j-initial][m]=keyt[j][m];
					partcolumn[m]=column[i][m];
					//System.out.println("column[i][m]:"+column[i][m]);
					DocQid[j-initial][m+1]=changed[j][m+1];
					if(keyt[j][m]!=0){a++;}
				}
				mnumber[j-initial]=a;
			}
		//	BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		//	RoundRobin(DocQid,partkey,preference,partcr);
		//	combMNZ(DocQid,partkey,preference,mnumber,partcr);
		//	SDM(DocQid,partkey,preference,partcr);
		//	MarkovChain(DocQid,preference,partcr);
			NewMethod(DocQid,partkey,partcolumn,preference,mnumber,partcr);
			initial=sum;
		}
	}
	private void NewMethod(String[][] DocQid, int[][] partkey,
			double[] partcolumn, int[] preference, int[] mnumber,
			double partcr) {
		// TODO Auto-generated method stub
		if(partcr<=0.08){
			RoundRobin(DocQid,partkey,preference,partcr);
			//combMNZ(DocQid,partkey,preference,mnumber,partcr);	
		}else if(partcr>0.08&&partcr<0.14){
			//MarkovChain(DocQid,preference,partcr);
			combMNZ(DocQid,partkey,preference,mnumber,partcr);
			//RoundRobin(DocQid,partkey,preference,partcr);
		}else if(partcr>=0.14&&partcr<0.16){
			BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		//	SDM(DocQid,partkey,preference,partcr);
		}else if(partcr>=0.16&&partcr<0.23){
			MarkovChain(DocQid,preference,partcr);
		}else if(partcr>=0.23&&partcr<0.25){
			BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		}else if(partcr>=0.25&&partcr<0.298){
			combMNZ(DocQid,partkey,preference,mnumber,partcr);
		}else if(partcr>=0.298&&partcr<0.45){
			BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		}else if(partcr>=0.45&&partcr<0.83){
			MarkovChain(DocQid,preference,partcr);
		}else if(partcr>=0.83&&partcr<0.87){
			combMNZ(DocQid,partkey,preference,mnumber,partcr);
		}else if(partcr>=0.87&&partcr<0.895){
			RoundRobin(DocQid,partkey,preference,partcr);
		}else if(partcr>=0.895){
			MarkovChain(DocQid,preference,partcr);
		}	
	/*	if(partcr<=0.08){
			RoundRobin(DocQid,partkey,preference,partcr);
			//combMNZ(DocQid,partkey,preference,mnumber,partcr);	
		}else if(partcr>0.08&&partcr<0.16){
			MarkovChain(DocQid,preference,partcr);
			//RoundRobin(DocQid,partkey,preference,partcr);
		}else if(partcr>=0.16&&partcr<0.45){
			BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		//	SDM(DocQid,partkey,preference,partcr);
		}else if(partcr>=0.45){
			MarkovChain(DocQid,preference,partcr);
		}	*/
	/*	if((partcr-0.25)<=0.08){
			RoundRobin(DocQid,partkey,preference,partcr);
			//combMNZ(DocQid,partkey,preference,mnumber,partcr);	
		}else if((partcr-0.25)>0.08&&(partcr-0.25)<0.16){
			MarkovChain(DocQid,preference,partcr);
			//RoundRobin(DocQid,partkey,preference,partcr);
		}else if((partcr-0.25)>=0.16&&(partcr-0.25)<0.45){
			BordaCount(DocQid,partkey,partcolumn,preference,partcr);
		//	SDM(DocQid,partkey,preference,partcr);
		}else if((partcr-0.25)>=0.45){
			MarkovChain(DocQid,preference,partcr);
		}*/
	}
	public void MarkovChain(String[][] docQid, int[] preference,double partcr){
		int len = docQid.length;
		double scorearray[] = new double[len];
		double[][] seed = new double[len][len];

		List<Map<String,Double>> mapList = new LinkedList<Map<String, Double>>();
		//记录结果docid顺序，后面的transferMatrix也按照这个顺序进行构建
		List<String> docList = new LinkedList<String>();
		for (int i = 0; i < len; i++) {
			docList.add(docQid[i][22]);//26
		}
		//step1. 计算每个结果，所有结果列表中，比它排名靠前的结果的id，构成一个数组
		List<String> rankList = new LinkedList<String>();
		int index = 0;
		for (String string : docList) {
			
			for (int i = 1; i <= searchEngineNum; i++) {
				int rank = Integer.parseInt(docQid[index][i]);
				if (rank != len){
					rankList.add(docQid[index][22]);//26
				}
				for (int j = 0; j < len; j++) {
					if(Integer.parseInt(docQid[j][i]) < rank) 
						rankList.add(docQid[j][22]);//26
				}
			}
			//step2. 计算状态转移概率，构造转移矩阵transferMatrix
			Map<String,Double> transferMap = new HashMap<String,Double>();
			for (String string2 : rankList) {
				double temp = 0.0;
				if (transferMap.containsKey(string2))
					temp = transferMap.get(string2);
				transferMap.put(string2, temp+1);
			}
			for (Map.Entry<String, Double> entry : transferMap.entrySet()) {
				double value = entry.getValue();
				entry.setValue(value/rankList.size());
			}
			mapList.add(transferMap);
			rankList.clear();
			index++;
		}
		
		
		
		//step3. 求解平稳分布状态，即计算transferMatrix特征根为1的特征向量，得到最终排序结果
	
		//遍历mapList，构建transferMap
		int flag = 0;
		for (Map<String, Double> map : mapList) {
			for(Map.Entry<String, Double> entry : map.entrySet()){
				int y = docList.indexOf(entry.getKey());
				//利用二维数组构造后面的转移矩阵，这里已经进行了转置，互换了横纵位置信息
				seed[y][flag] = entry.getValue();
			}
			flag++;
		}
		
		//求解transferMatrix的左特征向量,利用幂法求解近似的特征向量
		DenseMatrix64F dm = new DenseMatrix64F(seed);
		EigenPowerMethod epm = new EigenPowerMethod(len);
		System.out.println(epm.computeDirect(dm));
		DenseMatrix64F rank =  epm.getEigenVector();
		ArrayList<Letor> letorlist=new ArrayList<Letor>();

		for (int i = 0; i < len; i++) {
			Letor letor = new Letor();
			letor.setTotalscore(rank.get(i, 0));
			System.out.println("score: "+rank.get(i, 0));
			letor.setMethod("MC1");
			letor.setQid(docQid[i][0]);
			letor.setDoc(docQid[i][22]);//26
			letor.setPreference(preference[i]);
			letor.setCr(partcr);
			letorlist.add(letor);
		} 		
		PrintMC(letorlist);
	} 		
	private void PrintMC(ArrayList<Letor> letorlist) {
		// TODO Auto-generated method stub
		Collections.sort(letorlist, new Comparator<Letor>() {
            public int compare(Letor o1, Letor o2) {
                return new Double(o2.getTotalscore()).compareTo(new Double(o1.getTotalscore()));
            }
        });
		int totalpreference=0;
		for(int j=0;j<letorlist.size();j++){
			totalpreference+=letorlist.get(j).getPreference();
		}
		if(totalpreference!=0){
			CommonFunction f=new CommonFunction();
			for(int i=0;i<letorlist.size();i++){
				//+letorlist.get(i).getDoc()+" "
				String a=letorlist.get(i).getPreference()+" "+letorlist.get(i).getQid()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getCr()+" "+letorlist.get(i).getMethod();		
				//f.writeFile(fileborda, a);
				f.writeFile(filenewMethod, a);
				System.out.println(letorlist.get(i).getTotalscore()+" "+letorlist.get(i).getDoc()+" "+letorlist.get(i).getQid());
			}
		/*	String b=letorlist.get(0).getQid()+" "+letorlist.get(0).getCr()+" "+letorlist.size();
			f.writeFile(filecr, b);
			System.out.println("write file");*/
		}
	}

	private void combMNZ(String[][] docQid, int[][] partkey, int[] preference,
			int[] mnumber, double partcr) {
		// TODO Auto-generated method stub
		double scorearraay[] ;
		scorearraay=new double[partkey.length];
		ArrayList<Letor> letorlist=new ArrayList<Letor>();
		for(int i=0;i<partkey.length;i++){
			double score=0;
			for(int j=0;j<21;j++){//25
				score+=partkey[i][j];
			}
			score=score*mnumber[i];
			scorearraay[i]=score;
			System.out.println("end coloumn for score:"+score+" qid:"+docQid[i][0]+"doc:"+docQid[i][22]);//26
			Letor letor=new Letor();
			letor.setTotalscore(score);
			letor.setMethod("combMNZ");
			letor.setQid(docQid[i][0]);
			letor.setDoc(docQid[i][22]);//26
			letor.setPreference(preference[i]);
			letor.setCr(partcr);
			letorlist.add(letor);
		}
		Print(scorearraay,letorlist);
	}


	public void RoundRobin(String[][] DocQid,int[][] partkey,int[] preference, double partcr){
		double scorearraay[] ;
		scorearraay=new double[partkey.length];
		ArrayList<Letor> letorlist=new ArrayList<Letor>();
		for(int i=0;i<partkey.length;i++){
			double score=0;	
			for(int m=0;m<21;m++){//25
				score+=partkey[i][m];
			}
			scorearraay[i]=score;
			System.out.println("end coloumn for score:"+score+" qid:"+DocQid[i][0]+"doc:"+DocQid[i][22]);//26
			Letor letor=new Letor();
			letor.setTotalscore(score);
			letor.setMethod("RoundRobin");
			letor.setQid(DocQid[i][0]);
			letor.setDoc(DocQid[i][22]);//26
			letor.setPreference(preference[i]);
			letor.setCr(partcr);
			letorlist.add(letor);
		}
		Print(scorearraay,letorlist);
	}
	public static void main(String[] args) throws Exception {
		Methods lM=new Methods();
		lM.read();
	}
}
