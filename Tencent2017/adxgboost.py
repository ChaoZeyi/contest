# -*- coding:utf-8 -*-
'''
Created on 2017.5.27

@author: xy
'''
import scipy as sp
from scipy import sparse
from pandas import DataFrame,Series
from sklearn.preprocessing import OneHotEncoder
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
import numpy as np # linear algebra
from collections import Counter
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import math
import xgboost as xgb


#----------------分割线，基本的特征选择我已经完成，下面是隐藏特征的挖掘以及数据的处理------------------#
'''
某个用户距离上一次点击的时间间隔，强特征，已经转换为分钟处理好加在表格的新一列
def interclick(clicktime1,clicktime2):
    min1=clicktime1%100
    hour1=math.floor((clicktime1%10000)/100)
    day1=math.floor(clicktime1/10000)
    min2=clicktime2%100
    hour2=math.floor((clicktime2%10000)/100)
    day2=math.floor(clicktime2/10000)
    interclicktime=(day2*1440+hour2*60+min2-(day1*1440+hour1*60+min1))
    return interclicktime
#读取训练集
train=pd.read_csv('E:/pre/train.csv')
train=train.drop('conversionTime', axis=1)
train['interclickTime']=-1
dict={}
userser=train['userID']
clicktimeser=train['clickTime']
inter=train['interclickTime'].values
for i in range(0,3749528):
    print(i)
    userID=userser[i]
    clicktime=clicktimeser[i]
    if userID not in dict:
        dict[userID]=clicktime
    else:
        print('userID',userID)
        lastclicktime=dict[userID]
        inter[i]=interclick(lastclicktime, clicktime)
        dict[userID]=clicktime
train['interclickTime']=inter
train.to_csv('E:/train.csv',index=False)
#测试数据集

test=pd.read_csv('E:/pre/test.csv')
test['interclickTime']=-1
inter=test['interclickTime'].values
userser=test['userID']
clicktimeser=test['clickTime']
for i in range(0,338489):
    #print(i)
    userID=userser[i]
    clicktime=clicktimeser[i]
    if userID not in dict:
        dict[userID]=clicktime
    else:
        print('userID',userID)
        lastclicktime=dict[userID]
        inter[i]=interclick(lastclicktime, clicktime)
        dict[userID]=clicktime
test['interclickTime']=inter
test.to_csv('E:/test.csv',index=False)
'''
'''
df=train[['userID']].append(test[['userID']])
groupby_userID=df.groupby('userID').size()
train['userID_sum']=train['userID'].apply(lambda x:groupby_userID[x])
test['userID_sum']=test['userID'].apply(lambda x:groupby_userID[x])
'''

#-----------------------分割线，下面是读数据，表的合并-----------------------#
train=pd.read_csv('F:/github/contest/tencent/trainpro.csv')
test=pd.read_csv('F:/github/contest/tencent/testpro.csv')

#已经处理好的positionID-connectiontype组合特征统计表，据说是个很强的组合特征
pcc=pd.read_csv('F:/github/contest/tencent/pcc.csv')

instanceId=test['instanceID'].values
#读取广告特征
ad=pd.read_csv('F:/github/contest/tencent/pre/ad.csv')
#读取用户特征
user=pd.read_csv('F:/github/contest/tencent/pre/user.csv')
#读取广告目录
catagory=pd.read_csv('F:/github/contest/tencent/pre/app_categories.csv')
position=pd.read_csv('F:/github/contest/tencent/position.csv')
user_app_actions=pd.read_csv('F:/github/contest/tencent/pre/user_app_actions.csv')
user_installedapps=pd.read_csv('F:/github/contest/tencent/pre/user_installedapps.csv')

#将用户特征和广告特征合并到训练数据集
'''
对训练数据的处理，将各个表合并
'''
data=pd.merge(pd.merge(train,ad),user)
data=pd.merge(data,catagory)
data=pd.merge(data,position)
#data=pd.merge(data,pcc,on=['positionID', 'connectionType'],how='left')是否合并待确定，实验过程中发生了过拟合
'''
对测试数据做同样的处理
'''
testdata=pd.merge(pd.merge(test,ad),user)
testdata=pd.merge(testdata,catagory)
testdata=pd.merge(testdata,position)
#testdata=pd.merge(testdata,pcc,on=['positionID', 'connectionType'],how='left')
testdata=testdata.sort_index(by='instanceID')
testdata=testdata.drop(['instanceID'],axis=1)

'''
下面的代码用于贝叶斯平滑，对于长尾分布的特征（如positionID的转化率）需要进行平滑处理
已经处理好存放于position表中
a=0.52895 #这个数值是通过smooth.py计算得到的，下同
b=11.515
ave=a/b
dic=dict(Counter(data.positionID.values))
positive=data[data['label']==1]
dic2=dict(Counter(positive.positionID.values))
ad['bayes']=0
v=ad.bayes.values
positionID=ad['positionID']
for i in range(0,6582):
    ID=positionID[i]
    if ID not in dic:
        v[i]=1000*a/(a+b)#这里将转化率乘以了1000
    elif ID not in dic2:
        v[i]=1000*a/(a+b+dic[ID])
    else:
       v[i]=1000*(a+dic2[ID])/(a+b+dic[ID])
ad['bayes']=v
'''
'''
统计某个用户已经安装的APP个数,uiac:user_install_app_count
'''
uiac=user_installedapps['userID'].value_counts()
dic={'userID':uiac.index,'uiac':uiac.values}
df3=DataFrame(dic)
data=pd.merge(data,df3,on='userID',how='left')
data=data.fillna({'uiac':0})
data['uiac']=data['uiac'].values.astype(int)
testdata=pd.merge(testdata,df3,on='userID',how='left')
testdata=testdata.fillna({'uiac':0})
testdata['uiac']=testdata['uiac'].values.astype(int)
'''
统计训练期间用户安装的APP个数
'''
uiact=user_app_actions['userID'].value_counts()
dic={'userID':uiact.index,'uiact':uiact.values}
df=DataFrame(dic)
data=pd.merge(data,df,on='userID',how='left')
data=data.fillna({'uiact':0})
data['uiact']=data['uiact'].values.astype(int)
testdata=pd.merge(testdata,df,on='userID',how='left')
testdata=testdata.fillna({'uiact':0})
testdata['uiact']=testdata['uiact'].values.astype(int)

