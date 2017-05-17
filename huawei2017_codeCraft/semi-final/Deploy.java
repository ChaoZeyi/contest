package com.cacheserverdeploy.deploy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayDeque;


class Edges{
	//起点、终点、容量、流量、下一条、费用
	int from, to, capacity, flow, next, re, cost;
	public Edges(){
	}
}
public class Deploy
{
	/**
     * 你需要完成的入口
     * <功能详细描述>
     * @param graphContent 用例信息文件
     * @return [参数说明] 输出结果信息
     * @see [类、类#方法、类#成员]
     */
	static int numberOfPoint=0;  //网络节点的个数
	static int numberOfEdge=0;    //网络边的条数
	static int numberOfGuest=0;     //网络用户节点的个数
	public static int V;// 	总的节点个数
	private static final int MAXEDGE=30000;  //设定最大的边的数目为30000
	private static final int MAX = 1<<30; 
	static Edges[] edge = new Edges[MAXEDGE];
	static int[] edgeHead;
	static int idx;//	边的数量索引
	static int flow,cost,ans;
	static boolean vis[];
	static int d[],cur[];
	static int s,t;
	static List<String> resultList = new ArrayList<>();
	static List<List<Integer>> pathList = new ArrayList<List<Integer>>();
	static List<Integer>[] pathArray;
	static List<Integer> flowUsed = new ArrayList<>();
	static int[] priceOfServer=new int[10];  //网络中部署不同档次服务器的价格
	static int[] capOfServer=new int[10];  //网络中部署不同档次服务器的容量
	static int[] priceOfPoint;
	static HashMap<Integer,Integer> checkFlow = new HashMap<>();
	static HashMap<Integer,Integer> serverIdx = new HashMap<>();
	static int numOfLevel = 0;
	static HashMap<String, int[]> map=new HashMap<String, int[]>();
	static HashMap<Integer,Integer> GuestOfVertex=new HashMap();
	static int[] serverseries;
	static int resultindex=0;
	static int mincost=MAX;
	static String netInformation=new String(); 
	static int[] netInformationInteger=new int[3];
	static List<Integer>[] pointConnectInformation;
	static int flowvalue=0;
	static  Integer[] allServer;
	//将txt文件中的边信息储存在edgeInformation矩阵中
	static int [][] edgeInformation;
	//将txt文件中的用户信息储存在guestInformation矩阵中
	static int [][] guestInformation;
	static int group_count=0;//种群数量
	static int numberofhighneed=0;
	static int numberofhighdegree=0;
	static int numberofbest=0;
	static int numberofbest1=0;
	static int [] GuestNeed;//保存消费节点i的需求
	static int [] orderOfGuestNeed;  //保存消费节点的排序
	static int [] VertexofGuest;//保存与消费节点相连的网络节点
	static int[] lowneedguest;//保存需求低低的用户节点
	static int[] bestseries;//保存初始最优序列
	static int[] sortDeployCost;
	static int[] sortDeployCap;
	static int[] sortDeployDegree;
	static ArrayList<Integer> highneedguest=new ArrayList<>();//保存需求低低的用户节点
	static ArrayList<Integer> highvaluevertex=new ArrayList<>();//保存度很大的网络节点 
	static ArrayList<int[]> bestpopulation=new ArrayList<>();
	static ArrayList<int[]> bestpopulation1=new ArrayList<>();
	static ArrayList<Integer> level1=new ArrayList<>();
	static ArrayList<Integer> level2=new ArrayList<>();
	static ArrayList<Integer> level3=new ArrayList<>();
	static ArrayList<Integer> level4=new ArrayList<>();
	static ArrayList<Integer> level5=new ArrayList<>();
	static ArrayList<Integer> level6=new ArrayList<>();
	static int[] bestseries1;
	static int[] bestseries2;
	static int[] bestseries3;
	static int[] bestseries4;
	static int[] bestseries5;
	static int[] bestseries6;
	static int[] levelpos=new int[5];
	static int lengthofseries=0;
	static ArrayList<Integer> importpos=new ArrayList<>();
	static List<Integer> nonimportpos=new ArrayList<>();
	static HashMap<Integer, Integer> importposcount=new HashMap<>();
	static HashMap<Integer, Integer> levelofpoint=new HashMap<>();
	static int importposnumber=0;
	static int costofbestseries6=MAX;
	static ArrayList<Integer> commonpos=new ArrayList<>();//保存需求低低的用户节点
    @SuppressWarnings("unchecked")
	public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
    	/*************************下面是静态变量的初始化*****************************/
    	netInformation=graphContent[0];
    	netInformationInteger=stringToInteger(netInformation);
    	numberOfPoint=netInformationInteger[0];  //网络节点的个数
		numberOfEdge=netInformationInteger[1];    //网络边的条数
		numberOfGuest=netInformationInteger[2]; 
    	edgeInformation=new int[numberOfEdge][4];
    	guestInformation=new int[numberOfGuest][3];
    	pointConnectInformation = new List[numberOfPoint];
    	for(int j = 0; j < numberOfPoint; j++)
    		pointConnectInformation[j] = new ArrayList();
    	int k = 0;
    	int m = 0;
    	for(int i = numberOfPoint;i < graphContent.length;i++)
    	{
    		String[] contentReadLine = graphContent[i].split(" ");
    		//System.out.println(graphContent[i]+"   "+i);
    		if(contentReadLine.length == 4)
    		{
//    			System.out.println(contentReadLine[i]);
    			for(int j = 0; j < 4; j++)
    				edgeInformation[k][j] = Integer.parseInt(contentReadLine[j]);
				
				k = k + 1;
    		}
    		if(contentReadLine.length == 3)
    		{
    			for(int j = 0; j < 3; j++)
    				guestInformation[m][j] = Integer.parseInt(contentReadLine[j]);
    			m = m + 1;
    		}
    	}
		//group_count=(numberOfPoint>500)? 5:(numberOfPoint>200)? 70:100;
		group_count=(numberOfPoint>800)? 18:70;
		lengthofseries=(int)((numberOfPoint<800)? (1.3*numberOfGuest):1.1*numberOfGuest);//序列长度
		numberofbest=(numberOfPoint>800)?4:16;//序列长度
		numberofbest1=(numberOfPoint>800)?5:20;//序列长度
		
		serverseries=new int[lengthofseries];
		numberofhighneed=(int) (numberOfGuest);//需求较高的消费节点个数
		GuestNeed=new int[numberOfGuest];
		importposnumber=lengthofseries;
		int[] GuestNeedTemp=new int[numberOfGuest];
		orderOfGuestNeed=new int[numberOfGuest];
		VertexofGuest=new int[numberOfGuest];
		bestseries=new int[lengthofseries];
		bestseries1=new int[lengthofseries];
		bestseries2=new int[lengthofseries];
		bestseries3=new int[lengthofseries];
		bestseries4=new int[lengthofseries];
		bestseries5=new int[lengthofseries];
		bestseries6=new int[lengthofseries];
		sortDeployCost=new int[lengthofseries];
		sortDeployCap=new int[lengthofseries];
		sortDeployDegree=new int[lengthofseries];
		for(int i=0;i<lengthofseries;i++)
			importpos.add(i);
		
		for(int i=0;i<numberOfGuest;i++){
			VertexofGuest[i]=guestInformation[i][1];
			GuestOfVertex.put(guestInformation[i][1], i);
			GuestNeed[i]=guestInformation[i][2];
			GuestNeedTemp[i]=guestInformation[i][2];
		}
		for(int i=0;i<numberOfGuest;i++)
			orderOfGuestNeed[i]=i;
		int temp1=0,temp2=0;
		for(int i=0;i<numberOfGuest;i++)
			for(int j=i+1;j<numberOfGuest;j++)
				if(GuestNeedTemp[i]>GuestNeedTemp[j]){
					temp1=GuestNeedTemp[i];
					GuestNeedTemp[i]=GuestNeedTemp[j];
					GuestNeedTemp[j]=temp1;
					temp2=orderOfGuestNeed[i];
					orderOfGuestNeed[i]=orderOfGuestNeed[j];
					orderOfGuestNeed[j]=temp2;
				}
		initGraphInfo(graphContent);
		AckLevelOfpoint();
		
