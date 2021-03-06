决赛与复赛相比，除了大体背景相同，都是部署服务器、服务消费节点之外，感觉其他的全部都变了，下面是摘自官网的赛题描述：

#### 赛题通用性描述

##### 网络结构模型：

给定一个由若干网络节点（例如路由器、交换机）构成的网络结构无向图，每个节点至少与另外一个节点通过网络链路相连（网络链路特指两个网络节点之间直接相连的网络通路，中间没有其他网络节点，相当于无向图中的一条边），一个节点可以将收到的数据通过网络链路传输给相连的另一个节点，节点本身的转发能力无上限。每条链路的网络总带宽不同（例如某条链路的总带宽为10Gbps）。而每条链路承载的视频传输需要按照占用带宽的多少按每10秒收取对应网络租用费，每条链路的单位租用费均不同（例如某条链路的租用费为5千元/Gbps/10秒）。某条链路上被占用的带宽总和不得超过该链路的总带宽。

##### 消费节点：

给定的网络结构中有部分网络节点直接连接到小区住户的网络，每个小区住户网络在这个给定的网络结构图中呈现为一个消费节点，并存在视频带宽消耗需求。

##### 视频内容服务器：

视频内容服务器存放视频内容（如：电影影片、电视剧等），视频内容服务器的视频数据流可以经由网络节点与链路构成的网络路径流向消费节点，一台视频内容服务器可以服务多个消费节点，而一个消费节点也可以同时从多台视频内容服务器获取视频流。购买一台视频内容服务器需要硬件成本，每台视频内容服务器的输出能力存在上限（例如200Gbps/台），并分为若干档次（如1，2，3三个档次），不同档次的输出能力上限与硬件成本均不同（例如档次1的服务器硬件成本为100千元/台）。此外对于某个网络节点，部署一台视频内容服务器需要支付额外的部署成本（例如某个网络节点的额外部署成本为30千元/台），不同网络节点的部署成本可能不同。因此部署一台视频内容服务器到某网络节点的成本为该服务器硬件成本与该节点部署成本之和。

#### 行为规则

有多家视频服务商在相同的网络上部署视频内容服务器，但他们之间的数据流互不影响，链路总带宽也相互独立并相同，即每家服务商在网络中不会感知到其他服务商的存在。

参赛的每个队伍将作为其中一家视频服务商，在比赛过程中动态更新最优部署方案，与其他队伍代表的服务商共同争抢消费节点，赚取服务费用于支付成本与设备更新升级。

每家服务商可实时知悉任何消费节点选择哪家服务商，但是不清楚该服务商提供给该消费节点的具体视频带宽是多少（除非该服务商是他自己）。

比赛开始时每家服务商将被分配一定数额的初始资金用于最初部署方案，之后所有花销均来自于消费节点支付的服务费。

每隔10秒，视频服务商可以改变一次部署方案。每台视频内容服务器可以折旧卖出，折旧价格按如下公式计算：服务器折旧价格 = 服务器原始价格 * 0.8 *（600 – 使用秒数）/ 600

##### 消费节点的行为规则：

消费节点存在一个最低的带宽消耗需求。在满足最低需求的前提下，消费节点会选择给自己提供最大视频带宽的服务商作为签约服务商，并按每10秒为周期支付视频观看服务费。每个消费节点每10秒支付的服务费相同。如果在相同时间点上存在多家服务商提供了相同的最大带宽，则消费节点将随机选择其中一家服务商。如果某家服务商想要从其他服务商手中夺得消费节点，他所提供的带宽必须大于其他服务商提供给该消费节点的带宽。如果所有服务商提供的带宽都不满足某个消费节点的最低带宽需求，则该消费节点不会选择任何一家服务商。

##### 比赛胜负规则：

如果比赛过程中服务商的账户余额为0，则对应队伍立刻退出比赛，并记录时间。如果小于等于一只队伍仍坚持比赛或比赛计时终止，则比赛结束。比赛结束时，余额越高的队伍排名越高。对于退出比赛的队伍，坚持时间越长则排名越高。如果若干队伍的余额相同且均坚持完比赛，或者退出比赛且坚持时间相同，则算为平局。

#### 补充说明

1.  两个网络节点之间最多仅存在一条链路，链路上下行方向的网络总带宽相互独立，并且上下行方向的总带宽与网络租用费相同。例如对于网络节点A与B之间的链路，该条链路上的总带宽为10Gbps，每10秒单位租用费为5千元/Gbps，则表示A->B、B->A两个方向上的网络总带宽分别均为10Gbps，并且租用费均为5千元/Gbps。如果某条数据流在该链路A->B方向的占用带宽为3Gbps，那么该数据流该10秒在该链路的租用费为15千元，并且该链路A->B方向的剩余可用带宽为7Gbps。而B->A方向的剩余可用带宽不受该数据流的影响，仍为10Gbps。
2. 每个网络节点最多仅能连接一个消费节点，每个消费节点仅能连接一个网络节点。消费节点与连接的网络节点之间的链路总带宽无限大，并且网络租用费为零。
3. 网络节点数量不超过10000个，每个节点的链路数量不超过10000条，消费节点的数量不超过10000个。
4. 链路总带宽与网络租用费为[0, 100]的整数，视频内容服务器部署成本、消费节点每10秒的视频服务费与最低带宽消耗需求均为[0,1000000]的整数。
5. 部署方案中，网络路径上的占用带宽必须为整数。
6. 每个网络节点上最多仅可部署一台视频内容服务器。
7. 购买的视频内容服务器一旦部署到某节点上之后不得转移到其他节点使用。如果需要移除、升级、降级该节点的服务器，必须先将原有视频内容服务器折旧变卖。
8. 如果某网络节点上一轮（10秒前）没有部署服务器，则本轮部署服务器需要支付部署成本。否则如果该网络节点已部署了服务器，需要升级或降级服务器的档次，则无需支付部署成本。
9. 如果服务商向某个消费节点提供了带宽，但是未抢到该消费节点，仍将承担链路租用费。
10. 比赛结束时，服务器不会被自动折旧变卖，是否变卖以及何时变卖服务器由服务商自行决定。
11. 比赛时长为10分钟。在第600秒时会按照第590秒的部署方案计算服务费与网络租用费，不会再读取部署方案，并随后结束比赛。


