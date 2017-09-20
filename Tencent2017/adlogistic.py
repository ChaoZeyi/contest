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
from sklearn.ensemble import RandomForestClassifier
from collections import Counter
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import TensorFlow
#读取训练集
train=pd.read_csv('F:/github/contest/tencent/pre/train.csv')
#测试数据集
test=pd.read_csv('F:/github/contest/tencent/pre/test.csv')
instanceId=test['instanceID'].values
#读取广告特征
ad=pd.read_csv('F:/github/contest/tencent/pre/ad.csv')
#读取用户特征
user=pd.read_csv('F:/github/contest/tencent/pre/user.csv')
#读取广告目录
catagory=pd.read_csv('F:/github/contest/tencent/pre/app_categories.csv')
pos=pd.read_csv('F:/github/contest/tencent/pre/position.csv')
#user_app_actions=pd.read_csv('F:/github/contest/tencent/pre/user_app_actions.csv')
#user_installedapps=pd.read_csv('F:/github/contest/tencent/pre/user_installedapps.csv')
#将用户特征和广告特征合并到训练数据集
'''
对训练数据的处理
'''

print user

data=pd.merge(pd.merge(train,ad),user)


data=pd.merge(data,catagory)
data=pd.merge(data,pos)
data=data.drop(['conversionTime'],axis=1)
y_train = data["label"].values
y_train
data
'''
对clickTime进行处理
'''
clicktime=data['clickTime'].values
clicktime=clicktime/100
clicktime=clicktime.astype(int)
hour=clicktime%100
day=(clicktime-hour)/100
day=day.astype(int)
data['day']=day
data['hour']=hour
data,testdata,y_train,y_test= train_test_split(data, y_train, test_size=0.2)
'''
对测试数据的处理
'''
testdata=pd.merge(pd.merge(test,ad),user)
testdata=pd.merge(testdata,catagory)
testdata=pd.merge(testdata,pos)
testdata=testdata.sort_index(by='instanceID')
testdata=testdata.drop(['instanceID'],axis=1)
clicktime=testdata['clickTime'].values
clicktime=clicktime/100
clicktime=clicktime.astype(int)
hour=clicktime%100
day=(clicktime-hour)/100
day=day.astype(int)
testdata['day']=day
testdata['hour']=hour
# feature engineering/encoding
enc = OneHotEncoder()
feats = ['day','hour','adID','positionID','age','gender','connectionType','advertiserID']
feats = ['label','hour','adID','positionID','age','gender','advertiserID','appCategory','connectionType','appID','campaignID']
for i,feat in enumerate(feats):
    x_train = enc.fit_transform(data[feat].values.reshape(-1, 1))
    x_test = enc.transform(testdata[feat].values.reshape(-1, 1))
    if i == 0:
        X_train, X_test = x_train, x_test
    else:
        X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
def testfeats(feats):
    for i,feat in enumerate(feats):
        x_train = enc.fit_transform(data[feat].values.reshape(-1, 1))
        x_test = enc.transform(testdata[feat].values.reshape(-1, 1))
        if i == 0:
            X_train, X_test = x_train, x_test
        else:
            X_train, X_test = sparse.hstack((X_train, x_train)), sparse.hstack((X_test, x_test))
    lr = LogisticRegression()
    lr.fit(X_train, y_train)
    proba_test = lr.predict_proba(X_test)[:,1]
    print(logloss(y_test, proba_test))
# model training
def logloss(act, pred):
  epsilon = 1e-15
  pred = sp.maximum(epsilon, pred)
  pred = sp.minimum(1-epsilon, pred)
  ll = sum(act*sp.log(pred) + sp.subtract(1,act)*sp.log(sp.subtract(1,pred)))
  ll = ll * -1.0/len(act)
  return ll
lr = LogisticRegression()
lr.fit(X_train, y_train)
proba_test = lr.predict_proba(X_test)[:,1]
df=DataFrame({'instanceId':instanceId,'prob': proba_test})
df.to_csv('E:/pre/submission.csv',index=False)
