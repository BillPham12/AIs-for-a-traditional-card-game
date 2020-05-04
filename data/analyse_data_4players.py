import pymongo
import json
from matplotlib import pyplot as plt
import pandas as pd
import numpy as np
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["test"]
mycol = mydb["test"]

myqueries = [{"testType":"4players"}]

table = [[],[],[],[]]
for query in myqueries:
    data = mycol.find(query)
    table.append([])
    for x in data:
        table[0].append(x['first_point'])
        table[1].append(x['second_point'])
        table[2].append(x['third_point'])
        table[3].append(x['forth_point'])


def record(list):
    return [list[9999][0],list[9999][1],list[9999][2],list[9999][3]]


def calculatePoint(x):
    output = []
    for i in range (0,10000):
        output.append(x[i][0]*2 + x[i][1] - x[i][2] - x[i][3]*2)
    return output


def report(data,names):
    df = pd.DataFrame({'Name': pd.Series(names),
                    names[0]: pd.Series(data[0]),
                    names[1]: pd.Series(data[1]),
                    names[2]: pd.Series(data[2]),
                    names[3]: pd.Series(data[3])
                    })
    print(df.describe(),"\n\n")

results = [record(table[0]),record(table[1]),record(table[2]),record(table[3])]
points = [calculatePoint(table[0]),calculatePoint(table[1]),calculatePoint(table[2]),calculatePoint(table[3])]

plt.figure(1)
x = np.arange(4)
width= 0.2
print(results)
fig1 = plt.bar(x+0,results[0],color = "red", width = width,label = "Aggressive Player's ranks")
fig1 = plt.bar(x+width,results[1],color = "green",width = width,label = "Patient Player's ranks")
fig1 = plt.bar(x+2*width,results[2],color = "blue",width = width,label = "Rational Player's ranks")
fig1 = plt.bar(x+3*width,results[3],color = "Turquoise",width = width,label = "P_Rational Player's ranks")
plt.xticks(x+width,("1st rank","2nd rank","3rd rank","4th rank"))
plt.title("The result of Aggresive Player, Patient Player, Rational Player, P_Rational")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of Winning games', fontsize=15)
plt.legend()

plt.figure(2)
list = [i for i in range(1,10001)]
fig2 = plt.plot(list,points[0],color = "red",label = "Aggressive Player's points")

fig2 = plt.plot(list,points[1],color = "green",label = "Patient Player's points")

fig2 = plt.plot(list,points[2],color = "blue",label = "Rational Player's points")

fig2 = plt.plot(list,points[3],color = "Turquoise",label = "P_Rational Player's points")

plt.title("The result of Aggresive Player, Patient Player, Rational Player, P_Rational Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('POINTS', fontsize=15)
plt.legend()
names = ["Aggresive Player","Patient Player","Rational Player", "P_Rational Player"]
data = [points[0],points[1],points[2], points[3]]
report(data,names)



plt.show()
