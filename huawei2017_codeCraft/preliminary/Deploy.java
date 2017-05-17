package com.cacheserverdeploy.deploy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;


class Edges{
	//起点、终点、容量、流量、下一条、费用
	int from, to, capacity, flow, next, cost;
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
	static int numberOfPoint = 0;  //网络节点的个数
	static int numberOfEdge = 0;    //网络边的条数
	static int numberOfGuest = 0;     //网络用户节点的个数
	static int priceOfServer = 0;  //网络中部署一台服务器的价格
	static List<String> resultList = new ArrayList<>();
	public static int V;// 	总的节点个数
	private static final int MAXEDGES = 30000;  //设定最大的边的数目为30000
	private static final int MAXEDGE = 3000;	//输出路径的总条数
	private static final int MAX = 1<<30; 
	static Edges[] edge = new Edges[MAXEDGES];		//保存边信息
	static int[] fromNode;
	static int idx;//	边的编号
	static int flow,cost;	//流量，花费
	static List<List<Integer>> pathList = new ArrayList<List<Integer>>();
	static List<Integer>[] pathArray;
	static List<Integer> flowUsed = new ArrayList<>();
	static HashMap<String, int[]> map=new HashMap<String, int[]>();
	static int[] serverseries;
	static int resultindex=0;
	
