*author: Bill Pham

Requirements for the project:
project SDK: 1.8.0_112/ 11.0.5 or higher
Python: 3.6.8 or higher
Maven compiler: 2.3.2 or higher
MongoDB: 2.13.3 or higher


- To run the game and analyze files, you must install Mongodb:
  + First, you need to find the appropriate version of mongoDB which is suitable for your current OS.
   https://www.mongodb.com/download-center/community?jmp=docs
  + Then, you must create a database directory in your computer.
    the folder database directory must be: "C:\data\db"

  + To host a localhost for mongodb: You will need a terminal to run these following commands:
    host server: "C:\Program Files\MongoDB\Server\4.2\bin\mongod.exe" --dbpath="c:\data\db"
- Next is install the software development IntelliJ IDEA:
  https://www.jetbrains.com/idea/download/#section=windows (this is optional if you want to
  run the project in IntelliJ)
    + if you choose this option, you just need to install the software then import the project and set up SDK
    + I strongly recommend you to follow this strategy to avoid messy complied classes in the project.
    + for more information please take a look on the video I made.
- Otherwise, you have to compile server and robots in command line and there will 6 command lines in your screen:
  (1 database server,1 server, 3 robots and 1 for user interface)

- the order to run programs in the project:
1) host database server
2) run server program
3) robots (order doesn't matter: aggressive robot, rational robot, p_rational robot)
 + I ignore the patient robot because it is the loser.
4) GUI.

- You would be able to play the game of 2 players or 3players, but you have to modify code
in the server:
  + line 89 in the server: num_clients = number of players
  + then modify ports for serverSocket, which must be matched with robots and order is matter.
  + For example: if you want to player the game of 2 players you and aggresive robot:
      - First, let's set the name of player from line 105 -- 108 correctly (AGGRESSION, HUMAN).
      - Second, modify the port in the side of robot and user interface: Aggressive robot port = 1234
       user interface port = 2345.
- By default, the code modified in the submission is for four players:
  + User, aggressive robot, rational robot, and patient & rational robot.


To re-generate graphs in the report by python programs:
You must run the following command to import data to mongoDB server:

--you must run in ~\mongodb\bin to import data in the mongodb server
  mongoimport --db test --collection test --file (where the data file is stored)~\data.json

then you just need to run:
python analyse_data_2players.py
python analyse_data_3players.py
python analyse_data_4players.py
(make sure that python is installed in your computer)
