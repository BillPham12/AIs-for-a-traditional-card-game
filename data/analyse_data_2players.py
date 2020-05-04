import pymongo
import json
from matplotlib import pyplot as plt
import pandas as pd
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["test"]
mycol = mydb["test"]

myqueries = [{"testType":"2players" ,"first_name": "Aggressive Player","second_name": "Patient Player"},
{"testType":"2players" ,"first_name": "Rational Player","second_name": "P_Rational Player"},
{"testType":"2players" ,"first_name": "Aggressive Player","second_name": "Rational Player"},
{"testType":"2players" ,"first_name": "Patient Player","second_name": "P_Rational Player"}]

table = []
for query in myqueries:
    data = mycol.find(query)
    table.append([])
    for x in data:
       table[len(table)-1].append(x['point'])

plt.figure(1)
list = [i for i in range(1,10001)]
a = []
b = []
i = 0
for x in table[0]:
        a.append(x[0])
        b.append(x[3])
fig1 = plt.plot(list,a,color = "red",label = "Aggressive Player's wins")
fig1 = plt.plot(list,b,color = "green",label = "Patient Player's wins")
plt.legend()
plt.title("The result of AggresivePlayer VS PatientPlayer")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of winning games', fontsize=15)
df = pd.DataFrame({'Name': pd.Series(["Aggressive Player", "Patient Player"]),
                'AggressivePlayer': pd.Series(a),
                'PatientPlayer': pd.Series(b)})
print("The result of AggresivePlayer VS PatientPlayer")
print(df.describe(),"\n\n")

plt.figure(2)
a = []
p = []
i = 0
for x in table[1]:
        a.append(x[0])
        p.append(x[3])
fig2 = plt.plot(list,a,color = "blue",label = "Rational Player's point")
fig2 = plt.plot(list,p,color = "Turquoise",label = "P_Rational Player's point")
plt.legend()
plt.title("The result of RationalPlayer VS P_RationalPlayer")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of winning games', fontsize=15)
df = pd.DataFrame({'Name': pd.Series(["Rational Player", "P_Rational Player"]),
                'RationalPlayer': pd.Series(a),
                'P_RationalPlayer': pd.Series(p)})
print("The result of RationalPlayer VS P_RationalPlayer")
print(df.describe(),"\n\n")



plt.figure(3)
a = []
p = []
i = 0
for x in table[2]:
        a.append(x[0])
        p.append(x[3])
fig3 = plt.plot(list,a,color = "red",label = "Aggressive Player's point")
fig3 = plt.plot(list,p,color = "blue",label = "Rational Player's point")
plt.legend()
plt.title("The result of AggressivePlayer VS RationalPlayer")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of winning games', fontsize=15)
df = pd.DataFrame({'Name': pd.Series(["Aggressive Player", "Rational Player"]),
                'AggressivePlayer': pd.Series(a),
                'RationalPlayer': pd.Series(p)})
print("The result of AggressivePlayer VS RationalPlayer")
print(df.describe(),"\n\n")

plt.figure(4)
a = []
p = []
i = 0
for x in table[3]:
        a.append(x[0])
        p.append(x[3])
fig4 = plt.plot(list,a,color = "green",label = "Patient Player's point")
fig4 = plt.plot(list,p,color = "Turquoise",label = "P_Rational Player's point")
plt.legend()
plt.title("The result of PatientPlayer VS P_RationalPlayer")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of winning games', fontsize=15)
df = pd.DataFrame({'Name': pd.Series(["Patient Player", "P_Rational Player"]),
                'PatientPlayer': pd.Series(a),
                'P_RationalPlayer': pd.Series(p)})
print("The result of PatientPlayer VS P_RationalPlayer")
print(df.describe(),"\n\n")

plt.show()