		/******************************初始化完毕***********************************/
		//用户的流量总需求    
		for(int i=0;i<numberOfGuest;i++)
			flowvalue=flowvalue+guestInformation[i][2];
		
		//System.out.println("总流量需求"+ flowvalue);
		String[] output=new String[MAXEDGE - numberOfPoint + 2];
		HashMap<Integer, Integer> serverInformation=new HashMap<>();//最优服务器布置情况          
		long startTime=System.currentTimeMillis();
		long endTime=System.currentTimeMillis();
		float excTime=(float)(endTime-startTime)/1000;
		/**
		 * 下面用遗传算法求解
		 */
//		deleteByGuestNumber();
//		deleteByPointNumber();
		findbestvertex();// 得到消费较高的与消费节点直连的网络节点和度较高的中心网络节点
		int[] res=getseries();
		System.arraycopy(res, 0, bestseries1, 0, lengthofseries);
		System.arraycopy(res, 0, bestseries2, 0, lengthofseries);
		System.arraycopy(res, 0, bestseries3, 0, lengthofseries);
		System.arraycopy(res, 0, bestseries, 0, lengthofseries);
		System.arraycopy(res, 0, bestseries4, 0, lengthofseries);
		System.arraycopy(res, 0, bestseries5, 0, lengthofseries);
		//for(int i=0;i<lengthofseries/10;i++)
			//bestseries[i]=-1;
		
		sortDeployDegree=sortByDegree();
		if(numberOfPoint<800)
		findbestlocation();
		else
			findbestlocationhigh();
		 // System.out.println("进化前："+fitness(bestseries5));
		endTime=System.currentTimeMillis();
		excTime=(float)(endTime-startTime)/1000;
		System.out.println("第一个最优个体所花时间："+excTime);
		
		//gen_bestpopulation(bestpopulation1);
		if(numberOfPoint<800){
			gen_bestpopulation(bestpopulation);
	    ArrayList<int[]> population=new ArrayList<>();//该线性表保存种群的信息，每个个体是一个表示服务器序列的数组
	    population=gen_population(lengthofseries, group_count);
	    int count=0;
	    double time=78;
	    while(excTime<time){
	    	count++;
	    	population=evolve(population, 0.2, 0.05, 0.8);
	    	endTime=System.currentTimeMillis();
			excTime=(float)(endTime-startTime)/1000;
			//System.out.println("第"+count+"轮进化结果："+mincost);
	    }
	 
	    replaceNext(excTime, serverseries);
	   // System.out.println(fitness(serverseries));
		}
		