'''
统计用户在训练期间和测试期间的总点击次数,强特征！
'''
df=data[['userID']].append(testdata[['userID']])
groupby_userID=df.groupby('userID').size()
data['userID_sum']=data['userID'].apply(lambda x:groupby_userID[x])
testdata['userID_sum']=testdata['userID'].apply(lambda x:groupby_userID[x])

#----------------------------分割线，下面是数据的训练与预测----------------------------------------#

params = {
'booster': 'gbtree',
'objective': 'binary:logistic',
#'gamma': 0.1,  # 在树的叶子节点下一个分区的最小损失，越大算法模型越保守 。[0:]
'max_depth': 6, # 构建树的深度 [1:]
'subsample': 0.6, # 采样训练数据，设置为0.5，随机选择一般的数据实例 (0:1]
'colsample_bytree': 0.8, # 构建树树时的采样比率 (0:1]
'eval_metric': 'logloss',
'eta': 0.06, # 如同学习率
'seed': 710
}

train_y = data["label"]#整个训练集的x部分
train_X=data.drop('label',axis=1)#整个训练集的label部分
#划分本地训练集与测试集
X_trains, X_tests, y_train, y_test = train_test_split(train_X, train_y, test_size = 0.3)

#使用已经筛选好了的特征
feats = ['adID','age','gender','advertiserID','appCategory','connectionType','appID','camgaignID','interclickTime',
         'uiact','uiac','userID_sum','positionID','bayes']
X_train=X_trains[feats]
X_test=X_tests[feats]
test_X = testdata[feats]

#处理本地训练集与测试集
X_train = X_train.iloc[:, :].values
X_test = X_test.iloc[:, :].values
y_train = y_train.iloc[:].values
y_test = y_test.iloc[:].values

test_X = test_X.iloc[:, :].values  #真实测试集
#将数据转化为xgb格式
xgtrain = xgb.DMatrix(X_train, label = y_train)
xgval = xgb.DMatrix(X_test, label = y_test)
watchlist = [(xgtrain, 'train'),(xgval, 'test')]

num_rounds=1000
model = xgb.train(params, xgtrain, num_rounds,watchlist,early_stopping_rounds =50)#训练
xgtest=xgb.DMatrix(test_X)#预测
preds = model.predict(xgtest,ntree_limit=model.best_ntree_limit)#得到结果
df=DataFrame({'instanceId':instanceId,'prob': preds})
df.to_csv('F:/github/contest/tencent/submission.csv',index=False)
# feature engineering/encoding
'''
下面是用逻辑回归模型预测的代码，效果没有xgboost好
enc = OneHotEncoder()
for i,feat in enumerate(feats):
    x_train = enc.fit_transform(data[feat].values.reshape(-1, 1))
    x_test = enc.transform(testdata[feat].values.reshape(-1, 1))
    if i == 0:
        X_train, X_test = x_train, x_test
    else:
        X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    x_train=data['uiac'].values.reshape(-1, 1)
    x_test=testdata['uiac'].values.reshape(-1, 1)
    X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    x_train=data['uiact'].values.reshape(-1, 1)
    x_test=testdata['uiact'].values.reshape(-1, 1)
    X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    x_train=data['ucc'].values.reshape(-1, 1)
    x_test=testdata['ucc'].values.reshape(-1, 1)
    X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    x_train=data['utc'].values.reshape(-1, 1)
    x_test=testdata['utc'].values.reshape(-1, 1)
    X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
merge=pd.concat([data,testdata],ignore_index=True)
for i,feat in enumerate(feats):
    x_train = enc.fit_transform(merge[feat].values.reshape(-1, 1))
    if i == 0:
        xmerge = x_train
    else:
        xmerge = sparse.hstack((xmerge, x_train))
xmerge=xmerge.tocsr()
X_train=xmerge[:3749528,:]
X_test=xmerge[3749528:,:]
def testfeats(feats):
    for i,feat in enumerate(feats):
        x_train = enc.fit_transform(xdata[feat].values.reshape(-1, 1))
        x_test = enc.transform(xtestdata[feat].values.reshape(-1, 1))
        if i == 0:
            X_train, X_test = x_train, x_test
        else:
            X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    lr = LogisticRegression()
    lr.fit(X_train, xy_train)
    proba_test = lr.predict_proba(X_test)[:,1]
    print(logloss(xy_test, proba_test))
lr = LogisticRegression()
lr.fit(X_train, y_train)
proba_test = lr.predict_proba(X_test)[:,1]
'''
