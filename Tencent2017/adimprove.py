# -*- coding:utf-8 -*-
'''
Created on 2017.5.18

@author: xy
'''
import scipy as sp
from pandas import DataFrame,Series
import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
from sklearn.decomposition import PCA
from sklearn.ensemble import ExtraTreesClassifier
from sklearn.svm import SVC
from sklearn.model_selection import train_test_split
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import GridSearchCV
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression 
from sklearn.ensemble import GradientBoostingClassifier
import xgboost as xgb

train=pd.read_csv('E:/pre/train.csv')
trainuser=train['positionID'].values
#测试数据集
test=pd.read_csv('E:/pre/test.csv')
testuser=test['positionID'].values
instanceId=test['instanceID'].values
#读取广告特征
ad=pd.read_csv('E:/pre/ad.csv')
#读取用户特征
user=pd.read_csv('E:/pre/user.csv')
#读取广告目录
catagory=pd.read_csv('E:/pre/app_categories.csv')
pos=pd.read_csv('E:/pre/position.csv')
'''
经过测试，train和test共有87975个公共用户
'''
settrain=set()
settest=set()
for id in trainuser:
    settrain.add(id)
for id in testuser:
    settest.add(id)
u=settrain.intersection(settest)
len(u)

#将用户特征和广告特征合并到训练数据集
'''
对训练数据的处理
'''
data=pd.merge(pd.merge(train,ad),user)
data=pd.merge(data,catagory)
data=pd.merge(data,pos)
data=data.drop(['conversionTime'],axis=1)
'''
对测试数据的处理
'''
testdata=pd.merge(pd.merge(test,ad),user)
testdata=pd.merge(testdata,catagory)
testdata=pd.merge(testdata,pos)
testdata=testdata.sort_index(by='instanceID')
instanceId=testdata['instanceID'].values
testdata=testdata.drop(['instanceID'],axis=1)
'''
下面是特征选择部分
'''
#labels=data['label']
data=data.drop(['haveBaby','appPlatform','telecomsOperator'],axis=1)
testdata=testdata.drop(['haveBaby','appPlatform','telecomsOperator'],axis=1)
data=pd.concat([data,testdata],ignore_index=True)
data=pd.get_dummies(data,columns=['connectionType','gender','advertiserID','appID','sitesetID', 'positionType'])
#降维
pca = PCA(n_components=60,whiten=True)
pca = pca.fit(data)
dataPCA = pca.transform(data)
train=dataPCA.iloc[:3749528,:]
test=dataPCA.iloc[3749528:,:]
train.to_csv('F:/train.txt',header=None,index=False)
test.to_csv('F:/test.txt',header=None,index=False)
#下面的数据用于本地调试
offset=data.shape[0]-300000
localtrain=data.iloc[:offset,:]
labels=localtrain['label']
localtest=data.iloc[offset:,:]
act=localtest['label'].copy().values
localtest['label']=-1
testlabels=localtest['label']
'''
下面是特征选择部分
'''
params={
'booster':'gbtree',
'objective':'binary:logistic', 
'gamma':0.05,  # 在树的叶子节点下一个分区的最小损失，越大算法模型越保守 。[0:]
'max_depth':6, # 构建树的深度 [1:]
#'lambda':450,  # L2 正则项权重
'subsample':0.7, # 采样训练数据，设置为0.5，随机选择一般的数据实例 (0:1]
'colsample_bytree':0.7, # 构建树树时的采样比率 (0:1]
'min_child_weight':100, # 节点的最少特征数
'silent':1 ,
'eta': 0.02, # 如同学习率
'seed':710,
'nthread':4,# cpu 线程数,根据自己U的个数适当调整
'eval_metric':'mlogloss',
'max_delta_step':1 
}
xgtrain = xgb.DMatrix(data, label=labels)
xgtest= xgb.DMatrix(testdata, label=testlabels)
num_rounds=200
model = xgb.train(params, xgtrain, num_rounds)
preds = model.predict(xgtest)
'''
将特征选择之后的数据写入txt文件
然后使用FM模型训练、预测
'''

testdata.to_csv('E:/pre/test.txt',header=None,index=False)
def logloss(act, pred):
  epsilon = 1e-15
  pred = sp.maximum(epsilon, pred)
  pred = sp.minimum(1-epsilon, pred)
  ll = sum(act*sp.log(pred) + sp.subtract(1,act)*sp.log(sp.subtract(1,pred)))
  ll = ll * -1.0/len(act)
  return ll
'''
使用FM模型得到预测结果之后提交文件
'''
prediction=pd.read_csv('E:/pre/predict.libfm',names=['prob'])
p=prediction['prob'].values    
df=DataFrame({'instanceId':instanceId,'prob': p})
df.to_csv('E:/pre/submission.csv',index=False)