		else {
			System.arraycopy(bestseries, 0, serverseries, 0, lengthofseries);
		}
	    for(int i=0;i<lengthofseries;i++){
	    	if(i<highneedguest.size())
	    		serverInformation.put(highneedguest.get(i),serverseries[i]);//服务器所在的位置信息
	    	else serverInformation.put(highvaluevertex.get(i-highneedguest.size()),serverseries[i]);//服务器所在的位置信息
	    }
		int[] result=new int[2];
		result= spfaMCMF_addDecLevel(serverInformation);
		Iterator iterator = serverInformation.keySet().iterator();
		List<Integer> serverSeries_deployed = new ArrayList();
		while(iterator.hasNext())
		{
			int key = (int)iterator.next();
			int value = serverInformation.get(key);
			if(value >= 0)
			{
				serverSeries_deployed.add(key);
				addedge(V - 1,key,capOfServer[value],0,0);
			}
		}
		for(int i = 0; i < edge.length; i++)
		{ 
			if(edge[i] == null)
				break;
			 edge[i].flow = flowUsed.get(i) ;
		}
		while(outputPath(V-1,V-2))
			{
			}
		//	判断是否可以降档
		HashMap<Integer,Integer> realFlow = new HashMap<>();
		HashMap<Integer,Integer> realLevel = new HashMap<>();
		HashMap<Integer,Integer> finalLevel = new HashMap<>();
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			realFlow.put(serverSeries_deployed.get(i), 0);
			realLevel.put(serverSeries_deployed.get(i), serverInformation.get(serverSeries_deployed.get(i)));
			finalLevel.put(serverSeries_deployed.get(i), serverInformation.get(serverSeries_deployed.get(i)));
		}
		for(int i = 0; i < resultList.size(); i++)
       {
			String[] pathInfomation = resultList.get(i).split(" ");
			int IdOfServer = Integer.parseInt(pathInfomation[0]);
			int flowTemp  = Integer.parseInt(pathInfomation[pathInfomation.length - 1]);
			realFlow.put(IdOfServer, flowTemp + realFlow.get(IdOfServer));
    	   //output[i + 2] = resultList.get(i);
       }
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			int flowTemp  = realFlow.get(serverSeries_deployed.get(i));
			for(int j = 0; capOfServer[j] > 0; j++ )
				if(flowTemp <= capOfServer[j])
				{
					//add_level = j;
					finalLevel.put(serverSeries_deployed.get(i), j);
					break;
				}
			
		}

		for(int i = 0; i < resultList.size(); i++)
	       {
				//int add_level = numOfLevel - 1;
				String[] pathInfomation = resultList.get(i).split(" ");
				int IdOfServer = Integer.parseInt(pathInfomation[0]);
	    	   output[i + 2] = resultList.get(i) + " " + finalLevel.get(IdOfServer);
	       }
		//System.out.println(realLevel);
		//System.out.println("降档前的花费" + result[1]);
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			//System.out.println(serverSeries_deployed.get(i));
			int realServerLevel = realLevel.get(serverSeries_deployed.get(i));
			int finalServerLevel = finalLevel.get(serverSeries_deployed.get(i));
			//result[1] -= (priceOfServer[realServerLevel] - priceOfServer[finalServerLevel]);
		}
		//System.out.println("降档后的花费" + result[1]);
		output[0]=Integer.toString(resultList.size());
		output[1]="";
		return output;
    }	
  
    public static void AckLevelOfpoint(){
    	HashMap<Integer, Integer> capofpoint=new HashMap<>();
    	capofpoint=CountCap();
    	for(int i=0;i<numberOfPoint;i++){
    		int bestlevel=0;
    		double minppr=MAX;
    		for(int j=0;j<6;j++){
    			int cost=priceOfServer[j]+priceOfPoint[i];
    			int cap=Math.min(capOfServer[j], capofpoint.get(i));
    			double ppr=(double)cost/cap;
    			if(ppr<minppr){
    				minppr=ppr;
    				bestlevel=j;
    			}
    		}
    		levelofpoint.put(i, bestlevel);
    	}
    	for(int i = 0; i < numberOfGuest; i++)
		{
			int flowNeeded = guestInformation[i][2];
			int pointServer  = guestInformation[i][1];
			while(capOfServer[levelofpoint.get(pointServer)] < flowNeeded && (levelofpoint.get(pointServer) < (numOfLevel -1)))
			{
				levelofpoint.put(pointServer, levelofpoint.get(pointServer) + 1);
			}
		}
    }
    public static int[] getseries(){
    	int[] res=new int[lengthofseries];
        for(int i=0;i<lengthofseries;i++){
    	if(i<highneedguest.size())
    		res[i]=levelofpoint.get(highneedguest.get(i));//服务器所在的位置信息
    	else res[i]=levelofpoint.get(highvaluevertex.get(i-highneedguest.size()));//服务器所在的位置信息
        }
    return res;
    }
    public static void gen_bestpopulation(ArrayList<int[]> population){
    	
    	for(int i=0;i<numberofbest;i++){
    		
    		int[] tempbestseries=new int[lengthofseries];
    		System.arraycopy(bestseries2, 0, tempbestseries, 0, lengthofseries);
    		//for(int j=0;i<level1.size();i++)
    			//tempbestseries[j]=-1;
    		if(Math.random()<0.5)
    		findrandombestlocation(tempbestseries);
    		else
    			findrandombestlocation2(tempbestseries);
    		population.add(tempbestseries);
    		//System.out.println(Arrays.toString(tempbestseries));
    		
    		
    	}
    	
    	
        
    }
    public static void RandomOrderArray(int[] a){
    	if(a.length!=0){
    	Random random=new Random();
    	for(int i=0;i<a.length/4;i++){
    		int pos1=random.nextInt(a.length);
    		int pos2=random.nextInt(a.length);
    		swap(a, pos1, pos2);
    	}
    	}
    }
    public static int[] GetRandomOrderSeries(){
    	int[] templevel1=listtoarray(level1);
    	int[] templevel2=listtoarray(level2);
    	int[] templevel3=listtoarray(level3);
    	int[] templevel4=listtoarray(level4);
    	int[] templevel5=listtoarray(level5);
    	int[] templevel6=listtoarray(level6);
    	int length=templevel1.length+templevel2.length+templevel3.length+templevel4.length+templevel5.length+templevel6.length;
    	int[] result=new int[length];
    	RandomOrderArray(templevel1);
    	RandomOrderArray(templevel2);
    	RandomOrderArray(templevel3);
    	RandomOrderArray(templevel4);
    	RandomOrderArray(templevel5);
    	RandomOrderArray(templevel6);
    	System.arraycopy(templevel1, 0,result, 0, templevel1.length);
    	System.arraycopy(templevel2, 0,result, templevel1.length, templevel2.length);
    	System.arraycopy(templevel3, 0,result, templevel1.length+templevel2.length, templevel3.length);
    	System.arraycopy(templevel4, 0,result, templevel1.length+templevel2.length+templevel3.length, templevel4.length);
    	System.arraycopy(templevel5, 0,result, templevel1.length+templevel2.length+templevel3.length+templevel4.length, templevel5.length);
    	System.arraycopy(templevel6, 0,result,length-templevel6.length, templevel6.length);
    	return result;
    } 
    /**
     * 得到消费较高的与消费节点直连的网络节点和度较高的中心网络节点
     */
    public static void findbestvertex(){
    	int[] temp=new int[numberOfGuest];
		System.arraycopy(GuestNeed, 0, temp, 0, numberOfGuest);
		int kthMaxNeed=getKthValue(temp, numberOfGuest-numberofhighneed);//第k大的需求
		//if(numberOfPoint<200){
			//for(int i=0;i<numberOfGuest;i++){//将需求小的消费节点放入此表
				//if(GuestNeed[i]>kthMaxNeed)
					//highneedguest.add(VertexofGuest[i]);
			//}
		//}
		for(int i=0;i<numberOfGuest;i++){//将需求小的消费节点放入此表
			if(GuestNeed[orderOfGuestNeed[i]]>=kthMaxNeed)
				highneedguest.add(VertexofGuest[orderOfGuestNeed[i]]);
		}
		for(int i=0;i<highneedguest.size();i++){
			if(i<highneedguest.size()/5)
				level1.add(i);
			else if(i<2*highneedguest.size()/5)
				level2.add(i);
			else if(i<3*highneedguest.size()/5)
				level3.add(i);
			else if(i<4*highneedguest.size()/5)
				level4.add(i);
			else if(i<9*highneedguest.size()/10)
				level5.add(i);
			else level6.add(i);
		}
		//System.out.println("消费节点上的服务器个数："+highneedguest.size());
		numberofhighdegree=lengthofseries-highneedguest.size();//中心节点个数
		HashMap<Integer, Integer> valueofpoint=new HashMap<>();
		valueofpoint=CountValue();
		int[] current=new int[numberOfPoint];
		Iterator iter = valueofpoint.entrySet().iterator(); 
		for(int i=0;iter.hasNext();i++) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		   // System.out.println("节点"+entry.getKey()+"的度为："+(int) entry.getValue()); 
		    current[i]=(int) entry.getValue();
		} 
		ArrayList<Integer> importvertex=new ArrayList<>();
		int kthmaxdegree=getKthValue(current, numberOfPoint-numberofhighdegree);
		for(int key:valueofpoint.keySet()){
			if(valueofpoint.get(key)>=kthmaxdegree&&!highneedguest.contains(key))
				importvertex.add(key);
			if(importvertex.size()==numberofhighdegree)
				break;
		}
		HashMap<Integer, Integer> ordermap=new HashMap<>();
		int[] orderofvertex=new int[numberofhighdegree];
		for(int i=0;i<numberofhighdegree;i++)
			orderofvertex[i]=i;
		int[] Temp=new int[numberofhighdegree];
		for(int i=0;i<importvertex.size();i++){
			Temp[i]=valueofpoint.get(importvertex.get(i));
			ordermap.put(i, importvertex.get(i));
		}
		int temp1=0,temp2=0;
		for(int i=0;i<numberofhighdegree;i++)
			for(int j=i+1;j<numberofhighdegree;j++)
				if(Temp[i]>Temp[j]){
					temp1=Temp[i];
					Temp[i]=Temp[j];
					Temp[j]=temp1;
					temp2=orderofvertex[i];
					orderofvertex[i]=orderofvertex[j];
					orderofvertex[j]=temp2;
				}
		
		for(int i=0;i<numberofhighdegree;i++){
			double a=Math.random();
			//if(a<0.3)
				//level4.add(i+highneedguest.size());
			 if(i<numberofhighdegree/2)
				level5.add(i+highneedguest.size());
			else 
				level6.add(i+highneedguest.size());
			highvaluevertex.add(ordermap.get(orderofvertex[i]));
		}
		
    }
  
    public static void findrandombestlocation(int[] bestseries111){
        int[] order=GetRandomOrderSeries();
        int[] tempseries=new int[lengthofseries];
    	for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries111, 0, tempseries, 0, lengthofseries);
    		if(order[i]>=(int)(0.8*numberOfGuest)){
    			tempseries[order[i]]=-1;
    			if(fitness(tempseries)<fitness(bestseries111))
    				bestseries111[order[i]]=-1;
    		}
    		else	
    			bestseries111[order[i]]=bestseries1[order[i]];	
    	}
    	//System.out.println("bestseries111:"+fitness(bestseries111));
    }
    public static void findrandombestlocation1(int[] bestseries111){
        int[] order=GetRandomOrderSeries();
        int[] tempseries=new int[lengthofseries];
    	for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries111, 0, tempseries, 0, lengthofseries);
    		if(order[i]>=(int)(0.8*numberOfGuest)){
    			tempseries[order[i]]=-1;
    			if(fitness(tempseries)<fitness(bestseries111))
    				bestseries111[order[i]]=-1;
    		}
    		else	
    			bestseries111[order[i]]=bestseries3[order[i]];	
    	}
    	//System.out.println("bestseries111:"+fitness(bestseries111));
    }
    public static void findrandombestlocation2(int[] bestseries111){
        int[] order=GetRandomOrderSeries();
        int[] tempseries=new int[lengthofseries];
    	for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries111, 0, tempseries, 0, lengthofseries);
    		if(order[i]>=(int)(0.8*numberOfGuest)){
    			tempseries[order[i]]=-1;
    			if(fitness(tempseries)<fitness(bestseries111))
    				bestseries111[order[i]]=-1;
    		}
    		else	
    			bestseries111[order[i]]=bestseries[order[i]];	
    	}
    	//System.out.println("bestseries111:"+fitness(bestseries111));
    }
    /**
     * 找到最好的序列
     */
    /**
     * 找到最好的序列
     */
    public static void findbestlocation(){
    	int[] tempseries=new int[lengthofseries];
    	
		for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries, 0, tempseries, 0, lengthofseries);
    			tempseries[sortDeployDegree[i]]=-1;
    			if(fitness(tempseries)<fitness(bestseries))
    				bestseries[sortDeployDegree[i]]=-1;
    		}
		//replaceNext(10, bestseries);
		replaceNext(10, bestseries);
		replaceNext(10, bestseries);
		replaceNext(10, bestseries);
		//System.out.println("bestseries:"+fitness(bestseries));
		for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries1, 0, tempseries, 0, lengthofseries);
    			tempseries[i]=-1;
    			if(fitness(tempseries)<fitness(bestseries1))
    				bestseries1[i]=-1;
    		}
		
		//System.out.println("bestseries1:"+fitness(bestseries1));
		sortDeployCap=sortByCap();
		for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries3, 0, tempseries, 0, lengthofseries);
    			tempseries[sortDeployCap[i]]=-1;
    			if(fitness(tempseries)<fitness(bestseries3))
    				bestseries3[sortDeployCap[i]]=-1;
    		}
		//System.out.println("bestseries3:"+fitness(bestseries3));
    }
    
    public static void findbestlocationhigh(){
    	int[] tempseries=new int[lengthofseries];
    	long startTime=System.currentTimeMillis();
     	for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries, 0, tempseries, 0, lengthofseries);
    		tempseries[i]=-1;
    		int tempfitness=fitness(tempseries);
    		int bestfitness=fitness(bestseries);
    		if(tempfitness<bestfitness)
    			bestseries[i]=-1;
     	}
     	//System.out.println("bestseries:"+fitness(bestseries));
     	long endTime=System.currentTimeMillis();
		float excTime=(float)(endTime-startTime)/1000;
     	replaceNext(excTime,bestseries);
     	//System.out.println("bestseries:"+fitness(bestseries));
    }  
    public static int[] listtoarray(ArrayList<Integer> l){
    	int[] temp=new int[l.size()];
    	for(int i=0;i<l.size();i++)
    		temp[i]=l.get(i);
    	return temp;
    }
    public static ArrayList<Integer> arraytolist(int[] a){
    	ArrayList<Integer> temp=new ArrayList<>();
    	for(int i=0;i<a.length;i++)
    		temp.add(a[i]);
    	return temp;
    }
    
    /**
     * 产生随机的服务器序列
     * @param length 序列长度，即消费节点数量
     * @return 一个随机的服务器系列
     */
    public static int[] gen_chromosome(int length){
    	Random random=new Random();
    	
    	int[] series=new int[length];
    	for(int i=0;i<length;i++){
    		if(random.nextInt(2)==0)
    			series[i]=bestseries2[i];
    		else series[i]=-1;
    	}
		return series;
    } 
   
    
    /**
     * 初始化种群
     * @param length 每个个体（服务器序列）的序列长度，即消费节点数目
     * @param count 种群数量
     * @return 初始种群
     */
    public static ArrayList<int[]> gen_population(int length,int group_count){
    	ArrayList<int[]> population=new ArrayList<>();
    	int[] a=new int[lengthofseries];
    	for(int i=0;i<a.length;i++)
    		a[i]=bestseries2[i];
    	if(numberOfPoint<200)
    	population.add(a);
    	else{
    		population.add(bestseries);
    		//population.add(bestseries1);
    		//population.add(bestseries2);
    		//population.add(bestseries3);
    		population.add(bestseries1);
    		population.add(bestseries3);
    		for(int i=0;i<bestpopulation.size();i++)
    			population.add(bestpopulation.get(i));
    		//for(int i=0;i<bestpopulation1.size();i++)
    			//population.add(bestpopulation1.get(i));
    	}
    	while(population.size()<group_count){
	    	int[] serverseries=new int[length];
	    	if(numberOfPoint<200)
	    	serverseries=gen_chromosome(length);
	    	else serverseries=gen_chromosome(length);
	    	population.add(serverseries);
	    }
    	return population;
    }
    /**
     * 输入某个服务器部署序列，返回其开销（链路开销+服务器开销）
     * @return 在该部署方案下的总开销
     */
    public static int fitness(int[] serverseries){ 
    	String string=Arrays.toString(serverseries);
		HashMap serverInformation=new HashMap<>();
		 for(int i=0;i<lengthofseries;i++){
		    	if(i<highneedguest.size())
		    		serverInformation.put(highneedguest.get(i),serverseries[i]);//服务器所在的位置信息
		    	else 
		    		serverInformation.put(highvaluevertex.get(i-highneedguest.size()),serverseries[i]);//服务器所在的位置信息
		    }
//		 List<Integer> allServerList = new ArrayList();
//		 allServerList.addAll(highneedguest);
//		 allServerList.addAll(highvaluevertex);
//		 allServer = new Integer[lengthofseries];
//		 allServerList.toArray(allServer);
//		 for(int i = 0; i < lengthofseries; i++)
//		 {
//			 for(int j = i + 1; j < lengthofseries; j++)
//			 {
//				 if(allServer[i] > allServer[j])
//				 {	
//					 int temp = allServer[i];
//					 allServer[i] = allServer[j];
//					 allServer[j] = temp;
//				 }
//				 
//				 
//			 }
//		 }
//		 for(int i=0;i<lengthofseries;i++)
//			 serverInformation.put(allServer[i],serverseries[i]);
    	if(map.containsKey(string))
    		return (map.get(string)[0]==flowvalue)?(map.get(string)[1]):MAX;
		int[] result = new int[2];
		result=spfaMCMF_addDecLevel(serverInformation);
		//result=spfaMCMF(serverInformation);//输出最大流，最小花费
		map.put(string, result);
		if(result[0]!=flowvalue)
			return MAX;
		return result[1];
    }
    /**
     * 在现有种群中选择一部分适应性强（即开销小）的个体和一些幸存个体加入种群
     * 作为繁衍下一代的parents
     * @param population 现有的种群
     * @param retain_rate 竞争系数
     * @param random_selection_rate 幸存系数
     * @return 生存下来的种群，即下一代的parents
     */
    public static ArrayList<int[]> selection(ArrayList<int[]> population,double retain_rate,double random_selection_rate){
    	ArrayList<int[]> parents=new ArrayList<>();//存放生存下来的种群
    	int[] fitness=new int[group_count];
    	for(int i=0;i<population.size();i++){
    		fitness[i]=fitness(population.get(i));
    	}
    	//按适应性从大到小排序的第k个个体的fitness
    	int a[]=new int[group_count];
    	System.arraycopy(fitness, 0, a, 0,group_count);
    	int kthfitness=getKthValue(a,(int) (group_count*retain_rate));
    	for(int j=0;j<group_count;j++)//将适应性最强的k个个体选择出来加入parents中
    	{
    		if(fitness[j]<mincost){
    			resultindex=j;
    			mincost=fitness[j];
    			System.arraycopy(population.get(resultindex), 0, serverseries, 0, lengthofseries);
    		}//找到开销 
    		if(fitness[j]<=kthfitness)
    			parents.add(population.get(j));
    		//if(fitness[j]==MAX)
    			//population.remove(j);
    		else if((Math.random()<random_selection_rate)&&(fitness[j]!=MAX))
    			parents.add(population.get(j));
    	}
    	//System.out.println("实际开销为："+fitness(serverseries));
    	return parents;
    }
    /**
     * 对parents做交叉操作
     * @param parents 待交叉的parents
     * @return 交叉之后的种群，包含parents和新产生的孩子
     */
    
    public static ArrayList<int[]> crossover(ArrayList<int[]> parents){
    	ArrayList<int[]> population=new ArrayList<>();//存放交叉之后的种群结果
    	population.addAll(parents);//将parents放入种群
    	int SizeOfChildren=group_count-parents.size();
    	Random random=new Random();
    	for(int i=0;i<SizeOfChildren;i++){
    		int[] male=parents.get(random.nextInt(parents.size()));//随机选取父母做交叉
    		int[] female=parents.get(random.nextInt(parents.size()));
    		int crosspos=0;//选择交叉位置
    		int length=level1.size()+level2.size()+level3.size()+level4.size();
    		int[] crossresult=new int[lengthofseries];
    		crosspos=length+random.nextInt(lengthofseries-length);
    	    System.arraycopy(male, 0,crossresult,0 , crosspos);
    	    System.arraycopy(female, crosspos,crossresult,crosspos,lengthofseries-crosspos);
    	    population.add(crossresult);//将交叉得到的孩子放入种群
    	}
    	return population;
    }
    /**
     * 对种群中的基因进行变异
     * @param population 输入种群
     * @param mutation_rate 变异概率
     */
    public static void mutation(ArrayList<int[]> population,double mutation_rate){
    	Random random=new Random();
          for(int[] a:population){
        	  if(Math.random()<mutation_rate){
        		  ArrayList<Integer> dep=getdeployedpos(a);
        		 // int length=level1.size()+level2.size()+level3.size()+level4.size();
        		 // int mutationpos=length+random.nextInt(lengthofseries-length);//随机选取个体中的某个基因
        		  //a[mutationpos]=random.nextInt(7)-1;  
        		 // int mutationpos=random.nextInt(lengthofseries);
        		 //if(a[mutationpos]>=0)
        			 //a[mutationpos]=-1;
        		 //else
        		  int mutationpos1=dep.get(random.nextInt(dep.size()));
        		  int mutationpos2=dep.get(random.nextInt(dep.size()));
        		  while(mutationpos2==mutationpos1)
        			  mutationpos2=dep.get(random.nextInt(dep.size()));
        		     if(a[mutationpos1]!=5)
        			 a[mutationpos1]+=1;
        			 a[mutationpos2]-=1;
        		  
        	  }
          }
    }
    public static ArrayList<Integer> getundeployedpos(int[] series){
    	ArrayList<Integer> result=new ArrayList<>();
    	for(int i=0;i<series.length;i++){
    		if(series[i]==-1)
    			result.add(i);
    	}
    	return result;
    }
    /**
     * 得到部署了服务器的位置
     * @param series
     * @return
     */
    public static ArrayList<Integer> getdeployedpos(int[] series){
    	ArrayList<Integer> result=new ArrayList<>();
    	for(int i=0;i<series.length;i++){
    		if(series[i]>=0)
    			result.add(i);
    	}
    	return result;
    }
    /**
     * 种群的进化过程，包括选择、交叉、变异
     * @param population 初始种群
     * @param retain_rate 竞争系数
     * @param random_selection_rate 幸存系数
     * @param mutation_rate 变异系数
     */
    public static ArrayList<int[]>  evolve(ArrayList<int[]> population,double retain_rate,double random_selection_rate,double mutation_rate){
    	ArrayList<int[]> parents=new ArrayList<>();
    	parents=selection(population, retain_rate, random_selection_rate);
    	ArrayList<int[]> newpopulation=new ArrayList<>();
    	newpopulation=crossover(parents);
    	mutation(newpopulation,mutation_rate);
    	return newpopulation;
    }
   // public static int getsuitlevel(int cost){
    	
    //}
  //将含有多个数字的字符串转变为数字
  	public static int[] stringToInteger(String graphContent)
      {
          /**do your work here**/
  		String information=graphContent;		
  		String[] inputInformation=new String[information.length()];
  		int j=0,count=0;
  		for(int i=0;i<information.length();i++){
  			if(information.charAt(i)!=' '){
  				if(inputInformation[j]==null) 
  					inputInformation[j]=String.valueOf(information.charAt(i));
  				else
  					inputInformation[j]=inputInformation[j]+information.charAt(i);
  			}
  			else {
  					j++;
  			}
  		}
  		for(int i=0;i<information.length();i++){ 
  			if(inputInformation[i]!=null)
  				count++;
  		}
  		int[] output=new int[count];
  		for(int i=0;i<count;i++){ 
  			output[i]=Integer.parseInt(inputInformation[i]);
  		}
  		return output;
  	}
	/**
	  * 
	  * @return 每个节点的通量
	  */
	 public static HashMap<Integer, Integer> CountValue(){
	    	HashMap<Integer, Integer> valuemap=new HashMap<>();
	    	for(int i=0;i<numberOfEdge;i++){	
	    		if(valuemap.containsKey(edgeInformation[i][0]))
	    			valuemap.put(edgeInformation[i][0],valuemap.get(edgeInformation[i][0])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][0], edgeInformation[i][2]);
	    		if(valuemap.containsKey(edgeInformation[i][1]))
	    			valuemap.put(edgeInformation[i][1],valuemap.get(edgeInformation[i][1])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][1], edgeInformation[i][2]);	
	    	}
	    	for(int key:valuemap.keySet()){
	    		valuemap.put(key, valuemap.get(key)-priceOfPoint[key]/50);
	    	}
	    	return valuemap;
	    }
	/**
	 * 
	 * @return 每个节点的度
	 */
	 public static HashMap<Integer, Integer> CountDegree(){
	    	HashMap<Integer, Integer> degreecount=new HashMap<>();
	    	for(int i=0;i<numberOfEdge;i++){
	    		if(degreecount.containsKey(edgeInformation[i][0]))
	    			degreecount.put(edgeInformation[i][0],degreecount.get(edgeInformation[i][0])+1);
	    		else degreecount.put(edgeInformation[i][0], 1);
	    		if(degreecount.containsKey(edgeInformation[i][1]))
	    			degreecount.put(edgeInformation[i][1],degreecount.get(edgeInformation[i][1])+1);
	    		else degreecount.put(edgeInformation[i][1], 1);
	    	}
	    	return degreecount;
	    }
	 /**
		 * 
		 * @param graphContent 读入文件内容，初始化边信息
		 */
	 public static int aug(int u, int flow)  
		{  
		    if(u == t)  
		    {   
		        return flow;  
		    }  
		    vis[u] = true;  
			for(int i = cur[u]; i != -1; i = edge[i].next)
			{
				int v = edge[i].to;
		        if(edge[i].capacity > edge[i].flow && d[u] == d[v] + edge[i].cost && !vis[v])  
		        {  
		            int delta = aug(v, Math.min(flow,edge[i].capacity - edge[i].flow));  
		            edge[i].flow += delta;  
		            edge[i ^ 1].flow -= delta;  
		            cur[u] = i; 
		            if(delta != 0) 
		            	return delta;  
		        }  
			}
		    return 0;  
		}  
	 public static boolean modlabel()  
		{  
			int dis = MAX;
			
		    for(int u = 0; u < V; u++)
		    	if(vis[u])
		    		for(int i = edgeHead[u]; i != -1; i = edge[i].next)
		    		{
		    			int v = edge[i].to;
						if(edge[i].capacity>edge[i].flow && !vis[v])
							dis = Math.min(dis, d[v]+edge[i].cost-d[u]);
					}
			if(dis == MAX)return false;
			for(int i = 0;i < V;i++)
				if(vis[i])
				{
					vis[i] = false;
					d[i] += dis;
	 
				}
			return true; 
		}  
	 public static void initGraphInfo(String[] graphContent){
			//变量初始化
			for(int i=0;i<graphContent.length;i++){
				String[] contentReadLine = graphContent[i].split(" ");
				if(i==0){
					numberOfPoint = Integer.parseInt(contentReadLine[0]);
					numberOfEdge = Integer.parseInt(contentReadLine[1]);
					numberOfGuest = Integer.parseInt(contentReadLine[2]);
					V = numberOfPoint+numberOfGuest+2;
					priceOfPoint = new int[numberOfPoint];
					s = V-1;
					t = V-2;
					idx = 0;
					edgeHead = new int[V];
					d = new int[V];
					cur = new int[V];
					vis = new boolean[V];
					pathArray = new ArrayList[V];
					for(int j=0;j<V;j++){
				    	edgeHead[j]=-1;
				    }
					for(int j=0;j<V;j++){
				    	pathList.add(new ArrayList<Integer>());
				    }
					continue;
				}
				if(contentReadLine.length==0) continue;
				if(i < 12 && contentReadLine.length==3){
					capOfServer[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[1]);
					priceOfServer[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[2]);
					numOfLevel += 1;
					continue;
				}
				//网络节点
				if(contentReadLine.length==2)
				{
					priceOfPoint[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[1]);
				}
				//网络链路
				if(contentReadLine.length==4){
		            int from = Integer.parseInt(contentReadLine[0]);
		            int to = Integer.parseInt(contentReadLine[1]);
		            int cap = Integer.parseInt(contentReadLine[2]);
		            int cost = Integer.parseInt(contentReadLine[3]);
		            pointConnectInformation[from].add(to);
		            pointConnectInformation[to].add(from);
					addedge(from,to,cap,cost,0);
		            addedge(to,from,cap,cost,0);
					continue;
				}
				//用户节点
				if(i > 12 && contentReadLine.length==3){
					int from = Integer.parseInt(contentReadLine[0]);
		            int to = Integer.parseInt(contentReadLine[1]);
		            int flow_need = Integer.parseInt(contentReadLine[2]);
		            addedge(to,from+numberOfPoint,flow_need,0,0);
		            addedge(numberOfPoint+from,V-2,MAX,0,0);
				}
			}
		}
			
		/**
		 * 
		 * @param from 起点
		 * @param to 终点
		 * @param capacity 流量
		 * @param cost 花费
		 * @param flow 流量
		 */
	public static void addedge(int from,int to,int capacity,int cost,int flow){
		edge[idx] = new Edges();
	    edge[idx].from=from;
	    edge[idx].to=to;
	    edge[idx].capacity=capacity;
	    edge[idx].flow=0;
	    edge[idx].cost=cost;
	    edge[idx].re=idx;
	    edge[idx].next = edgeHead[from];
	    edgeHead[from] = idx;//		记录from节点是编号为idx边的头结点
	    idx +=1;
	    //	反向边
	    edge[idx] = new Edges();
	    edge[idx].from=to;
	    edge[idx].to=from;
	    edge[idx].capacity=0;
	    edge[idx].flow=0;
	    edge[idx].cost=-cost;
	    edge[idx].re=idx-1;
	    edge[idx].next = edgeHead[to];
	    edgeHead[to]=idx;
	    idx+=1;
	    if(pathArray[from]==null)
	    	pathArray[from]=new ArrayList<Integer>();
	    pathArray[from].add(idx-2);
	    
	    if(pathArray[to]==null)
	    	pathArray[to]=new ArrayList<Integer>();
	    pathArray[to].add(idx-1);
	    
	    pathList.get(from).add(idx-2);
	    pathList.get(to).add(idx-1);
	}	
	/**
	 * 找到数组中第k小的元素的值
	 * @param a 待查找的数组
	 * @param k
	 * @return 第k小的元素值
	 */
	public static int getKthValue(int[] a ,int k) {
		if(k==0)
			k=1;
	    return getKthValue(a, 0, a.length-1, k);
	}
	public static int randompartition(int[] a, int p, int r) { 
		//随机选择数组中的某个数作为中枢
		int k= (int) (Math.random() * (r - p) + p); 
		swap(a, k, r);
		int x=a[r];
        int i = p-1;
        for(int j=p;j<r;j++){
        	if(a[j]<=x){
        		i++;
        		swap(a, i, j);
        	}
        }
        swap(a, i+1, r);  
        return i+1;  
    }  
	private static void swap(int[] a, int i, int j) {
		int temp=a[i];
		a[i]=a[j];
		a[j]=temp;	
	}
	public static int getKthValue(int[] a, int left, int right, int k) {  
        if (left == right) return a[left];  
        int result = randompartition(a, left, right);  
        int i = result - left + 1;// r是当前序列里面第i小的数字  
        if (i == k) {  
            return a[result];  
        } 
        else if (k < i) {  
            return getKthValue(a,left,result-1,k);  
        } 
        else return getKthValue(a,result+1,right,k - i);  
    }  
	
	/**
	 * 
	 * @param s 超级源点
	 * @param t 超级汇点
	 * @return 是否存在增广路径
	 */
	public static boolean spfaForPath(int s, int t){

		boolean[] vis = new boolean[V];
		int[] dis = new int[V];
		int[] flowMax = new int[V];
		int[] preNode = new int[V];
		Arrays.fill(dis, MAX);
		dis[s] = 0;
	    vis[s]=true;
	    flowMax[s]=MAX;
	    
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		queue.add(s);
		
	    int top,i;
	    Edges e;
	    while(!queue.isEmpty()){  
	    	top=queue.poll();  
	        vis[top]=false;  
	        for(i=edgeHead[top];i!=-1;i=edge[i].next)  
	        {  
	        	e = edge[i];
	            if(e.capacity>e.flow&&dis[e.to]>dis[top]+e.cost)  
	            {   
	                    dis[e.to]=dis[top]+e.cost;  
	                    preNode[e.to]=i;
	                    flowMax[e.to]=Math.min(flowMax[top], e.capacity-e.flow);
	                    if(false==vis[e.to]){
	                    	vis[e.to] = true;
	                    	if(!queue.isEmpty()&&dis[e.to]<dis[queue.peek()])
	                    		queue.addFirst(e.to);
	                    	else 
	                    		queue.add(e.to);// lll优化
	                        
	                    }
	            }  
	        }  
	    } 
	    if(dis[t]==MAX) {
	    	return false;
	    }
	    flow+=flowMax[t];
	    cost+=dis[t]*flowMax[t];
	    int u = t;
	    while(u!=s){
	    	edge[preNode[u]].flow+=flowMax[t];
	    	edge[preNode[u]^1].flow-=flowMax[t];
	    	u = edge[preNode[u]].from;
	    }
	    return true; 
	}
	/**
	 * 	
	 * @param serverSeries 服务器序列
	 * @return 最小花费最大流
	 */
	public static int[] spfaMCMF_forPath(HashMap<Integer,Integer> serverSeries)
	{
		resultList.clear();
		flow=0;
		cost=0;
		int cost_fixed1 = 0;
		int cost_fixed2 = 0;
		Iterator iterator = serverSeries.keySet().iterator();
		List<Integer> serverSeries_deployed = new ArrayList();
		while(iterator.hasNext())
		{
			int key = (int)iterator.next();
			int value = serverSeries.get(key);
			if(value >= 0)
			{
				serverSeries_deployed.add(key);
				addedge(V-1,key,capOfServer[value],0,0);
				cost_fixed1 += priceOfPoint[key];//	网络节点的部署成本
				cost_fixed2 += priceOfServer[value];//	服务器的硬件成本
				
			}
			
		} 
	    int[] result = new int[2];
        while(spfaForPath(V - 1,V - 2));  
        result[0] = flow;
    	result[1] = cost + cost_fixed1 + cost_fixed2;
    	
        flowUsed.clear();
        for(int i=0;i<edge.length;i++){
        	if(edge[i]==null) break;
        	flowUsed.add(edge[i].flow);
        	edge[i].flow = 0;
        }
        	
	   //更新
	    edgeHead[V-1]=-1;
	    for(int temp:serverSeries_deployed)
	    	edgeHead[temp] = edge[edgeHead[temp]].next;
	    for(int j=0;j<serverSeries_deployed.size();j++){
		    edge[idx-2] = null;
		    edge[idx-1] = null;
		    idx = idx-2;
	    }
	    pathList.get(V-1).clear();
	    for(int temp:serverSeries_deployed){
	    	int size = pathList.get(temp).size();
	    	pathList.get(temp).remove(size-1);
	    }   
	    pathArray[V-1]=null;
	    for(int temp:serverSeries_deployed){
	    	int size = pathArray[temp].size();
	    	pathArray[temp].remove(size-1);
	    }
	    return result;
	}
	/**
	 * 	
	 * @param serverSeries 服务器序列
	 * @return 最小花费最大流
	 */
	public static int[] spfaMCMF(HashMap<Integer,Integer> serverSeries)
	{
		flow=0;
		cost=0;
		int cost_fixed1 = 0;
		int cost_fixed2 = 0;
		/*serverSeries保存的是服务器部署在哪个网络节点的信息，大小等于网络节点数量，
		 * 需要判断哪个节点部署了服务器，部署的是什么档位的服务器，哪个节点没有部署服务器，*/
		Iterator iterator = serverSeries.keySet().iterator();
		List<Integer> serverSeries_deployed = new ArrayList();
		while(iterator.hasNext())
		{
			int key = (int)iterator.next();
			int value = serverSeries.get(key);
			if(value >= 0)
			{
				serverSeries_deployed.add(key);
				addedge(V-1,key,capOfServer[value],0,0);
				cost_fixed1 += priceOfPoint[key];//	网络节点的部署成本
				cost_fixed2 += priceOfServer[value];//	服务器的硬件成本
				
			}
			
		}
	    int[] result = new int[2];
	    for(int i = 0;i < V;i++)
	    	d[i] = 0;
		while(true)
		{
			for(int i = 0;i < V;i++)
				cur[i] = edgeHead[i];
			while(true)
			{
				for(int i = 0;i < V;i++)
					vis[i] = false;
				int tmp = aug(s,MAX);
				if(tmp == 0)break;
				flow += tmp;
				cost += tmp*d[s];
			}
			if(!modlabel())break;
		}
        //LogUtil.printLog("MCMF");
        result[0] = flow;
        //System.out.println("提供的流量"+flow);
//        System.out.println(cost);
//        System.out.println(cost_fixed1);
//        System.out.println(cost_fixed2);
    	result[1] = cost + cost_fixed1 + cost_fixed2;;
    	
       
        for(int i=0;i<edge.length;i++){
        	if(edge[i]==null) break;
        	edge[i].flow = 0;
        }
        edgeHead[V-1]=-1;
	    for(int temp:serverSeries_deployed)
	    	edgeHead[temp] = edge[edgeHead[temp]].next;
	    for(int j=0;j<serverSeries_deployed.size();j++){
		    edge[idx-2] = null;
		    edge[idx-1] = null;
		    idx = idx-2;
	    }	
	    pathList.get(V-1).clear();
	    for(int temp:serverSeries_deployed){
	    	int size = pathList.get(temp).size();
	    	pathList.get(temp).remove(size-1);
	    }   
	    pathArray[V-1]=null;
	    for(int temp:serverSeries_deployed){
	    	int size = pathArray[temp].size();
	    	pathArray[temp].remove(size-1);
	    }
	    return result;
	}
    /**
     * 
     * @param s 超级源点
     * @param t 超级汇点
     * @return 输出路径
     * 增加的功能：在输出路径之前先判断每个服务器实际提供的流量，决定是否可以降档
     */
	public static boolean outputPath(int s,int t)
    {	
    	boolean[] vis = new boolean[V];
		int[] dis = new int[V];
		int[] flowMax = new int[V];
		int[] preNode = new int[V];
		Arrays.fill(dis, MAX);
		dis[s] = 0;
	    vis[s]=true;
	    flowMax[s]=MAX;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(s);
        while(!queue.isEmpty())
        {
            int top=queue.poll();
            vis[top]=false;
            for(int i=0;i<pathList.get(top).size();i++)
            {   
            	if(pathList.get(top).get(i)%2!=0) continue;
                Edges e=edge[pathList.get(top).get(i)];             
                if(e.flow > 0 && dis[e.to]>dis[top]+e.flow)
                {
                    dis[e.to]=dis[top]+e.flow;
                    preNode[e.to]=pathList.get(top).get(i);
                    flowMax[e.to]=Math.min(flowMax[top],e.flow);
                    if(false==vis[e.to]){
                    	vis[e.to] = true;
                    	if(!queue.isEmpty()&&dis[e.to]<dis[queue.peek()])
                    		queue.addFirst(e.to);
                    	else 
                    		queue.add(e.to);// lll优化
                        
                    }
                }
            }           
        }
        if(dis[t]==MAX) return false;
        int flowTemp  =  flowMax[t];//该条路径经过的流量
        List<Integer> pathTemp = new ArrayList<>();
        int u=t;
        while(u!=s)
        {  	pathTemp.add(u);
        	edge[preNode[u]].flow-=flowMax[t];
        	edge[preNode[u]+1].flow += flowMax[t];
            u=edge[preNode[u]].from;
        }//倒序得到路径
        pathTemp.set(0, flowTemp);
        pathTemp.set(1, pathTemp.get(1)-numberOfPoint);
        Collections.reverse(pathTemp);
        String path = "";
        for(int i = 0; i < pathTemp.size(); i++){
        	path += pathTemp.get(i);
			if(i != pathTemp.size()-1)
				path += " ";
		}
        resultList.add(path);
        return true;
    }
	public static int[] spfaMCMF_addDecLevel(HashMap<Integer,Integer> serverSeries)
	{
		resultList.clear();
		flow=0;
		cost=0;
		int cost_fixed1 = 0;
		int cost_fixed2 = 0;
		/*serverSeries保存的是服务器部署在哪个网络节点的信息，大小等于网络节点数量，
		 * 需要判断哪个节点部署了服务器，部署的是什么档位的服务器，哪个节点没有部署服务器，*/
		Iterator iterator = serverSeries.keySet().iterator();
		List<Integer> serverSeries_deployed = new ArrayList();
		while(iterator.hasNext())
		{
			int key = (int)iterator.next();
			int value = serverSeries.get(key);
			if(value >= 0)
			{
				serverSeries_deployed.add(key);
				addedge(V-1,key,capOfServer[value],0,0);
				serverIdx.put(key, idx - 2);
				cost_fixed1 += priceOfPoint[key];//	网络节点的部署成本
				cost_fixed2 += priceOfServer[value];//	服务器的硬件成本
				
			}
			
		}
	    int[] result = new int[2];
	    for(int i = 0;i < V;i++)
	    	d[i] = 0;
		while(true)
		{
			for(int i = 0;i < V;i++)
				cur[i] = edgeHead[i];
			while(true)
			{
				for(int i = 0;i < V;i++)
					vis[i] = false;
				int tmp = aug(s,MAX);
				if(tmp == 0)break;
				flow += tmp;
				cost += tmp*d[s];
			}
			if(!modlabel())break;
		}
        result[0] = flow;
//        System.out.println(cost);
//        System.out.println(cost_fixed1);
//        System.out.println(cost_fixed2);
    	result[1] = cost + cost_fixed1 + cost_fixed2;
    	for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			int no = serverSeries_deployed.get(i);
			checkFlow.put(no, edge[serverIdx.get(no)].flow);
		}
    	flowUsed.clear();
        for(int i=0;i<edge.length;i++){
        	if(edge[i]==null) break;
        	flowUsed.add(edge[i].flow);
        	edge[i].flow = 0;
        }
        	
	   //更新
        edgeHead[V-1]=-1;
	    for(int temp:serverSeries_deployed)
	    	edgeHead[temp] = edge[edgeHead[temp]].next;
	    for(int j=0;j<serverSeries_deployed.size();j++){
		    edge[idx-2] = null;
		    edge[idx-1] = null;
		    idx = idx-2;
	    }	
	    pathList.get(V-1).clear();
	    for(int temp:serverSeries_deployed){
	    	int size = pathList.get(temp).size();
	    	pathList.get(temp).remove(size-1);
	    }   
	    pathArray[V-1]=null;
	    for(int temp:serverSeries_deployed){
	    	int size = pathArray[temp].size();
	    	pathArray[temp].remove(size-1);
	    }