	static int mincost=MAX;
	static String netInformation=new String(); 
	static int[] netInformationInteger=new int[3];
	static int flowvalue=0;
	//将txt文件中的边信息储存在edgeInformation矩阵中
	static int [][] edgeInformation;
	//将txt文件中的用户信息储存在guestInformation矩阵中
	static int [][] guestInformation;
	static int group_count=0;//种群数量
	static int numberofhighneed=0;
	static int numberofhighdegree=0;
	static int [] GuestNeed;//保存消费节点i的需求
	static int [] orderOfGuestNeed;  //保存消费节点的排序
	static int [] VertexofGuest;//保存与消费节点相连的网络节点
	static int[] lowneedguest;//保存需求低低的用户节点
	static int[] bestseries;//保存初始最优序列
	static ArrayList<Integer> highneedguest=new ArrayList<>();//保存需求低低的用户节点
	static ArrayList<Integer> highdegreevertex=new ArrayList<>();//保存度很大的网络节点 
	static int lengthofseries=0;
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
    	priceOfServer=Integer.parseInt(graphContent[2]);
    	edgeInformation=new int[numberOfEdge][4];
    	guestInformation=new int[numberOfGuest][3];
    	for(int i=0;i<numberOfEdge;i++)
			edgeInformation[i]=stringToInteger(graphContent[i+4]);
		for(int i=0;i<numberOfGuest;i++)
			guestInformation[i]=stringToInteger(graphContent[i+numberOfEdge+5]);
		//group_count=(numberOfPoint>500)? 5:(numberOfPoint>200)? 70:100;
		group_count=(numberOfPoint>500)? 8:(numberOfPoint>200)?65:75;
		lengthofseries=numberOfGuest;//序列长度
		serverseries=new int[lengthofseries];
		numberofhighneed=(int) (numberOfGuest);//需求较高的消费节点个数
		GuestNeed=new int[numberOfGuest];
		int[] GuestNeedTemp=new int[numberOfGuest];
		orderOfGuestNeed=new int[numberOfGuest];
		VertexofGuest=new int[numberOfGuest];
		bestseries=new int[lengthofseries];
		for(int i=0;i<lengthofseries;i++)
    		bestseries[i]=1;
		for(int i=0;i<numberOfGuest;i++){
			VertexofGuest[i]=guestInformation[i][1];
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
		/******************************初始化完毕***********************************/
		//用户的流量总需求    
		for(int i=0;i<numberOfGuest;i++)
			flowvalue=flowvalue+guestInformation[i][2];
		//利用遗传算法求解
		List<Integer>[] lineOutput = new List[MAXEDGE - numberOfPoint + 2];
		String[] output=new String[MAXEDGE - numberOfPoint + 2];
		int[] serverInformation=new int[lengthofseries];//最优服务器布置情况          
		List<Integer>[] graph = new List[numberOfPoint+2]; // 顶点数组
		List<Integer>[] capacity = new List[numberOfPoint+2]; // 容量数组
		List<Integer>[] cost= new List[numberOfPoint+2]; // 价格数组
		long startTime=System.currentTimeMillis();
		long endTime=System.currentTimeMillis();
		float excTime=(float)(endTime-startTime)/1000;
		/**
		 * 下面用遗传算法求解
		 */
		findbestvertex();// 得到消费较高的与消费节点直连的网络节点和度较高的中心网络节点
		if(numberOfPoint>200)
			findbestlocation();//对于中级和高级案例先找到一个优秀个体
	    ArrayList<int[]> population=new ArrayList<>();//该线性表保存种群的信息，每个个体是一个表示服务器序列的数组
	    population=gen_population(lengthofseries, group_count);
	    int count=0;
	    int time=0;
	    time=(numberOfPoint<200)?25:87;
	    while(excTime<87){
	    	count++;
	    	population=evolve(population, 0.2, 0.05, 0.8);
	    	endTime=System.currentTimeMillis();
			excTime=(float)(endTime-startTime)/1000;
			System.out.println("第"+count+"轮进化结果："+mincost);
	    }
	  //System.out.println("迭代次数："+count);
	    for(int i=0;i<lengthofseries;i++){
	    	if(i<highneedguest.size())
	    		serverInformation[i]=serverseries[i]*highneedguest.get(i);//服务器所在的位置信息
	    	else serverInformation[i]=serverseries[i]*highdegreevertex.get(i-highneedguest.size());//服务器所在的位置信息
	    	if(serverInformation[i]==0){
	    		if(serverseries[i]<0)
	    			serverInformation[i]=-1;
	    	}
	    }
	    List<Integer> serverPosition = new ArrayList();
	    for(int i = 0; i < numberOfGuest; i++)
	    {
	    	if(serverInformation[i] >= 0)
	    		serverPosition.add(serverInformation[i]);
	    }
		int[] result111=new int[2];
		
		result111 = spfaMCMF(serverPosition);
		for(int temp:serverPosition)
	    	addedge(V - 1,temp,MAX,0,0);
		int count111=0;
		for(int i = 0; i < edge.length; i++)
		{ 
			if(edge[i] == null)
				break;
			 edge[i].flow = flowUsed.get(i) ;
		}
		while(outputPath(V-1,V-2))
			{
			count111++;
			}
		for(int i = 0; i < resultList.size(); i++)
       {
    	   output[i + 2] = resultList.get(i);
       }
		output[0]=Integer.toString(resultList.size());
		output[1]="";
		return output;
    }	
    /**
     * 得到消费较高的与消费节点直连的网络节点和度较高的中心网络节点
     */
    public static void findbestvertex(){
    	int[] temp=new int[numberOfGuest];
		System.arraycopy(GuestNeed, 0, temp, 0, numberOfGuest);
		int kthMaxNeed=getKthValue(temp, numberOfGuest-numberofhighneed);//第k大的需求
		if(numberOfPoint<200){
			for(int i=0;i<numberOfGuest;i++){//将需求小的消费节点放入此表
				if(GuestNeed[i]>kthMaxNeed)
					highneedguest.add(VertexofGuest[i]);
			}
		}
		else{
		for(int i=0;i<numberOfGuest;i++){//将需求小的消费节点放入此表
			if(GuestNeed[orderOfGuestNeed[i]]>kthMaxNeed)
				highneedguest.add(VertexofGuest[orderOfGuestNeed[i]]);
		}
		}
		//System.out.println("消费节点上的服务器个数："+highneedguest.size());
		numberofhighdegree=lengthofseries-highneedguest.size();//中心节点个数
		HashMap<Integer, Integer> degree=new HashMap<>();
		degree=CountCap();
		int[] current=new int[numberOfPoint];
		Iterator iter = degree.entrySet().iterator(); 
		for(int i=0;iter.hasNext();i++) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		   // System.out.println("节点"+entry.getKey()+"的度为："+(int) entry.getValue()); 
		    current[i]=(int) entry.getValue();
		} 
		int kthmaxdegree=getKthValue(current, numberOfPoint-numberofhighdegree);
		for(int key:degree.keySet()){
			if(degree.get(key)>=kthmaxdegree&&highdegreevertex.size()!=numberofhighdegree)
				highdegreevertex.add(key);
			if(highdegreevertex.size()==numberofhighdegree)
				break;
		}
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
    		tempseries[i]=-1;
    		if(fitness(tempseries)<fitness(bestseries))
    			bestseries[i]=-1;
    	}
		for(int i=0;i<lengthofseries;i++){
    		System.arraycopy(bestseries, 0, tempseries, 0, lengthofseries);
    		tempseries[i]=1;
    		if(fitness(tempseries)<fitness(bestseries))
    			bestseries[i]=1;
		}
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
    			series[i]=1;
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
    		a[i]=1;
    	if(numberOfPoint<200)
    	population.add(a);
    	else{
    	population.add(bestseries);
    	population.add(bestseries);
    	}
    	while(population.size()<group_count){
	    	int[] serverseries=new int[length];
	    	serverseries=gen_chromosome(length);
	    	population.add(serverseries);
	    }
    	return population;
    }
    @SuppressWarnings("unchecked")

	public static int fitness(int[] serverseries){ 
    	String string=Arrays.toString(serverseries);
		int[] serverlocation=new int[lengthofseries];
		int numberOfserver=0;
    	for(int i=0;i<lengthofseries;i++){
    		if(i<highneedguest.size())
    		serverlocation[i]=serverseries[i]*highneedguest.get(i);//服务器所在的位置信息
    		else serverlocation[i]=serverseries[i]*highdegreevertex.get(i-highneedguest.size());//服务器所在的位置信息
    		if(serverlocation[i]==0){
	    		if(serverseries[i]<0)
	    			serverlocation[i]=-1;
	    	}
			if(serverseries[i]>=0)
				numberOfserver++;    //部署的服务器的个数
    	}
    	if(map.containsKey(string))
    		return (map.get(string)[0]==flowvalue)?(map.get(string)[1]+numberOfserver*priceOfServer):MAX;
    	List<Integer>[] graph = new List[numberOfPoint+2]; // 顶点数组
		List<Integer>[] capacity = new List[numberOfPoint+2]; // 容量数组
		List<Integer>[] cost= new List[numberOfPoint+2]; // 价格数组
		graph=ConvertToGraph(serverlocation);//网络中的连接情况
		capacity=ConvertToCapacity(serverlocation);//网络中的通量限制
		cost=ConvertToCost(serverlocation);//网络中的通量限制
		int[] result = new int[2];
		List<Integer> serverPosition = new ArrayList();
		
		 for(int i = 0; i < numberOfGuest; i++)
		    {
		    	if(serverlocation[i] >= 0)
		    		serverPosition.add(serverlocation[i]);
		    	//System.out.print(serverlocation[i]+" ");
		    }
		//result=spfaMCMF(serverPosition);
		result=maxFlow(graph, capacity, cost,0,numberOfPoint+1);//输出最大流，最小花费
		map.put(string, result);
		if(result[0]!=flowvalue)
			return MAX;
		return numberOfserver*priceOfServer+result[1];
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
    		int crosspos=random.nextInt(lengthofseries);//选择交叉位置
    		int[] crossresult=new int[lengthofseries];
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
        		  int mutationpos=random.nextInt(lengthofseries);//随机选取个体中的某个基因
        		  a[mutationpos]=(a[mutationpos]==1)? -1:1;
        	  }
          }
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
	/* * 判断能否找到一条最短路径增广矩阵
	 * @param rGraph 残留网络
	 * @param s 源点
	 * @param t 终点
	 * @param path 路径
	 * @return 最短路径增广矩阵的单位花费
	 **/
	public static int hasPath(List<Integer>[] mvexs, List<Integer>[] rGraph, int s, int t, int[] path, List<Integer>[] cost) {
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(s);	//	queue是先入先出，新元素插入到队尾，从队列头部访问数据
		int[] a = new int[numberOfPoint+2];
		boolean[] vis = new boolean[numberOfPoint+2];
		Arrays.fill(a, MAX);
		a[s] = 0;
		vis[s] = true;
		//BFS算法
		while(queue.size() > 0){
			int top = queue.poll();
			vis[top] = false;
			for(int i = 0; i < mvexs[top].size(); i++){
				int temp = mvexs[top].get(i);	//得到当前节点的节点号
				if( a[temp] > a[top] + cost[top].get(i) && rGraph[top].get(i) > 0){
					a[temp] = a[top] + cost[top].get(i);
					if(!vis[temp])
					{
						vis[temp] = true;
						queue.add(temp);
					}
					path[temp] = top;		
				}
			}
		}
		return a[t];
	}
	/**
	 * 
	 * @param rGraph 残存矩阵
	 * @param graph 有向图的矩阵表示
	 * @param s 源点
	 * @param t 终点
	 * @return 最大流量
	 */
	@SuppressWarnings("unchecked")
	private static int[] maxFlow(List<Integer>[] vexs, List<Integer>[] graph,List<Integer>[] cost,int s, int t) {
		@SuppressWarnings("rawtypes")
		List[] rGraph = new List[numberOfPoint+2];
		for(int i=0; i<numberOfPoint+2; i++)
		{	
			rGraph[i] = new ArrayList<Integer>();
			 rGraph[i] = graph[i];
		}
		int maxFlow = 0;
		int minCost = 0;
		int[] result = new int[2];
		int path[] = new int[numberOfPoint+2];
		int perCost = 0; 
		while(!(perCost == MAX)){
			perCost = hasPath(vexs, rGraph, s, t, path, cost);
			int min_flow = Integer.MAX_VALUE;
			//更新路径中的每条边,找到最小的流量
			for(int v=t; v != s; v=path[v]){
				int u = path[v];
				int idex = vexs[u].indexOf(v);
				min_flow = Math.min(min_flow, (Integer)rGraph[u].get(idex));
			}//	找到增广路径中，流量最小的边，作为允许增加的最大流量
			if(min_flow==0)
				break;
			//更新路径中的每条边
			for(int v=t; v != s; v=path[v]){
				int u = path[v];
				int idex_v = vexs[u].indexOf(v);
				int idex_u = vexs[v].indexOf(u);
				int pos = (Integer)rGraph[u].get(idex_v);
				int neg = (Integer)rGraph[v].get(idex_u);
				pos -= min_flow;
				neg += min_flow;
				rGraph[u].set(idex_v, pos);
				rGraph[v].set(idex_u, neg);
			}
			maxFlow += min_flow;
			minCost += min_flow * perCost;
		}
		result[0] = maxFlow;//	返回最大流
		result[1] = minCost;//	返回最大花费
		return result;
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
		public static void initGraphInfo(String[] graphContent){
			//变量初始化
			for(int i=0;i<graphContent.length;i++){
				
				String[] contentReadLine = graphContent[i].split(" ");
				
				if(i==0){
					numberOfPoint = Integer.parseInt(contentReadLine[0]);
					numberOfEdge = Integer.parseInt(contentReadLine[1]);
					numberOfGuest = Integer.parseInt(contentReadLine[2]);
					V = numberOfPoint+numberOfGuest+2;
					idx = 0;
					fromNode = new int[V];
					pathArray = new ArrayList[V];
					for(int j=0;j<V;j++){
				    	fromNode[j]=-1;
				    }
				    for(int j=0;j<V;j++){
				    	pathList.add(new ArrayList<Integer>());
				    }
					continue;
				}
				if(contentReadLine.length==0) continue;
				if(i==2){
					priceOfServer = Integer.parseInt(contentReadLine[0]);
					continue;
				}
				//网络节点
				if(contentReadLine.length==4){
		            int from = Integer.parseInt(contentReadLine[0]);
		            int to = Integer.parseInt(contentReadLine[1]);
		            int cap = Integer.parseInt(contentReadLine[2]);
		            int cost = Integer.parseInt(contentReadLine[3]);
					addedge(from,to,cap,cost,0);
		            addedge(to,from,cap,cost,0);
					continue;
				}
				//用户节点
				if(contentReadLine.length==3){
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
		    edge[idx].next = fromNode[from];
		    fromNode[from] = idx;
		    idx +=1;
		    //	反向边
		    edge[idx] = new Edges();
		    edge[idx].from=to;
		    edge[idx].to=from;
		    edge[idx].capacity=0;
		    edge[idx].flow=0;
		    edge[idx].cost=-cost;
		    edge[idx].next = fromNode[to];
		    fromNode[to]=idx;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List[] ConvertToGraph(int[] serverInformation){
		List[] vexs  = new List[numberOfPoint+2]; // 顶点数组
		for(int i = 0; i < numberOfPoint+2; i++)
		{
			vexs[i] = new ArrayList<Integer>();
		}
		for(int i = 0; i < numberOfEdge; i++)
		{
			vexs[edgeInformation[i][0] + 1].add(edgeInformation[i][1] + 1);
			vexs[edgeInformation[i][1] + 1].add(edgeInformation[i][0] + 1);
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(serverInformation[i]>=0){
				vexs[0].add(serverInformation[i] + 1);
				vexs[serverInformation[i] + 1].add(0);
			}
		}
		for(int i = 0; i < numberOfGuest; i++){
			vexs[guestInformation[i][1] + 1].add(numberOfPoint+1);
			vexs[numberOfPoint+1].add(guestInformation[i][1] + 1);
		}
		return vexs;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List[] ConvertToCapacity(int[] serverInformation){
		List[] graph  = new List[numberOfPoint+2]; // 顶点数组
		for(int i = 0; i < numberOfPoint+2; i++)
		{
			graph[i] = new ArrayList<Integer>();
		}
		for(int i = 0; i < numberOfEdge; i++)
		{
			graph[edgeInformation[i][0] + 1].add(edgeInformation[i][2]);
			graph[edgeInformation[i][1] + 1].add(edgeInformation[i][2]);
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(serverInformation[i]>=0){
				graph[0].add(MAX);
				graph[serverInformation[i] + 1].add(0);
			}
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			graph[guestInformation[i][1] + 1].add(guestInformation[i][2]);
			graph[numberOfPoint+1].add(0);
		}
		return graph;
	}	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List[] ConvertToCost(int[] serverInformation){
		List[] cost  = new List[numberOfPoint+2]; // 顶点数组
		for(int i = 0; i < numberOfPoint+2; i++)
		{
			cost[i] = new ArrayList<Integer>();
		}
		for(int i = 0; i < numberOfEdge; i++)
		{
			cost[edgeInformation[i][0] + 1].add(edgeInformation[i][3]);
			cost[edgeInformation[i][1] + 1].add(edgeInformation[i][3]);
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(serverInformation[i]>=0){
				cost[0].add(0);
				cost[serverInformation[i] + 1].add(MAX);
			}
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			cost[guestInformation[i][1] + 1].add(0);
			cost[numberOfPoint+1].add(MAX);
		}
		return cost;
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
	        for(i=fromNode[top];i!=-1;i=edge[i].next)  
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
	public static int[] spfaMCMF(List<Integer> serverSeries)
	{
		resultList.clear();
		flow=0;
		cost=0;
		int serverNum = serverSeries.size();
	    for(int temp:serverSeries)
	    	addedge(V-1,temp,MAX,0,0);   
	    int[] result = new int[2];
        while(spfaForPath(V - 1,V - 2));  
        result[0] = flow;
    	result[1] = cost + serverNum * priceOfServer;
    	
        flowUsed.clear();
        for(int i=0;i<edge.length;i++){
        	if(edge[i]==null) break;
        	flowUsed.add(edge[i].flow);
        	edge[i].flow = 0;
        }
        	
	   //更新
	    fromNode[V-1]=-1;
	    for(int temp:serverSeries)
	    	fromNode[temp] = edge[fromNode[temp]].next;
	    for(int j=0;j<serverSeries.size();j++){
		    edge[idx-2] = null;
		    edge[idx-1] = null;
		    idx = idx-2;
	    }
	    pathList.get(V-1).clear();
	    for(int temp:serverSeries){
	    	int size = pathList.get(temp).size();
	    	pathList.get(temp).remove(size-1);
	    }   
	    pathArray[V-1]=null;
	    for(int temp:serverSeries){
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
}
