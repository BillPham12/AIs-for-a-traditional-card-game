import pymongo
import json
from matplotlib import pyplot as plt
import pandas as pd
import numpy as np
myclient = pymongo.MongoClient("mongodb://localhost:27017/")
mydb = myclient["test"]
mycol = mydb["test"]

myqueries = [{"testType":"3players" ,"first_name": "Aggressive Player","second_name":
 "Patience Player","third_name" : "Rational Player"},
{"testType":"3players" ,"first_name": "Aggressive Player","second_name":
 "Patience Player","third_name" : "P_Rational Player"},
{"testType":"3players" ,"first_name": "Rational Player","second_name":
 "P_Rational Player","third_name" : "Aggressive Player"},
{"testType":"3players" ,"first_name": "Rational Player","second_name":
 "P_Rational Player","third_name" : "Patience Player"}]

table = []
for query in myqueries:
    data = mycol.find(query)
    table.append([])
    for x in data:
        table[len(table)-1].append((x["first_point"],x["second_point"],x["third_point:"]))

def record(list):
    return [list[0][9999],list[1][9999],list[2][9999]]


def calculatePoint(x):
    output = []
    for i in range (0,10000):
        output.append(x[0][i]*3 - x[1][i] - x[2][i]*2)
    return output


def report(data,names):
    df = pd.DataFrame({'Name': pd.Series(names),
                    names[0]: pd.Series(data[0]),
                    names[1]: pd.Series(data[1]),
                    names[2]: pd.Series(data[2])})
    print(df.describe(),"\n\n")


list = [i for i in range(1,10001)]
width = 0.3
first_data = [[],[],[]]
second_data = [[],[],[]]
third_data = [[],[],[]]
for x in table[0]:
    first_data[0].append(int(x[0][0]))
    first_data[1].append(int(x[0][1]))
    first_data[2].append(int(x[0][2]))

    second_data[0].append(int(x[1][0]))
    second_data[1].append(int(x[1][1]))
    second_data[2].append(int(x[1][2]))

    third_data[0].append(int(x[2][0]))
    third_data[1].append(int(x[2][1]))
    third_data[2].append(int(x[2][2]))
results = [record(first_data),record(second_data),record(third_data)]
points = [calculatePoint(first_data),calculatePoint(second_data),calculatePoint(third_data)]