////		判断是否可以降档
		HashMap<Integer,Integer> realLevel = new HashMap<>();
		HashMap<Integer,Integer> finalLevel = new HashMap<>();
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			realLevel.put(serverSeries_deployed.get(i), serverSeries.get(serverSeries_deployed.get(i)));
			finalLevel.put(serverSeries_deployed.get(i), serverSeries.get(serverSeries_deployed.get(i)));
		}
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			int flowTemp  = checkFlow.get(serverSeries_deployed.get(i));
			for(int j = 0; capOfServer[j] > 0; j++ )
				if(flowTemp <= capOfServer[j])
				{
					//add_level = j;
					finalLevel.put(serverSeries_deployed.get(i), j);
					break;
				}
			
		}
		//System.out.println("降档前的花费" + result[1]);
		for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			//System.out.println(serverSeries_deployed.get(i));
			int realServerLevel = realLevel.get(serverSeries_deployed.get(i));
			int finalServerLevel = finalLevel.get(serverSeries_deployed.get(i));
			result[1] -= (priceOfServer[realServerLevel] - priceOfServer[finalServerLevel]);
		}
//		System.out.println("流量" + result[0]);
//		System.out.println("降档后的花费" + result[1]);
	    return result;
	}
	/**
	 * 对highneedguest、highdegreevertex组合序列的度进行排序
	 * 返回索引值
	 */
	public static int[] sortByDegree()
	{
		int[] sortedByDegree = new int[lengthofseries];
		HashMap<Integer,Integer> orderOfNodes  = new HashMap<Integer, Integer>();
		
		List<Integer> allServerNodes = new ArrayList<Integer>();
		allServerNodes.addAll(highneedguest);
		allServerNodes.addAll(highvaluevertex);
		Integer[] tempServerNodes = new Integer[lengthofseries];
		allServerNodes.toArray(tempServerNodes);
		HashMap<Integer,Integer> degreeOfNodes = CountDegree();
		for(int i = 0; i < lengthofseries; i++)
		{
			orderOfNodes.put(allServerNodes.get(i), i);
			
		}
		
		for(int i = 0; i < lengthofseries; i++)
		{
			for(int j = i + 1; j < lengthofseries; j++)
			{
				
				if(degreeOfNodes.get(tempServerNodes[i]) > degreeOfNodes.get(tempServerNodes[j]))
				{
					int temp = tempServerNodes[i];
					tempServerNodes[i] = tempServerNodes[j];
					tempServerNodes[j] = temp;    			
				}
			}
		}
		for(int i = 0; i < lengthofseries; i++)
		{
			sortedByDegree[i] = orderOfNodes.get(tempServerNodes[i]);
		}
		return sortedByDegree;
	}
    /**
 /**
     * 对highneedguest、highdegreevertex组合序列的通量进行排序
     * 返回索引值
     */
    public static int[] sortByCap()
    {
    	int[] sortedByCap = new int[lengthofseries];
    	HashMap<Integer,Integer> orderOfNodes  = new HashMap<Integer, Integer>();
    	
    	List<Integer> allServerNodes = new ArrayList<Integer>();
    	allServerNodes.addAll(highneedguest);
    	allServerNodes.addAll(highvaluevertex);
    	Integer[] tempServerNodes = new Integer[lengthofseries];
    	allServerNodes.toArray(tempServerNodes);
    	HashMap<Integer,Integer> capOfNodes = CountCap();
    	for(int i = 0; i < lengthofseries; i++)
    	{
    		orderOfNodes.put(allServerNodes.get(i), i);
    		
    	}
    	
    	for(int i = 0; i < lengthofseries; i++)
    	{
    		for(int j = i + 1; j < lengthofseries; j++)
    		{
    			
    			if(capOfNodes.get(tempServerNodes[i]) > capOfNodes.get(tempServerNodes[j]))
    			{
    				int temp = tempServerNodes[i];
    				tempServerNodes[i] = tempServerNodes[j];
    				tempServerNodes[j] = temp;    			
    			}
    		}
    	}
    	for(int i = 0; i < lengthofseries; i++)
    	{
    		sortedByCap[i] = orderOfNodes.get(tempServerNodes[i]);
    	}
    	return sortedByCap;
    }
    /**
	  * 
	  * @return 每个节点的通量
	  */
	 public static HashMap<Integer, Integer> CountCap(){
	    	HashMap<Integer, Integer> capCount=new HashMap<>();
	    	for(int i=0;i<numberOfEdge;i++){	
	    		if(capCount.containsKey(edgeInformation[i][0]))
	    			capCount.put(edgeInformation[i][0],capCount.get(edgeInformation[i][0])+edgeInformation[i][2]);
	    		else capCount.put(edgeInformation[i][0], edgeInformation[i][2]);
	    		if(capCount.containsKey(edgeInformation[i][1]))
	    			capCount.put(edgeInformation[i][1],capCount.get(edgeInformation[i][1])+edgeInformation[i][2]);
	    		else capCount.put(edgeInformation[i][1], edgeInformation[i][2]);	
	    	}
	    	return capCount;
	    }
	 public static void replaceNext(float time,int[] series)
	 {
		//对部署服务器的相邻节点进行替换，聚类，观察花费是否会降低
			//每次替换一个节点
	
			int minCostOfReplace = fitness(series);
			List<Integer> serverInformation_deployed = new ArrayList();
			HashMap<Integer,Integer> serverInfo = new HashMap();
			int level=0;
			for(int i = 0; i < series.length; i++)
			{
				
				if(series[i] >= 0)
				{
					int tempKey;
					if(i < highneedguest.size())
						tempKey = highneedguest.get(i);
					else 
						tempKey = highvaluevertex.get(i - highneedguest.size());
//					tempKey=allServer[i];
					serverInformation_deployed.add(tempKey);
					serverInfo.put(tempKey, series[i]);
				}		
			}
//			System.out.println("前》》》》》》》》》" + highneedguest);
//			System.out.println("前》》》》》》》》》" + highvaluevertex);
			HashMap<Integer,Integer> minCostServer = new HashMap();
			
			
//			System.out.println("服务器序列："+serverInformation_deployed);
//			System.out.println("服务器用户节点序列："+serverInfo);
			long startTime=System.currentTimeMillis();
			long endTime=System.currentTimeMillis();
			float excTime=(float)(endTime-startTime)/1000;
			for(int i = 0; i < serverInformation_deployed.size(); i++)
			{
				
				int previousServer = serverInformation_deployed.get(i);
				int finalReplaceServer = serverInformation_deployed.get(i);
				int idx1 = -2;
				int idx2 = -2;
				if(highneedguest.contains(previousServer))
					 idx1 = highneedguest.indexOf(previousServer);
				else{
					idx2 = highvaluevertex.indexOf(previousServer);
					//continue;
				}
//				System.out.println(previousServer);
//				System.out.println(pointConnectInformation[previousServer]);
				//找到和previousServer相邻的网络节点
				for(int j = 0; j < pointConnectInformation[previousServer].size(); j++)
				{
					int replaceServer = pointConnectInformation[previousServer].get(j);
					//System.out.println("replaceServer》》》》》》》》"+replaceServer);
					
					//System.out.println("前》》》》》》》》"+replaceServer);
					HashMap<Integer,Integer> tempServerInfo = new HashMap();
					Iterator iter1 = serverInfo.keySet().iterator();
					while(iter1.hasNext())
					{
						int key = (int)iter1.next();
						int value = serverInfo.get(key);
						tempServerInfo.put(key, value);
					}
					
					tempServerInfo.remove(previousServer);
					if(!tempServerInfo.containsKey(replaceServer))
						tempServerInfo.put(replaceServer, serverInfo.get(previousServer));
					//System.out.println("替换后的服务器序列："+tempServerInfo);
					
					int[] tempResult = spfaMCMF_addDecLevel(tempServerInfo);
					//System.out.println("flow"+tempResult[0]);
					//System.out.println("cost"+tempResult[1]);
					if(tempResult[1] < minCostOfReplace && tempResult[0] == flowvalue)
					{
						
						minCostOfReplace = tempResult[1];
						finalReplaceServer = replaceServer;
						//level=levelofpoint.get(finalReplaceServer);
//						System.out.println(tempResult[1]);
//						System.out.println(">>>>>>>>>>>>>>更优的序列" + tempServerInfo);
						Iterator iter2 = tempServerInfo.keySet().iterator();
						while(iter2.hasNext())
						{
							int key = (int)iter2.next();
							int value = tempServerInfo.get(key);
							minCostServer.put(key, value);
						}
					}
				}
				int tempLevel = serverInfo.get(previousServer);
				serverInfo.remove(previousServer);
				serverInfo.put(finalReplaceServer, tempLevel);
//				
				if(idx1 != -2)
				{
					if(highneedguest.contains(finalReplaceServer))
					{
						series[idx1] = -1;
						series[highneedguest.indexOf(finalReplaceServer)] = tempLevel;
					}
					else if(highvaluevertex.contains(finalReplaceServer))
					{
						series[idx1] = -1;
						series[highvaluevertex.indexOf(finalReplaceServer) + highneedguest.size()] = tempLevel;
					}
					else
					highneedguest.set(idx1, finalReplaceServer);
					//highneedguest.set(idx1_, previousServer);
				}
				else
				{
					if(highvaluevertex.contains(finalReplaceServer))
					{
						series[idx2 + highneedguest.size()] = -1;
						series[highvaluevertex.indexOf(finalReplaceServer) + highneedguest.size()] = tempLevel;
					}
					else if(highneedguest.contains(finalReplaceServer)){
						series[idx2 + highneedguest.size()] = -1;
						series[highneedguest.indexOf(finalReplaceServer)] = tempLevel;
					}
					else
						highvaluevertex.set(idx2, finalReplaceServer);
					
				}
				endTime=System.currentTimeMillis();
				excTime=(float)(endTime-startTime)/1000;
				double sumtime=(numberOfPoint>800)?85:86.5;
				if(excTime+time>=sumtime)
					break;
			}

	 }
}
