package finalTest28;

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
	static int myID;
	static int[][] netCost;
	static int leftMoney;
	static int numberOfPoint=0;  //网络节点的个数
	static int numberOfEdge=0;    //网络边的条数
	static int numberOfGuest=0;     //网络用户节点的个数
	public static int V;// 	总的节点个数
	static int tempNumNeeded;
	static int moneyPerTenSec;
	static int allFlow;
	static int firstServerNum;
	static int[] selectedID;
	private static final int MAXEDGE=5000000;  //设定最大的边的数目为30000
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
	static int[] priceOfServer;  //网络中部署不同档次服务器的价格
	static int[] capOfServer;  //网络中部署不同档次服务器的容量
	static int[] priceOfPoint;
	
	static HashMap<Integer,Integer> checkFlow = new HashMap<>();
	static HashMap<Integer,Integer> checkPointFlow = new HashMap<>();
	static HashMap<Integer,Integer> serverIdx = new HashMap<>();
	static HashMap<Integer, Integer> lastFlowOfGuest=new HashMap<>();
	static int numOfLevel = 0;
	static HashMap<String, int[]> map=new HashMap<String, int[]>();
	static HashMap<Integer,Integer> GuestOfVertex=new HashMap();
	static int[] serverseries;
	static int mincost=MAX;
	static String netInformation=new String(); 
	static int[] netInformationInteger=new int[3];
	static List<Integer>[] pointConnectInformation;
	static int flowvalue=0;
	static HashMap<Integer, Integer> capOfPoint=new HashMap<>();
	static  Integer[] allServer;
	//将txt文件中的边信息储存在edgeInformation矩阵中
	static int [][] edgeInformation;
	//将txt文件中的用户信息储存在guestInformation矩阵中
	static int [][] guestInformation;
	static int group_count=0;//种群数量
	static int [] GuestNeed;//保存消费节点i的需求

	static int [] VertexofGuest;//保存与消费节点相连的网络节点

	static int[] bestseries;//保存初始最优序列
	static int[] sortDeployCost;
	static int[] sortDeployCap;
	static int[] sortDeployDegree;
	static int[] allGuestPoint;
	static int moneyForFlow;
	
	static int lengthofseries=0;
	static HashMap<Integer, Integer> levelofpoint=new HashMap<>();
	static HashMap<Integer, Double> pprofpoint=new HashMap<>();
	static HashMap<Integer, Integer> guestPointIdx = new HashMap<Integer, Integer>();
	static ArrayList<Integer> posofdeployed=new ArrayList<>();
	static int count=0;
	
	static ArrayList<Integer>[] point2GuestPath;//保存每个网络节点到消费节点的任意一条路径
	static ArrayList<Integer> netPoints=new ArrayList<>();//按照性价比从大到小排序的网络节点
	static ArrayList<Integer> guestPoints=new ArrayList<>();//按照需求从小到大排序的消费节点
	
	static ArrayList<Integer> considervertex=new ArrayList<>();//每一轮考虑的消费节点编号
	static ArrayList<Integer> lastdeplyednonsus;//上一轮准备抢占但是未抢占的节点
	static ArrayList<Integer> kind1=new ArrayList<>();//保存第一类消费节点，即我们的消费节点
	static ArrayList<Integer> kind23=new ArrayList<>();//保存对手抢占的节点
	
	static ArrayList<Integer> kind4=new ArrayList<>();//保存第四类消费节点，剩余的节点
	static ArrayList<Integer> allFailurePoint=new ArrayList<>();//保存上一轮所有未占领的节点，且按流量从小到大排好序
	static HashMap<Integer, Integer> flowOfAllFailurePoint = new HashMap<>();
	static int[] res;
	
	static HashMap<Integer,Integer> flowoftodeploye=new HashMap<>();
	static HashMap<Integer,Integer> flowoffirstdeploye=new HashMap<>();
	static ArrayList<Integer> todeployeServer=new ArrayList<>();
	static HashMap<Integer, Integer> countofaddserver = new HashMap<Integer, Integer>();
	static ArrayList<Integer> saveserver=new ArrayList<>();
	static ArrayList<Integer> canserverpos=new ArrayList<>();//候选服务器位置
    @SuppressWarnings("unchecked")
	public static String[] deployServer(String[] graphContent)
    {
        /**do your work here**/
    	/*************************下面是静态变量的初始化*****************************/
    	count++;
    	ArrayList<Integer> tempdeployeserver=new ArrayList<>();
    	//对所有变量的初始化
    	if(count==1){
    		allFlow = 0;
    	int tempNum = 0;
    	int k = 0;
    	int m = 0;
    	String[] lastReadLine = graphContent[graphContent.length - 1].split(" ");
		myID = Integer.parseInt(lastReadLine[0]);
		leftMoney = Integer.parseInt(lastReadLine[1]);
		System.out.println("round 1 start！");
		System.out.println("*************************************");
		//for(int i=0;i<graphContent.length;i++)
			//System.out.println(graphContent[i]);
		for(int i=0;i<graphContent.length;i++)
		{
			//System.out.println(graphContent[i]);
			String[] contentReadLine = graphContent[i].split(" ");
			if(i==0){
				numberOfPoint = Integer.parseInt(contentReadLine[0]);
				numberOfEdge = Integer.parseInt(contentReadLine[1]);
				numberOfGuest = Integer.parseInt(contentReadLine[2]);
				V = numberOfPoint+numberOfGuest+2;
				selectedID = new int[numberOfGuest];
				priceOfPoint = new int[numberOfPoint];
				edgeInformation=new int[numberOfEdge][4];
		    	guestInformation=new int[numberOfGuest][4];
		    	pointConnectInformation = new List[numberOfPoint];
		    	netCost = new int[numberOfPoint][numberOfPoint];
		    	allGuestPoint = new int[numberOfGuest];
		    	point2GuestPath = new ArrayList[numberOfPoint];
		    	
		    	kind1.clear();
		    	kind23.clear();
		    	
		    	kind4.clear();
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
		    	for(int j = 0; j < numberOfPoint; j++)
		    		pointConnectInformation[j] = new ArrayList();
				tempNumNeeded++;
				continue;
			}
			if(graphContent[i].equals("")) 
			{
				//System.out.println(">>>>>>>");
				tempNumNeeded++;
				continue;
			}
			if(contentReadLine.length==1)
			{
				//System.out.println(i);
				tempNumNeeded++;
				moneyPerTenSec = Integer.parseInt(contentReadLine[0]);
				continue;
			}
			if(contentReadLine.length==3){
				numOfLevel += 1;
				tempNumNeeded++;
				continue;
			}
			//网络节点的部署成本
			if(contentReadLine.length==2 && i < (numberOfPoint+numberOfGuest) )
			{
				priceOfPoint[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[1]);
				tempNumNeeded++;
				continue;
			}
			
			//网络链路
			if(contentReadLine.length==4 && i < numberOfPoint + numberOfEdge + numOfLevel + 6){
	            int from = Integer.parseInt(contentReadLine[0]);
	            int to = Integer.parseInt(contentReadLine[1]);
	            int cap = Integer.parseInt(contentReadLine[2]);
	            int cost = Integer.parseInt(contentReadLine[3]);
	            netCost[Integer.parseInt(contentReadLine[0])][Integer.parseInt(contentReadLine[1])] = Integer.parseInt(contentReadLine[3]);
    			netCost[Integer.parseInt(contentReadLine[1])][Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[3]);
	            pointConnectInformation[from].add(to);
	            pointConnectInformation[to].add(from);
	            addedge(from,to,cap,cost,0);
	            addedge(to,from,cap,cost,0);
	            for(int j = 0; j < 4; j++)
    				edgeInformation[k][j] = Integer.parseInt(contentReadLine[j]);
				k = k + 1;
				continue;
			}
			//用户节点，增加了一个字段，上一轮选择的队伍ID
			if(i >= numberOfPoint + numberOfEdge + numOfLevel + 6&& contentReadLine.length==4){
				int from = Integer.parseInt(contentReadLine[0]);
	            int to = Integer.parseInt(contentReadLine[1]);
				int flow_need = Integer.parseInt(contentReadLine[2]);
				int selectedID  = Integer.parseInt(contentReadLine[3]);
				allGuestPoint[m] = to;
	            addedge(to,from+numberOfPoint,flow_need,0,0);
	            guestPointIdx.put(from, idx - 2);
	            addedge(numberOfPoint+from,V-2,MAX,0,0);
				for(int j = 0; j < 4; j++)
    				guestInformation[m][j] = Integer.parseInt(contentReadLine[j]);
    			m = m + 1;
    			continue;
			}
		}
		capOfServer = new int[numOfLevel];
		priceOfServer = new int[numOfLevel];
		for(int i=0;i< 10 + numOfLevel; i++)
		{
			String[] contentReadLine = graphContent[i].split(" ");
			if(contentReadLine.length==3){
				capOfServer[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[1]);
				priceOfServer[Integer.parseInt(contentReadLine[0])] = Integer.parseInt(contentReadLine[2]);
				continue;
			}
				
		}
		lengthofseries= (int) (1.1*numberOfGuest);//序列长度
		serverseries=new int[lengthofseries];
		GuestNeed=new int[numberOfGuest];
		int[] GuestNeedTemp=new int[numberOfGuest];
		VertexofGuest=new int[numberOfGuest];
		
		bestseries=new int[lengthofseries];
		sortDeployCost=new int[lengthofseries];
		sortDeployCap=new int[lengthofseries];
		sortDeployDegree=new int[lengthofseries];
		for(int i=0;i<numberOfGuest;i++){
			VertexofGuest[i]=guestInformation[i][1];
			GuestOfVertex.put(guestInformation[i][1], i);
			GuestNeed[i]=guestInformation[i][2];
			GuestNeedTemp[i]=guestInformation[i][2];
		}
		for(int i = 0; i < point2GuestPath.length; i++)
			point2GuestPath[i] = new ArrayList<>();
		//先对直连的网络节点找到路径
		boolean[] flag = new boolean[numberOfPoint];
		Arrays.fill(flag, false);
		
		List<Integer> allFoundPoint = new ArrayList<Integer>();
		for(int i = 0; i < numberOfGuest; i++)
		{
			int tempPoint = allGuestPoint[i];
			point2GuestPath[tempPoint].add(tempPoint);
			point2GuestPath[tempPoint].add(i);
			flag[tempPoint] = true;
			allFoundPoint.add(tempPoint);
		}
		while(allFoundPoint.size() < numberOfPoint)
		{
			for(int i = allFoundPoint.size() - 1; i >= 0; i--)
			{
				int temps = allFoundPoint.get(i);
				for(int j = 0; j < pointConnectInformation[temps].size(); j++)
				{
					int nextTemps = pointConnectInformation[temps].get(j);
					if(flag[nextTemps] == false)
					{
						point2GuestPath[nextTemps].add(nextTemps);
						point2GuestPath[nextTemps].addAll(point2GuestPath[temps]);
						flag[nextTemps] = true;
						allFoundPoint.add(nextTemps);
					}
				}
			}
		}
		//对所有网络节点按照性价比排序，保存在netPoints中
		capOfPoint = Countcap2();
		AckLevelOfpoint();
		getpprofpoint();
		for(int i = 0; i < numberOfGuest; i++)
			lastFlowOfGuest.put(i, GuestNeed[i]);
		int[] order = new int[numberOfPoint];
		for(int i = 0; i < numberOfPoint; i++)
		{
			order[i] = i;
		}
		HashMap<Integer, Integer> tempcapofpoint=new HashMap<>();
		tempcapofpoint=Countcap1();
		for(int i = 0; i < numberOfPoint; i++)
		{
			//越小的排在越前面
			for(int j = i + 1; j < numberOfPoint; j++)
				if(tempcapofpoint.get(order[i]) < tempcapofpoint.get(order[j])){
					int temp = order[i];
					order[i] = order[j];
					order[j] = temp;
				}
		}

		for(int i = 0; i < numberOfPoint; i++)
		{
			netPoints.add(order[i]);
		}
		
		int[] order1 = new int[numberOfGuest];
		for(int i = 0; i < numberOfGuest; i++)
		{
			order1[i] = i;
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			//越小的排在越前面
			for(int j = i + 1; j < numberOfGuest; j++)
				if(GuestNeed[order1[i]] > GuestNeed[order1[j]]){
					int temp = order1[i];
					order1[i] = order1[j];
					order1[j] = temp;
				}
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			guestPoints.add(order1[i]);
		}
		tempcapofpoint=Countcap1();
		for(int i = 0; i < numberOfPoint; i++)
		{
			//越小的排在越前面
			for(int j = i + 1; j < numberOfPoint; j++)
				if(tempcapofpoint.get(order[i]) < tempcapofpoint.get(order[j])){
					int temp = order[i];
					order[i] = order[j];
					order[j] = temp;
				}
		}
		
		for(int i = 0; i < numberOfPoint; i++)
		{
			netPoints.add(order[i]);
		}
		//for(int v:netPoints)
			//System.out.println(capOfPoint.get(v)+":"+priceOfPoint[v]+":"+levelofpoint.get(v));
		//System.out.println();:
		//for(int i=0;i<netPoints.size();i++)
			//System.out.println("节点"+netPoints.get(i)+"的性价比为："+pprofpoint.get(netPoints.get(i)));
		
		//对所有消费节点按照需求排序，保存在guestPoints中，从小到大的顺序
		
		for(int i = 0; i < numberOfGuest; i++)
		{
			order1[i] = i;
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			//越小的排在越前面
			for(int j = i + 1; j < numberOfGuest; j++)
				if(GuestNeed[order1[i]] > GuestNeed[order1[j]]){
					int temp = order1[i];
					order1[i] = order1[j];
					order1[j] = temp;
				}
		}
		System.out.println();
		for(int i = 0; i < numberOfGuest; i++)
		{
			guestPoints.add(order1[i]);
			System.out.print(order1[i]+" ");
		}
		System.out.println();
		
		int tempCost = 0;
		
		for(int i = 0; i < netPoints.size(); i++)
		{
			int tempPoint = netPoints.get(i);
			tempCost += priceOfPoint[tempPoint] + priceOfServer[levelofpoint.get(tempPoint)];
			if(numberOfPoint<=2000)
			moneyForFlow = 60000;
			else if(numberOfPoint<=3000)
				moneyForFlow = 100000;
			else
				moneyForFlow = 150000;
			if(tempCost > (leftMoney - moneyForFlow))
				break;
			todeployeServer.add(tempPoint);
			countofaddserver.put(tempPoint, count);
			allFlow += Math.min(capOfPoint.get(tempPoint), capOfServer[levelofpoint.get(tempPoint)]);
		}
		firstServerNum = todeployeServer.size();
		System.out.println("allflow:"+allFlow);
		int tempGuestFlow = 0;	
		flowoftodeploye.clear();
		//选择消费节点，从小到大依次枚举，看最多能加进去多少个
		int allAddednum = 0;
		for(int i = 0; i < numberOfGuest; i++)
		{
			tempGuestFlow += GuestNeed[order1[i]];
			if(tempGuestFlow <= allFlow)
				allAddednum++;
			else 
				break;
		}
		int selectedNum = (int)(allAddednum * 0.45);
		int averageFlow = allFlow/selectedNum;
		
		int usedFlow = 0;
		
		ArrayList<Integer> saveguset=new ArrayList<>();
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(capOfPoint.get(VertexofGuest[i]) <= averageFlow)
			{
				usedFlow += capOfPoint.get(VertexofGuest[i]);
				
				System.out.println(capOfPoint.get(VertexofGuest[i]) + " : " + GuestNeed[i]);
				flowoftodeploye.put(i, capOfPoint.get(VertexofGuest[i]));
				saveguset.add(i);
			}
		}
		int leftUsedFlow = 0;
		int leftAddedNum = 0;
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(saveguset.contains(order1[i]))
				continue;
			leftUsedFlow += GuestNeed[order1[i]];
			if(leftUsedFlow <= (allFlow - usedFlow))
				leftAddedNum++;
			else {
				break;
			}
			
		}
		int leftSelectedNum = (int)(leftAddedNum * 0.35);
		int leftAverageFlow = (int)(allFlow - usedFlow)/leftSelectedNum;
		System.out.println(leftAverageFlow);
		int thePoint = 0;
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(saveguset.contains(order1[i]))
				continue;
			
			if(GuestNeed[order1[i]] > leftAverageFlow)
			{
				thePoint = i;
				break;
			}
				
		}
		leftUsedFlow = 0;
		for(int i = 0; i < thePoint; i++)
		{
			if(saveguset.contains(order1[i]))
				continue;
			leftUsedFlow += leftAverageFlow;
			if(leftUsedFlow <= (allFlow - usedFlow))
				flowoftodeploye.put(order1[i], leftAverageFlow);
			else 
				break;
		}
		for(int i = thePoint; i < numberOfGuest; i++)
		{
			if(saveguset.contains(order1[i]))
				continue;
			leftUsedFlow += GuestNeed[order1[i]];
			if(leftUsedFlow <= (allFlow - usedFlow))
				flowoftodeploye.put(order1[i], GuestNeed[order1[i]]);
			else {
				break;
			}
		}
		
		
		 for(int i : flowoftodeploye.keySet())
		    	lastFlowOfGuest.put(i, flowoftodeploye.get(i));
		int adjcount=0;
		System.out.println("服务器序列："+todeployeServer);
		for(int i=0;i<todeployeServer.size();i++){
				ArrayList<Integer> adjlist=new ArrayList<>();
				int server=todeployeServer.get(i);
				System.out.println("服务器位置:"+server);
				System.out.println("服务器通量："+capOfPoint.get(server));
			    adjlist.addAll(pointConnectInformation[server]);
			    System.out.println("before："+adjlist);
			    adjlist.retainAll(todeployeServer);
			    System.out.println("after:"+adjlist);
		}
		
	    int batchnum=5;
    	}else 
    		if(count==59){
			allFlow = 0;
			for(int server:todeployeServer){
			double profitofsell=0.8*(double)priceOfServer[levelofpoint.get(server)]*(countofaddserver.get(server)-1)/60;
			double profitofnotsell=(double)kind1.size()/todeployeServer.size()*moneyPerTenSec;
			System.out.println("profitofsell"+profitofsell);
			System.out.println("profitofnotsell"+profitofnotsell);
			if(profitofnotsell>profitofsell)
			{
				saveserver.add(server);
				allFlow += Math.min(capOfPoint.get(server), capOfServer[levelofpoint.get(server)]);
			}
			}
			todeployeServer.clear();
			for(int server : saveserver)
				todeployeServer.add(server);
		}
    	else{
    		//第二轮及之后每一轮需要的更新
    		allFlow = 0;
    		System.out.println("round"+count+" start!");
    		System.out.println("************************************************");
    		kind1.clear();
	    	kind23.clear();
	    	
	    	kind4.clear();
	    	
    		for(int i=0;i<graphContent.length;i++)
    		{
    			String[] contentReadLine = graphContent[i].split(" ");
    			if(contentReadLine.length==2 && i > (numberOfPoint+numberOfGuest))
    			{
    				leftMoney = Integer.parseInt(contentReadLine[1]);
    				continue;
    			}
    			if(i >= numberOfPoint + numberOfEdge + numOfLevel + 6&& contentReadLine.length==4)
    			{
    				selectedID[Integer.parseInt(contentReadLine[0])]= Integer.parseInt(contentReadLine[3]);
    				int from = Integer.parseInt(contentReadLine[0]);
    				int selectedID  = Integer.parseInt(contentReadLine[3]);
    				if(selectedID == 0)
    					kind4.add(from);
    				if(selectedID == myID)
    					kind1.add(from);
    				if(selectedID != myID && selectedID != 0)
    				{
    						kind23.add(from);
    				}
    			}   		
    		}
    		System.out.println(count+"：number of our guest kind1 "+kind1.size());
    		
    		
    		System.out.println(count+"：number of enemy guest " + kind23.size());
    		//对我们上轮未占领到的节点排序，主要有三种
    		//试图占领，却被对方抢占 kind2
    		//没有打算占领，对方占领的 kind3
    		//双方都未打算占领 kind4
    		//我们占领的点，不加流量；对方占领的点，流量加10；双方都没占领的点，状态设为5
    		
    		HashMap<Integer, Integer> tempFlowOfToDeploy = new HashMap<Integer, Integer>();
    		HashMap<Integer, Integer> tempFlowOfToSort = new HashMap<Integer, Integer>();
    		if(count <= 30)
    		{
    		for(int i = 0; i < kind1.size(); i++)
    		{
    			int previous = lastFlowOfGuest.get(kind1.get(i));		
    			tempFlowOfToDeploy.put(kind1.get(i), (int)Math.min(previous + 13 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind1.get(i)])));
    			tempFlowOfToSort.put(kind1.get(i), (int)Math.min(previous + 13 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind1.get(i)])));
    		}
    		for(int i = 0; i < kind23.size(); i++)
    		{
    			int previous = lastFlowOfGuest.get(kind23.get(i));		
    			tempFlowOfToDeploy.put(kind23.get(i), (int)Math.min(previous + 20 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind23.get(i)])));
    			tempFlowOfToSort.put(kind23.get(i), (int)Math.min(previous  + 20 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind23.get(i)]))/2);
    		}
    		for(int i = 0; i < kind4.size(); i++)
    		{
    			int previous = lastFlowOfGuest.get(kind4.get(i));
    			tempFlowOfToDeploy.put(kind4.get(i), (int)Math.min(previous + 8 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind4.get(i)])));
    			tempFlowOfToSort.put(kind4.get(i), (int)Math.min(previous +8 + (30-count)* 0.3, capOfPoint.get(VertexofGuest[kind4.get(i)])));
    		}
    		}
    		else {
    			for(int i = 0; i < kind1.size(); i++)
        		{
        			int previous = lastFlowOfGuest.get(kind1.get(i));		
        			tempFlowOfToDeploy.put(kind1.get(i), (int)Math.min(previous + 13, capOfPoint.get(VertexofGuest[kind1.get(i)])));
        			tempFlowOfToSort.put(kind1.get(i), (int)Math.min(previous + 13 , capOfPoint.get(VertexofGuest[kind1.get(i)])));
        		}
        		for(int i = 0; i < kind23.size(); i++)
        		{
        			int previous = lastFlowOfGuest.get(kind23.get(i));		
        			tempFlowOfToDeploy.put(kind23.get(i), (int)Math.min(previous + 20 , capOfPoint.get(VertexofGuest[kind23.get(i)])));
        			tempFlowOfToSort.put(kind23.get(i), (int)Math.min(previous  + 20, capOfPoint.get(VertexofGuest[kind23.get(i)]))/2);
        		}
        		for(int i = 0; i < kind4.size(); i++)
        		{
        			int previous = lastFlowOfGuest.get(kind4.get(i));
        			tempFlowOfToDeploy.put(kind4.get(i), (int)Math.min(previous + 8 , capOfPoint.get(VertexofGuest[kind4.get(i)])));
        			tempFlowOfToSort.put(kind4.get(i), (int)Math.min(previous + 8 , capOfPoint.get(VertexofGuest[kind4.get(i)])));
        		}
			}
    		int sumcost = 0;	
    		int tempCost = 0;
    		for(int v:todeployeServer)
    		{
    			tempdeployeserver.add(v);
    			allFlow += Math.min(capOfPoint.get(v), capOfServer[levelofpoint.get(v)]);
    		}
    		
    		for(int i = todeployeServer.size(); i < netPoints.size(); i++)
    		{
    			//System.out.println(i);
    			int tempPoint = netPoints.get(i);
    			//System.out.println("当前节点："+tempPoint);
    			//System.out.println("pprof"+tempPoint+":"+pprofpoint.get(tempPoint));
    			
    		   if(count>55)
    			   break;
    			
                tempCost += priceOfPoint[tempPoint] + priceOfServer[levelofpoint.get(tempPoint)];
    			//System.out.println("tempcost"+tempCost);
                
                if(cost > moneyForFlow-20000)
                	moneyForFlow += 10000;
                //moneyForFlow += 3000;
    			if(tempCost > (leftMoney - moneyForFlow)){
    				tempCost-=priceOfPoint[tempPoint] + priceOfServer[levelofpoint.get(tempPoint)];
    				break;
    			}
    			tempdeployeserver.add(tempPoint);
    			countofaddserver.put(tempPoint, count);
    			allFlow += Math.min(capOfPoint.get(tempPoint), capOfServer[levelofpoint.get(tempPoint)]);
    		}
    		System.out.println("sum cost of round"+count+":"+sumcost);
    		System.out.println("left money of round"+count+":"+(leftMoney-sumcost));
    		
    		int[] orderOfFlow = new int[numberOfGuest];
    		for(int i = 0; i < numberOfGuest; i++)
    			orderOfFlow[i] = i;
    		for(int i = 0; i < numberOfGuest; i++)
    			for(int j = i + 1; j < numberOfGuest; j++)
    				if(tempFlowOfToSort.get(orderOfFlow[i]) > tempFlowOfToSort.get(orderOfFlow[j]))
    				{
    					int temp = orderOfFlow[i];
    					orderOfFlow[i] = orderOfFlow[j];
    					orderOfFlow[j] = temp;
    				}
    		
    		flowoftodeploye.clear();
    		//选择消费节点，从小到大依次枚举，看最多能加进去多少个
    		int tempGuestFlow = 0;
    		for(int i = 0; i < numberOfGuest; i++)
    		{
    			int tempGuest = orderOfFlow[i];
    			tempGuestFlow += tempFlowOfToDeploy.get(tempGuest);
    			if(tempGuestFlow <= allFlow)
    				flowoftodeploye.put(tempGuest, tempFlowOfToDeploy.get(tempGuest));
    			else 
    				break;
    		}
    		
    		for(int s : flowoftodeploye.keySet())
    			lastFlowOfGuest.put(s, flowoftodeploye.get(s));
    	}
    	
		/******************************初始化完毕***********************************/
		String[] output=new String[MAXEDGE - numberOfPoint + 2];
		HashMap<Integer, Integer> serverInformation=new HashMap<>();//最优服务器布置情况          
	    if(count == 1)
	      for(int i = 0; i < todeployeServer.size(); i++)
			 {
				 serverInformation.put(todeployeServer.get(i), levelofpoint.get(todeployeServer.get(i)));
			 }
	      else if(count==59)
			 for(int i = 0; i < saveserver.size(); i++)
			 {
				 serverInformation.put(saveserver.get(i), levelofpoint.get(saveserver.get(i)));
			  }
			 else
			 for(int i = 0; i < tempdeployeserver.size(); i++)
			 {
			    serverInformation.put(tempdeployeserver.get(i), levelofpoint.get(tempdeployeserver.get(i)));
			 }
		System.out.println("round"+count+":number of to deploye guest:"+flowoftodeploye.size());
		
		int[] result=new int[2];
		result= spfaMCMF_addDecLevel(serverInformation,flowoftodeploye);
		System.out.println("cost of round"+count +":"+ result[1]);
		int satisfiedNum = 0;
		
		for(int i = 0; i < todeployeServer.size(); i++)
		{
			System.out.println(checkFlow.get(todeployeServer.get(i)) + " : " + Math.min(capOfServer[levelofpoint.get(todeployeServer.get(i))], capOfPoint.get(todeployeServer.get(i))));
		}
		for(int s : flowoftodeploye.keySet())
		{
			System.out.println(GuestNeed[s] + " :" + checkPointFlow.get(s) + " : " + flowoftodeploye.get(s) + " : " + capOfPoint.get(VertexofGuest[s]));
			if(checkPointFlow.get(s) >= flowoftodeploye.get(s));
				satisfiedNum++;
		}
		System.out.println("all satisfied guest num : " + satisfiedNum);
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
		
		Set<Integer> realServer = new HashSet<>();
		for(int i = 0; i < resultList.size(); i++)
		{
			String[] pathInfomation = resultList.get(i).split(" ");
			int tempServer = Integer.parseInt(pathInfomation[0]);
			realServer.add(tempServer);
		}
		
		System.out.println("number of real server in link without 0 path:" + realServer.size());
		
		List<Integer> realDeployedServers = new ArrayList();
		for(int i = 0; i < resultList.size(); i++)
	       {
				
				String[] pathInfomation = resultList.get(i).split(" ");
				int IdOfServer = Integer.parseInt(pathInfomation[0]);
				realDeployedServers.add(IdOfServer);
				int tempFlowSize = Integer.parseInt(pathInfomation[pathInfomation.length - 1]);
				String outputString = "";
				for(int j = 0; j < pathInfomation.length - 1; j++)
					outputString += pathInfomation[j] + " ";
	    	   output[i + 2] = outputString + serverInformation.get(IdOfServer) +  " " + tempFlowSize;
	       }
		Set realDeployedServer = new HashSet(realDeployedServers);
		List<Integer> failDeployedServers = new ArrayList();
		
		if(count == 59){
			for(int s : saveserver)
				if(!realDeployedServer.contains(s))
					failDeployedServers.add(s);
			System.out.println("count 59 server num: " + saveserver.size());
		}
		else{
			for(int v:tempdeployeserver)
				if(!todeployeServer.contains(v))
					todeployeServer.add(v);
			for(int s : todeployeServer)
			if(!realDeployedServer.contains(s))
				failDeployedServers.add(s);
		}
		System.out.println("number of 0 flow server: " + failDeployedServers.size());
		System.out.println("number of real server in link including 0 path:" + (realDeployedServer.size() + failDeployedServers.size()));
		for(int i = 0; i < failDeployedServers.size(); i++)
		{
			output[i + 2 + resultList.size()] = "";
			for(int j = 0; j < point2GuestPath[failDeployedServers.get(i)].size(); j++)
			{
				output[i + 2 + resultList.size()] += point2GuestPath[failDeployedServers.get(i)].get(j) + " ";
				
			}
			output[i + 2 + resultList.size()] += serverInformation.get(failDeployedServers.get(i));
			output[i + 2 + resultList.size()] += " " + 0;
		}
		output[0]=Integer.toString(resultList.size() + failDeployedServers.size());
		output[1]="";
		System.out.println();
		return output;
    }	
    /**
     * 计算该位置与已经部署的服务器当中多少个服务器相邻
     * @param serverpos
     * @return
     */
    public static int countadjtodeployed(int serverpos,ArrayList<Integer> deployedlist){
    	int count=0;
    	for(int deployed:deployedlist){
    		if(pointConnectInformation[deployed].contains(serverpos))
    			count++;
    	}
    	return count;
    }
   public static int checkguest(int count,int flow){ 
	   int result=1;
	   double profit=moneyPerTenSec*(60-count);
	   double cost=0;
	   double averageppr=0;
	  for(int v:pprofpoint.keySet())
		   averageppr+=pprofpoint.get(v);
		   averageppr/=pprofpoint.size();
		   averageppr*=0.6;
	  // System.out.println("平均性价比："+averageppr);
	   cost=averageppr*flow;
	   if(cost>profit)
		   result=-1;
	   return result;
   }
   public static int checkserver(int serverpos){
	   int result=1;
		int cost= priceOfPoint[serverpos] + priceOfServer[levelofpoint.get(serverpos)];
		
	   return result;
   }
    public static void AckLevelOfpoint(){
    	HashMap<Integer, Integer> capofpoint=new HashMap<>();
    	capofpoint = Countcap2();
    	for(int i=0;i<numberOfPoint;i++){
    		int bestlevel=0;
    		double minppr=MAX;
    		/*for(int j=0;j<numOfLevel;j++){
    			int cost=priceOfServer[j]+priceOfPoint[i];
    			int cap=Math.min(capOfServer[j], capofpoint.get(i));
    			double ppr=(double)cost/cap;
    			if(ppr<minppr){
    				minppr=ppr;
    				bestlevel=j;
    			}
    		}*/
    		if(capofpoint.get(i)>=capOfServer[numOfLevel-1])
    			levelofpoint.put(i,numOfLevel-1);
    		else{
    		for(int j=0;j<numOfLevel;j++){
    			
    			if(capOfServer[j]>=capofpoint.get(i)){
    		        levelofpoint.put(i,j);
    		        break;
    			}
    		}
    		}
    	}
//    	for(int i = 0; i < numberOfGuest; i++)
//		{
//			int flowNeeded = guestInformation[i][2];
//			int pointServer  = guestInformation[i][1];
//			while(capOfServer[levelofpoint.get(pointServer)] < flowNeeded && (levelofpoint.get(pointServer) < (numOfLevel -1)))
//			{
//				levelofpoint.put(pointServer, levelofpoint.get(pointServer) + 1);
//			}
//		}
    }
    public static void getpprofpoint(){
    	
    	for(int i=0; i<numberOfPoint; i++){
    		int cost = priceOfServer[levelofpoint.get(i)] + priceOfPoint[i];
    		double ppr = (double)cost/(Math.min(capOfPoint.get(i), capOfServer[levelofpoint.get(i)]));
    		pprofpoint.put(i, ppr);
    	}
    }
   
	/**
	  * 
	  * @return 每个节点的通量
	  */
	 public static HashMap<Integer, Integer> Countcap1(){
		 int[] temppriceOfPoint=new int[priceOfPoint.length];
		  System.arraycopy(priceOfPoint, 0, temppriceOfPoint, 0, priceOfPoint.length);
	    	HashMap<Integer, Integer> valuemap=new HashMap<>();
	    	for(int i=0;i<numberOfEdge;i++){	
	    		if(valuemap.containsKey(edgeInformation[i][0]))
	    			valuemap.put(edgeInformation[i][0],valuemap.get(edgeInformation[i][0])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][0], edgeInformation[i][2]);
	    		if(valuemap.containsKey(edgeInformation[i][1]))
	    			valuemap.put(edgeInformation[i][1],valuemap.get(edgeInformation[i][1])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][1], edgeInformation[i][2]);	
	    	}
	    	int maxprice=0;
	    	int secondmaxprice=0;
	    	for(int i=0;i<temppriceOfPoint.length;i++)
	    		if(temppriceOfPoint[i]>maxprice)
	    			maxprice=temppriceOfPoint[i];
	    	Set<Integer> price=new HashSet<>();
	    	//System.out.println(maxprice);
	    	
	    	for(int i=0;(i<temppriceOfPoint.length);i++){
	    		if(temppriceOfPoint[i]!=maxprice)
	    		price.add(temppriceOfPoint[i]);
	    	}
	    	//System.out.println(price);
	    	for(int V:price)
	    		if(V>secondmaxprice)
	    			secondmaxprice=V;
	    	//System.out.println(secondmaxprice);
	    	for(int V:valuemap.keySet())
	    		if(temppriceOfPoint[V]>=maxprice)
	    			valuemap.put(V, -1);
	    	return valuemap;
	    }
	 public static HashMap<Integer, Integer> Countcap2(){
		 int[] temppriceOfPoint=new int[priceOfPoint.length];
		  System.arraycopy(priceOfPoint, 0, temppriceOfPoint, 0, priceOfPoint.length);
	    	HashMap<Integer, Integer> valuemap=new HashMap<>();
	    	for(int i=0;i<numberOfEdge;i++){	
	    		if(valuemap.containsKey(edgeInformation[i][0]))
	    			valuemap.put(edgeInformation[i][0],valuemap.get(edgeInformation[i][0])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][0], edgeInformation[i][2]);
	    		if(valuemap.containsKey(edgeInformation[i][1]))
	    			valuemap.put(edgeInformation[i][1],valuemap.get(edgeInformation[i][1])+edgeInformation[i][2]);
	    		else valuemap.put(edgeInformation[i][1], edgeInformation[i][2]);	
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
	public static int[] spfaMCMF_addDecLevel(HashMap<Integer,Integer> serverSeries, HashMap<Integer,Integer> tempGuestNeed)
	{
		//System.out.println("服务器个数："+serverSeries.size());
		resultList.clear();
		flow=0;
		cost=0;
		int cost_fixed1 = 0;
		int cost_fixed2 = 0;
		//将不提供服务的消费节点的容量设为0，通过这种方式动态改变消费节点，而不影响拓扑结构
		Iterator iter = tempGuestNeed.keySet().iterator();
		while(iter.hasNext())
	{
			int key = (int)iter.next();//和消费节点直连的网络节点编号
			int value = tempGuestNeed.get(key);//需要提供的流量
			
			int idx = guestPointIdx.get(key);
			edge[idx].capacity = value;
		}
		for(int i = 0; i < numberOfGuest; i++)
		{
			if(!tempGuestNeed.containsKey(i))
			{
				int idx = guestPointIdx.get(i);
				edge[idx].capacity = 0;
			}
		}
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
				if(count == 1)
				{
					cost_fixed1 += priceOfPoint[key];//	网络节点的部署成本
					cost_fixed2 += priceOfServer[value];//	服务器的硬件成本
				}
				else if(!todeployeServer.contains(key))//如果上轮已经部署该节点，则该轮不需要支付部署费用和硬件成本
				{	
					
					cost_fixed1 += priceOfPoint[key];//	网络节点的部署成本
					cost_fixed2 += priceOfServer[value];//	服务器的硬件成本
				}

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
        System.out.println("link cost of round"+count+":" + cost);
    	result[1] = cost + cost_fixed1 + cost_fixed2;
    	for(int i = 0; i < serverSeries_deployed.size(); i++)
		{
			int no = serverSeries_deployed.get(i);
			checkFlow.put(no, edge[serverIdx.get(no)].flow);
		}
    	for(int i : tempGuestNeed.keySet())
		{
			checkPointFlow.put(i, edge[guestPointIdx.get(i)].flow);
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
////		
		
		//System.out.println("降档前的花费" + result[1]);
	
//		System.out.println("流量" + result[0]);
		//System.out.println("cost" + result[1]);
	    return result;
	}
	
   
	
	 public static int[][] lengthOfEachPoint()
	 {
		 int[][] lengthOfEach = new int[numberOfGuest][numberOfGuest];
		 for(int i = 0; i < numberOfGuest; i++)
		 {
			 
			 int[] lengthVexs = dijkstra(allGuestPoint[i]);
			 for(int j = 0; j < numberOfGuest; j++)
			 {
				
				 lengthOfEach[i][j] = lengthVexs[allGuestPoint[j]];
			 }
		 }
		 return lengthOfEach;
	 }
	 public static int[] dijkstra(int s)
	    {
	    	int num = numberOfPoint;
	    
	    	 boolean[] flag = new boolean[numberOfPoint];
	         int[] prev=new int[numberOfPoint];
	         int[] dist=new int[numberOfPoint];
	         for (int i = 0; i < numberOfPoint; i++) {
	         	for (int j = 0; j < numberOfPoint; j++) {
	         		if(netCost[i][j]==0)
	         			netCost[i][j]=MAX;
	         	}
	         }
	         // 初始化
	         for (int i = 0; i < numberOfPoint; i++) {
	             flag[i] = false;          // 顶点i的最短路径还没获取到。
	             prev[i] = 0;              // 顶点i的前驱顶点为0。
	             dist[i] = netCost[s][i];  // 顶点i的最短路径为"顶点vs"到"顶点i"的权。
	         }

	         // 对"顶点vs"自身进行初始化
	         flag[s] = true;
	         dist[s] = 0;

	         // 遍历mVexs.length-1次；每次找出一个顶点的最短路径。
	         int k=0;
	         for (int i = 1; i <numberOfPoint; i++) {
	             // 寻找当前最小的路径；
	             // 即，在未获取最短路径的顶点中，找到离vs最近的顶点(k)。
	             int min = MAX;
	             for (int j = 0; j < numberOfPoint; j++) {
	                 if (flag[j]==false && dist[j]<min) {
	                     min = dist[j];
	                     k = j;
	                 }
	             }
	             // 标记"顶点k"为已经获取到最短路径
	             flag[k] = true;

	             // 修正当前最短路径和前驱顶点
	             // 即，当已经"顶点k的最短路径"之后，更新"未获取最短路径的顶点的最短路径和前驱顶点"。
	             for (int j = 0; j < numberOfPoint; j++) {
	                 int tmp = (netCost[k][j]==MAX? MAX : (min + netCost[k][j]));
	                 if (flag[j]==false && (tmp<dist[j]) ) {
	                     dist[j] = tmp;
	                     prev[j] = k;
	                 }
	             }
	         }
	     return dist;
	    }
	 public static int getNumOfSatisfiedGuest(HashMap<Integer,Integer> serverSeries,  HashMap<Integer,Integer> tempGuestNeed)
		{
			int num = 0;
			
		    
		    spfaMCMF_addDecLevel(serverSeries, tempGuestNeed);
			
			Iterator iterator2 = tempGuestNeed.keySet().iterator();
			
			while(iterator2.hasNext())
			{
				int key = (int)iterator2.next();
				
				if(checkPointFlow.get(key) -  GuestNeed[key]>= 0)
				{
					num++;
					//System.out.println(key + " : " + realGetFlow[key]);
				}
			}
		    return num;
		}
}