plt.figure(1)
x = np.arange(3)
print(results)
fig1 = plt.bar(x+0,results[0],color = "red", width = width,label = "Aggressive Player's ranks")
fig1 = plt.bar(x+width,results[1],color = "green",width = width,label = "Patient Player's ranks")
fig1 = plt.bar(x+2*width,results[2],color = "blue",width = width,label = "Rational Player's ranks")
plt.xticks(x+width,("1st rank","2nd rank","3rd rank"))
plt.title("The result of Aggresive Player, Patient Player, Rational Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of Winning games', fontsize=15)
plt.legend()

plt.figure(2)
fig2 = plt.plot(list,points[0],color = "red",label = "Aggressive Player's points")

fig2 = plt.plot(list,points[1],color = "green",label = "Patient Player's points")

fig2 = plt.plot(list,points[2],color = "blue",label = "Rational Player's points")

plt.title("The result of Aggresive Player, Patient Player, Rational Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('POINTS', fontsize=15)
plt.legend()
names = ["Aggresive Player","Patient Player","Rational Player"]
data = [points[0],points[1],points[2]]
report(data,names)

first_data = [[],[],[]]
second_data = [[],[],[]]
third_data = [[],[],[]]
for x in table[1]:
    first_data[0].append(int(x[0][0]))
    first_data[1].append(int(x[0][1]))
    first_data[2].append(int(x[0][2]))

    second_data[0].append(int(x[1][0]))
    second_data[1].append(int(x[1][1]))
    second_data[2].append(int(x[1][2]))

    third_data[0].append(int(x[2][0]))
    third_data[1].append(int(x[2][1]))
    third_data[2].append(int(x[2][2]))
results = [record(first_data),record(second_data),record(third_data)]
points = [calculatePoint(first_data),calculatePoint(second_data),calculatePoint(third_data)]
print(results)
plt.figure(3)
x = np.arange(3)
fig3 = plt.bar(x+0,results[0],color = "red", width = width,label = "Aggressive Player's ranks")
fig3 = plt.bar(x+width,results[1],color = "green",width = width,label = "Patient Player's ranks")
fig3 = plt.bar(x+2*width,results[2],color = "turquoise",width = width,label = "P_Rational Player's ranks")
plt.xticks(x+width,("1st rank","2nd rank","3rd rank"))
plt.title("The result of Aggresive Player, Patient Player, P_Rational Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of Winning games', fontsize=15)
plt.legend()

plt.figure(4)
fig4 = plt.plot(list,points[0],color = "red",label = "Aggressive Player's points")

fig4 = plt.plot(list,points[1],color = "green",label = "Patient Player's points")

fig4 = plt.plot(list,points[2],color = "turquoise",label = "P_Rational Player's points")

plt.title("The result of Aggresive Player, Patient Player, P_Rational Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('POINTS', fontsize=15)
plt.legend()
names = ["Aggresive Player","Patient Player","P_Rational Player"]
data = [points[0],points[1],points[2]]
report(data,names)


first_data = [[],[],[]]
second_data = [[],[],[]]
third_data = [[],[],[]]
for x in table[2]:
    first_data[0].append(int(x[0][0]))
    first_data[1].append(int(x[0][1]))
    first_data[2].append(int(x[0][2]))

    second_data[0].append(int(x[1][0]))
    second_data[1].append(int(x[1][1]))
    second_data[2].append(int(x[1][2]))

    third_data[0].append(int(x[2][0]))
    third_data[1].append(int(x[2][1]))
    third_data[2].append(int(x[2][2]))
results = [record(first_data),record(second_data),record(third_data)]
points = [calculatePoint(first_data),calculatePoint(second_data),calculatePoint(third_data)]
print(results)
plt.figure(5)
x = np.arange(3)
fig5 = plt.bar(x+0,results[0],color = "blue", width = width,label = "Rational Player's ranks")
fig5 = plt.bar(x+width,results[1],color = "turquoise",width = width,label = "P_Rational Player's ranks")
fig5 = plt.bar(x+2*width,results[2],color = "red",width = width,label = "Aggressive Player's ranks")
plt.xticks(x+width,("1st rank","2nd rank","3rd rank"))
plt.title("The result of Rational Player, P_Rational Player, Aggressive Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of Winning games', fontsize=15)
plt.legend()

plt.figure(6)
fig6 = plt.plot(list,points[0],color = "blue",label = "Rational Player's points")

fig6 = plt.plot(list,points[1],color = "turquoise",label = "P_Rational Player's points")

fig6 = plt.plot(list,points[2],color = "red",label = "Aggressive Player's points")

plt.title("The result of Rational Player, P_Rational Player, Aggressive Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('POINTS', fontsize=15)
plt.legend()
names = ["Rational Player","P_Rational Player","Aggressive Player"]
data = [points[0],points[1],points[2]]
report(data,names)




first_data = [[],[],[]]
second_data = [[],[],[]]
third_data = [[],[],[]]
for x in table[3]:
    first_data[0].append(int(x[0][0]))
    first_data[1].append(int(x[0][1]))
    first_data[2].append(int(x[0][2]))

    second_data[0].append(int(x[1][0]))
    second_data[1].append(int(x[1][1]))
    second_data[2].append(int(x[1][2]))

    third_data[0].append(int(x[2][0]))
    third_data[1].append(int(x[2][1]))
    third_data[2].append(int(x[2][2]))
results = [record(first_data),record(second_data),record(third_data)]
points = [calculatePoint(first_data),calculatePoint(second_data),calculatePoint(third_data)]
print(results)
plt.figure(7)
x = np.arange(3)
fig7 = plt.bar(x+0,results[0],color = "blue", width = width,label = "Rational Player's ranks")
fig7 = plt.bar(x+width,results[1],color = "turquoise",width = width,label = "P_Rational Player's ranks")
fig7 = plt.bar(x+2*width,results[2],color = "green",width = width,label = "Patient Player's ranks")
plt.xticks(x+width,("1st rank","2nd rank","3rd rank"))
plt.title("The result of Rational Player, P_Rational Player, Patient Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('Number of Winning games', fontsize=15)
plt.legend()

plt.figure(8)
fig8 = plt.plot(list,points[0],color = "blue",label = "Rational Player's points")

fig8 = plt.plot(list,points[1],color = "turquoise",label = "P_Rational Player's points")

fig8 = plt.plot(list,points[2],color = "green",label = "Patient Player's points")

plt.title("The result of Rational Player, P_Rational Player, Patient Player")
plt.xlabel('Number of games', fontsize=15)
plt.ylabel('POINTS', fontsize=15)
plt.legend()
names = ["Rational Player","P_Rational Player","Patient Player"]
data = [points[0],points[1],points[2]]
report(data,names)
plt.show()
