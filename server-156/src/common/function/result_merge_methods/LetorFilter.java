package common.function.result_merge_methods;

import java.util.LinkedList;

import common.functions.io.CommonFunction;
import common.functions.io.ReadFromFile;

public class LetorFilter {
	int maxfilenumber;
	int maxcrnumber;
	int listchange[][];
	String doc[];
	String qid[];
	
	public void read(){
	LinkedList<LinkedList<String>> result =ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017spring\\元搜索\\合成算法代码\\MQ2008-agg\\Fold1\\test.txt" );
	//LinkedList<LinkedList<String>> result =  ReadFromFile.getInfo("E:\\元搜索\\MQ2008-agg\\Fold1\\train.txt");
	maxfilenumber=result.size();	
	LinkedList<LinkedList<String>> result1 =  ReadFromFile.getInfo("D:\\文件娱乐\\文件和照片\\2017spring\\元搜索\\合成算法代码\\MQ2008-agg\\Fold1\\cr-1.txt");
	//LinkedList<LinkedList<String>> result1 =  ReadFromFile.getInfo("E:\\元搜索\\合成算法\\9themServer-20160628\\CR1.txt");
	maxcrnumber=result1.size();
	int[] count;
	count =new int[maxcrnumber];
	String alllist[][];
	String listvalue[][];
	alllist =new String[maxfilenumber][36];
	listvalue=new String[maxfilenumber][25];
	for( int i=0;i<result.size();i++){
		for(int k=0;k<=24;k++){
  			listvalue[i][k]=result.get(i).get(k+2);
  		 }
		 for(int m=0;m<36;m++){
				alllist[i][m]= result.get(i).get(m);
			 } 
		}
	//处理存有结果重合率的文件
	for( int i=0;i<result1.size();i++){
			count[i]=Integer.parseInt(result1.get(i).get(2));
	}
	TransToInt(alllist,listvalue,count);
	}
	
	public void TransToInt(String[][] alllist,String[][] listvalue,int[] count){
		String[][] t;
		int[][] keyt;
		t =new String[maxfilenumber][25];
		keyt=new int[maxfilenumber][25];
		for(int i=0;i<maxfilenumber;i++){
			for(int j=0;j<9;j++){
				String temp=listvalue[i][j].toString();
				temp=temp.substring(2,temp.length());//将1:NULL 2:NULL 3:33 4:NULL 5:NULL 6:148这样的形式规范化，只存内容
				t[i][j]=temp;
				if(t[i][j].equals("NULL")){t[i][j]="0";}
				//System.out.println(t[i][j]+"IJ:"+i+" "+j);
				keyt[i][j]=Integer.parseInt(t[i][j]);
			}
			for(int j=9;j<25;j++){
				String temp=listvalue[i][j].toString();
				temp=temp.substring(3,temp.length());//将10:NULL 12:NULL 13:33 14:NULL 15:NULL 16:148这样的形式规范化，只存内容
				t[i][j]=temp;
				if(t[i][j].equals("NULL")){t[i][j]="0";}
				keyt[i][j]=Integer.parseInt(t[i][j]);
			}
		}
		TransToSorting(keyt,alllist,count);
	}
	
	private void TransToSorting(int[][] keyt,String[][] alllist,int[] count) {
		// TODO Auto-generated method stub
		doc=new String[maxfilenumber];
		qid=new String[maxfilenumber];
		int temp[][];
		temp=new int[maxfilenumber][25];
		int inicount=0;
		for(int i=0;i<maxcrnumber;i++){
			int[] a=new int[count[i]];
			int sum=inicount+count[i];
			for(int j=inicount;j<sum;j++){//用于不重复的统计qid doc
				qid[j]=alllist[j][1];
				doc[j]=alllist[j][29];}
			for(int x=0;x<25;x++){
				for(int t=0;t<count[i];t++){a[t]=0;}//初始化每列的计数情况
				int count0=0;//记录每一列中null的个数
				for(int j=inicount;j<sum;j++){//按列开始统计，进入qid块
					if (keyt[j][x]==0){count0++;}
					}//完成对null的统计
						
				for(int j=inicount;j<sum;j++){//开始具体排位统计
					if(keyt[j][x]!=0){
						int tempofsmall=0;
						for(int s=inicount;s<sum;s++){
							if(keyt[j][x]>keyt[s][x]){tempofsmall++;}
						}
			//	System.out.println(x+" j: "+j+" tempofsmall:"+tempofsmall+" count0:"+count0);
						a[j-inicount]=count[i]-tempofsmall;
						temp[j][x]=a[j-inicount];
						System.out.println(x+"j "+j+" if temp[j][x]:"+temp[j][x]);
					}else{
						a[j-inicount]=count[i];//NULL用并列第几的情况记录，不知道是否可行
						temp[j][x]=a[j-inicount];
						System.out.println(x+"j "+j+" else temp[j][x]:"+temp[j][x]);
					}
				}
			}
		//
		inicount=sum;
		System.out.println("inicount:"+inicount);
		}
		PrintOut(qid,doc,temp);
	}

	private void PrintOut(String[] qid, String[] doc, int[][] temp) {
		// TODO Auto-generated method stub
		String Changed[][];
		CommonFunction f=new CommonFunction();
		Changed =new String[maxfilenumber][27];
		String a;
		for(int i=0;i<maxfilenumber;i++){
			Changed[i][0]=qid[i];
			Changed[i][26]=doc[i];
			for(int j=0;j<25;j++){
				Changed[i][j+1]= String.valueOf(temp[i][j]);
			}
		String b=Changed[i][0]+" "+Changed[i][1]+" "+Changed[i][2]+" "+Changed[i][3]+" "+Changed[i][4]+" "+Changed[i][5]+" "+Changed[i][6]+" "+Changed[i][7]+" "+Changed[i][8]+" ";
		String c=Changed[i][9]+" "+Changed[i][10]+" "+Changed[i][11]+" "+Changed[i][12]+" "+Changed[i][13]+" "+Changed[i][14]+" "+Changed[i][15]+" "+Changed[i][16]+" "+Changed[i][17]+" ";
		String d=Changed[i][18]+" "+Changed[i][19]+" "+Changed[i][20]+" "+Changed[i][21]+" "+Changed[i][22]+" "+Changed[i][23]+" "+Changed[i][24]+" "+Changed[i][25]+" "+Changed[i][26]+" ";
		a=b+c+d; 
		f.writeFile("D:\\文件娱乐\\文件和照片\\2017spring\\元搜索\\合成算法代码\\MQ2008-agg\\Fold1\\changed.txt",a);
		}	 	
	}
	public static void main(String[] args) throws Exception {
		LetorFilter lf=new LetorFilter();
		lf.read();
	}
	
}
