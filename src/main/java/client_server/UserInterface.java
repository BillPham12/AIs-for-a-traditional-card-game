package client_server;

import com.mongodb.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.application.Application;

import javax.swing.filechooser.FileSystemView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import cryptography.EncryptSystem;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.*;
import game.Meld;

import static client_server.Client.secure_system;

public class UserInterface extends Application{
    private static TextField username;
    private static MongoClient mongoClient;
    private static PasswordField password;
    private static TextField email;
    private static  GridPane grid;
    private static Pane aPane;

    private static Alert alert;
    private static Dialog dialog;
    private static int attempt = 3;
    private static int height, width;
    private static int serverPort = 2345;
    private static Meld check;
    private static Thread sendMessage,readMessage;
    private static InetAddress ip;

    private static HBox main_scene;
    private static TextArea console;
    private static ArrayList<Text> enemies;
    private static ArrayList<Text> points;

    // establish the connection
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static List<String> table;
    private static String name;
    private static Human player;
    private static int x_point, y_point;
    private static List<String> chosenCard;
    private static int display_x = 500;
    private static int display_y = 250;
    private static List<String> rank;

    // invalid move alert
    private static void invalidMove(){
        alert = new Alert(Alert.AlertType.ERROR);
        Stage s = (Stage) alert.getDialogPane().getScene() .getWindow();
        s.getIcons().add(getIcon());
        alert.setTitle("Wrong move");
        alert.setHeaderText(null);
        alert.setContentText("You have made a wrong move!\n" +
                "the number of attempt left is " + attempt);
        alert.showAndWait();
    }

    // Login --> Register
    private static void login(Stage primaryStage){
        table = new ArrayList<>();
        grid = new GridPane();
        // create TextField
        username = new TextField();
        username.setPromptText("Username");
        // create password Field
        password = new PasswordField();
        password.setPromptText("Password");
        // add these 2 variable into the grid
        grid.add(new Label("Username"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("Password"),0,1);
        grid.add(password,1,1);
        // set grid into dialog
        dialog = new Dialog();
        // favicon
        Stage s = (Stage) dialog.getDialogPane().getScene() .getWindow();
        s.getIcons().add(getIcon());

        dialog.setTitle("Log in");
        dialog.setHeaderText(null);
        // Log in button
        ButtonType loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);

        // Log in button
        ButtonType forget = new ButtonType("Help!", ButtonBar.ButtonData.HELP);

        dialog.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL,forget);
        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();
        // If the result if Ok, then check for validation
        if (result.isPresent() && result.toString().contains("OK_DONE")
                && !username.getText().equals("") && !password.getText().equals("")){

            // If valid then process further
            if (authenticate()){
                alert = new Alert(Alert.AlertType.INFORMATION);
                name = username.getText();
                // establish the connection
                try {
                    socket = new Socket(ip, serverPort);

                    // obtaining input and out streams
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // favicon
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());

                alert.setTitle("INFORMATION");
                alert.setHeaderText(null);
                alert.setContentText("Successfully log in");
                alert.showAndWait();
                // then activate 2 threads

                readMessage.start();
                // start the game
                play(primaryStage);
            }
            else{
                alert = new Alert(Alert.AlertType.INFORMATION);
                // favicon
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());
                alert.setTitle("INFORMATION");
                alert.setHeaderText(null);
                alert.setContentText("Your username or password is incorrect, please re-enter your account");
                alert.showAndWait();
                // recall register function
                login(primaryStage);
            }
        }
        // cancel then just do nothing
        else if(result.toString().contains("CANCEL")){}
        else if (result.toString().contains("HELP")){
            recoverPassword(primaryStage);
        }
        else {
            alert = new Alert(Alert.AlertType.WARNING);
            // favicon
            s = (Stage) alert.getDialogPane().getScene() .getWindow();
            s.getIcons().add(getIcon());
            alert.setTitle("WARNING");
            alert.setHeaderText(null);
            alert.setContentText("Your username or password is incorrect, please re-enter your account");
            alert.showAndWait();
            // re call the function
            login(primaryStage);
        }
        username.setText("");
        password.setText("");
    }

    private static void recoverPassword(Stage primaryStage) {
        table = new ArrayList<>();
        grid = new GridPane();
        // create TextField
        username = new TextField();
        username.setPromptText("Enter your userID");
        // create password Field
        email = new TextField();
        email.setPromptText("Enter your email");
        // add these 2 variable into the grid
        grid.add(new Label("Username"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("Email"),0,1);
        grid.add(email,1,1);
        // set grid into dialog
        dialog = new Dialog();
        // favicon
        Stage s = (Stage) dialog.getDialogPane().getScene() .getWindow();
        s.getIcons().add(getIcon());

        dialog.setTitle("Recover Password");
        dialog.setHeaderText(null);
        // Log in button
        ButtonType loginButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();
        // If the result if Ok, then check for validation
        if (result.isPresent() && result.toString().contains("OK_DONE")
                && !username.getText().equals("") && !email.getText().equals("")){
            System.out.println(username.getText() + "---" + email.getText());
            // If valid then process further
            if (canFind()){
                alert = new Alert(Alert.AlertType.INFORMATION);
                // favicon
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());

                alert.setTitle("INFORMATION");
                alert.setHeaderText(null);
                alert.setContentText("Here is your password " + password.getText());
                alert.showAndWait();
                username.setText(""); email.setText("");password.setText("");
                // come back to log in stage
                login(primaryStage);
            }
            else{
                alert = new Alert(Alert.AlertType.INFORMATION);
                // favicon
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());
                alert.setTitle("INFORMATION");
                alert.setHeaderText(null);
                alert.setContentText("Please check your user name or your email");
                alert.showAndWait();
                // recall recover Password
                recoverPassword(primaryStage);
            }
        }
        // cancel then just do nothing
        else if(result.toString().contains("CANCEL")){}
        else {
            alert = new Alert(Alert.AlertType.WARNING);
            // favicon
            s = (Stage) alert.getDialogPane().getScene() .getWindow();
            s.getIcons().add(getIcon());
            alert.setTitle("WARNING");
            alert.setHeaderText(null);
            alert.setContentText("You have entered invalid userID or email \n." +
                    "Please check your user name or your email");
            alert.showAndWait();
            // re call the function
            recoverPassword(primaryStage);
        }
        username.setText("");
        email.setText("");
    }

    // `Register` -->
    private static void register(Stage primaryStage){
        grid = new GridPane();
        // create TextField
        username = new TextField();
        username.setPromptText("Enter your username");
        // create TextField for email
        email = new TextField();
        email.setPromptText("Enter your email");
        // create password Field
        password = new PasswordField();
        password.setPromptText("Enter your password");
        // add these 3 variable into the grid
        grid.add(new Label("Username"),0,0);
        grid.add(username,1,0);
        grid.add(new Label("Password"),0,1);
        grid.add(password,1,1);
        grid.add(new Label("Email"),0,2);
        grid.add(email,1,2);

        // set grid into dialog
        dialog = new Dialog();
        // favicon
        Stage s = (Stage) dialog.getDialogPane().getScene() .getWindow();
        s.getIcons().add(getIcon());
        dialog.setTitle("Registration");
        dialog.setHeaderText(null);
        // Log in button
        ButtonType loginButton = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        Optional result = dialog.showAndWait();
        // if successfully register then write the account in mongodb databases
        if (result.isPresent() && !username.getText().equals("") &&
                !password.getText().equals("") && !email.getText().equals("") && email.getText().contains("@")
                && email.getText().contains(".")){
            // register for the account
            if(registerAccount()){
                // re new 2 text fields
                username.setText("");
                password.setText("");
                email.setText("");

                alert = new Alert(Alert.AlertType.WARNING);
                // favicon
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());
                alert.setTitle("WARNING");
                alert.setHeaderText(null);
                alert.setContentText("Successfully register account");
                alert.showAndWait();
                // comeback to login
                login(primaryStage);
            }
            else{alert = new Alert(Alert.AlertType.WARNING);
                s = (Stage) alert.getDialogPane().getScene() .getWindow();
                s.getIcons().add(getIcon());
                alert.setTitle("WARNING");
                alert.setHeaderText(null);
                alert.setContentText("The username has been used by somebody else.\n"
                        + "Please choose another one!");
                alert.showAndWait();
                register(primaryStage);}
        }
        // if users hit cancel. There is nothing to do nothing
        else if (result.isPresent() && result.toString().contains("CANCEL")){}
        else {
            alert = new Alert(Alert.AlertType.WARNING);
            s = (Stage) alert.getDialogPane().getScene() .getWindow();
            s.getIcons().add(getIcon());
            alert.setTitle("WARNING");
            alert.setHeaderText(null);
            alert.setContentText("You have enter an invalid username, password or " +
                    "email !\n Please re-register!!!");
            alert.showAndWait();
            register(primaryStage);
        }
    }

    // authenticate to server
    private static boolean registerAccount(){
        try {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            mongoClient = new MongoClient();
            //data base name
            DB database = mongoClient.getDB("test");
            // data base collections
            DBCollection collection = database.getCollection("users");

            // in case the account is already exist
            BasicDBObject andQuery = new BasicDBObject();
            BasicDBObject object = new BasicDBObject();
            object.put("userID",username.getText());
            Iterator<DBObject> cursor = collection.find(andQuery,object);
            if(cursor.hasNext())
                return false;


            // Otherwise insert data
            BasicDBObject data = new BasicDBObject();
            data.append("userId", username.getText());
            data.append("userPassword", password.getText());
            data.append("email", email.getText());
            collection.insert(data);
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    // authenticate to server
    private static boolean authenticate(){
        try {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            mongoClient = new MongoClient();
            //data base name
            DB database = mongoClient.getDB("test");
            // data base collections
            DBCollection collection = database.getCollection("users");

            // query
            BasicDBObject andQuery = new BasicDBObject();
            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
            obj.add(new BasicDBObject("userId", username.getText()));
            obj.add(new BasicDBObject("userPassword", password.getText()));
            andQuery.put("$and", obj);
            Iterator<DBObject> cursor = collection.find(andQuery);
            if(cursor.hasNext())
                return true;
            else
                return false;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }
    // recoverpassword
    // authenticate to server
    private static boolean canFind(){
        try {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
            mongoClient = new MongoClient();
            //data base name
            DB database = mongoClient.getDB("test");
            // data base collections
            DBCollection collection = database.getCollection("users");

            // query
            BasicDBObject andQuery = new BasicDBObject();
            List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
            obj.add(new BasicDBObject("userId", username.getText()));
            obj.add(new BasicDBObject("email", email.getText()));
            andQuery.put("$and", obj);
            Iterator<DBObject> cursor = collection.find(andQuery);
            if(cursor.hasNext()){
                DBObject db = cursor.next();
                password.setText((String) db.get("userPassword"));
                return true;
            }
            else
                return false;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }



    // get Carleton icon
    private static Image getIcon(){
        Image image = null;
        try {
            image = new Image(new FileInputStream(getSource() + "/icon.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    // get source
    private static String getSource(){
        String url = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        url += "\\project\\image";
        url = url.replace("\\","/");
        return url;
    }

    private static ImageView getImage(String img_name){
        Image image = null;
        img_name = "/" + img_name;
        try {
            image = new Image(new FileInputStream(getSource() + img_name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView imageview = new ImageView(image);
        return imageview;
    }


    // display player hand in GUI
    public static void displayPlayerHand(){
        int ran = 30;
        int size = player.getHand().size(); int middle = 500;
        int X= middle - ran*size/2;
        // display player hand
        for (String card: player.getHand()){
            // convert
            if(card.charAt(0) == 'X')
                card =card.replace("X","10");
            else if (card.charAt(0) == 'L')
                card = card.replace("L","2");

            card += ".jpg";

            ImageView imageview = getImage(card);
            // set Id to the card so that we would be able to remove it
            imageview.setId(card);

            // card width
            imageview.setFitHeight(110);
            imageview.setFitWidth(70);
            // set location for X
            imageview.setX(X);

            X = X + ran;
            imageview.setY(490);
            // set mouse click
            imageview.setOnMouseClicked(ev -> {
                if (imageview.getY() == 470){
                    // remove current card to the chosen meld
                    chosenCard.remove(imageview.getId());
                    imageview.setY(490);}
                else {
                    // add current card to the chosen meld
                    chosenCard.add(imageview.getId());
                    imageview.setY(470);}
            });
            aPane.getChildren().addAll(imageview);
        }
        upgradeTurn();
    }

    // display card played by others
    public static void displayCardPlayedByOthers(String str){
        System.out.println("Others played " + str);
        List<String> l = new ArrayList<>();
        while(str.length() > 0){
            l.add(str.substring(0,2));
            str = str.substring(2);
        }

        if (display_x == 500) display_x = 480;
        else display_x = 500;

        if (display_y == 250) display_y = 270;
        else display_y = 250;


        int x = display_x,y = display_y, ran = 30;


        // chosen the x index to display card
        x = x - l.size()*30/2;
        // display player hand
        for (String card: l){
            // convert
            if(card.charAt(0) == 'X')
                card =card.replace("X","10");
            else if (card.charAt(0) == 'L')
                card = card.replace("L","2");

            card += ".jpg";
            ImageView imageview = getImage(card);
            // set Id to the card so that we would be able to remove it
            imageview.setId(card);

            // card width
            imageview.setFitHeight(110);
            imageview.setFitWidth(70);
            // set location for X
            imageview.setX(x);

            x= x + ran;
            imageview.setY(y);
            aPane.getChildren().addAll(imageview);
        }
    }

    //clear the table
    public static void clearTable(){
        for (int i =0; i < aPane.getChildren().size();i++){
            if (aPane.getChildren().get(i).getId() != null){
                if(aPane.getChildren().get(i).getId().contains(".jpg") ||
                        aPane.getChildren().get(i).getId().contains("back")){
                    aPane.getChildren().remove(i);
                    i--;
                }
            }
        }
    }

    //clear the table
    public static void upsideDown(){
        ArrayList<String> player_hand = new ArrayList<>();
        for (String card: player.getHand()) {
            // convert
            if (card.charAt(0) == 'X')
                card = card.replace("X", "10");
            else if (card.charAt(0) == 'L')
                card = card.replace("L", "2");
            card += ".jpg";
            player_hand.add(card);
        }
        for (int i =0; i < aPane.getChildren().size();i++){
            if (aPane.getChildren().get(i).getId() != null){
                if(aPane.getChildren().get(i).getId().contains(".jpg") &&
                        !player_hand.contains(aPane.getChildren().get(i).getId())){
                    ImageView imageview = getImage("back.jpg");
                    imageview.setFitHeight(110);
                    imageview.setFitWidth(70);
                    imageview.setId("back");
                    imageview.setX(aPane.getChildren().get(i).getLayoutX());
                    double x = ((ImageView) (aPane.getChildren().get(i))).getX();
                    double y = ((ImageView) (aPane.getChildren().get(i))).getY();
                    imageview.setX(x); imageview.setY(y);
                    aPane.getChildren().set(i,imageview);
                    i--;
                }
            }
        }
    }

    //  begin the game
    // it contains menu times, login and registration
    public static void begin(Stage primaryStage){
        aPane = new Pane();
        width = 1000;
        height = 600;
        // Welcome Screen:
        ImageView welcome = getImage("aces.jpg");
        welcome.setFitWidth(width); welcome.setFitHeight(height);
        welcome.setId("welcome");

        // menu buttons
        MenuItem menuItem1 = new MenuItem("Game's rule");
        // menu buttons
        MenuItem menuItem2 = new MenuItem("User Interface Guideline");

        // set action log in
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                String url = "file:///" +  FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
                url += "\\project\\GameRule.html";
                webEngine.load(url);
                url = url.replace("\\","/");

                Button back  = new Button("Get Back");
                back.prefWidth(100);back.prefHeight(700);
                back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        begin(primaryStage);}});
                VBox box = new VBox();
                box.setPadding(new Insets(2));
                box.setSpacing(2);

                box.getChildren().addAll(webView,back);
                Scene scene = new Scene(box,1200,700);
                primaryStage.setScene(scene);
            }});

        // set action log in
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WebView webView = new WebView();
                WebEngine webEngine = webView.getEngine();
                String url = "file:///" +  FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
                url += "\\project\\MyGui.html";
                webEngine.load(url);
                url = url.replace("\\","/");

                Button back  = new Button("Get Back");
                back.prefWidth(100);back.prefHeight(700);
                back.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        begin(primaryStage);}});
                VBox box = new VBox();
                box.setPadding(new Insets(2));
                box.setSpacing(2);

                box.getChildren().addAll(webView,back);
                Scene scene = new Scene(box,1200,700);
                primaryStage.setScene(scene);
            }});

        MenuButton menu = new MenuButton("Hints");
        menu.getItems().addAll(menuItem1,menuItem2);

        // start button
        Button login = new Button("Log In");
        login.setPrefSize(80,50);
        login.relocate(width/2-125,height*6/7);
        // set action log in button
        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                login(primaryStage);}});

        // start button
        Button Register = new Button("Registration");
        Register.setPrefSize(80,50);
        Register.relocate(width/2,height*6/7);
        // set action register button
        Register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                register(primaryStage);}});

        aPane.getChildren().addAll(welcome,menu,login,Register);

        main_scene = new HBox();
        main_scene.getChildren().addAll(aPane);

        primaryStage.getIcons().add(getIcon());
        primaryStage.setTitle("ClimbingUp");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(main_scene,width,height));
        primaryStage.show();
    }

    // actually start the game and display stuffs
    public static void play(Stage primaryStage) {
        // adding console
        console = new TextArea();
        console.setPrefSize(200,600);
        console.setEditable(false);
        console.setVisible(true);
        console.setWrapText(true);
        main_scene = new HBox();
        main_scene.getChildren().addAll(aPane,console);
        primaryStage.setScene(new Scene(main_scene,width+200,height));

        aPane.getChildren().clear();

        // adding background picture
        ImageView tableView = getImage("table.png");
        aPane.getChildren().addAll(tableView);

        enemies = new ArrayList<>(); points = new ArrayList<>();
        ArrayList<String> schedule = new ArrayList<>();
        int x = player.schedule.indexOf("HUMAN");
        if(x != player.schedule.size()-1){
            schedule.addAll(player.schedule.subList(x+1,player.schedule.size()));
            schedule.addAll(player.schedule.subList(0,x+1));}
        else{
            schedule.addAll(player.schedule.subList(0,x+1));}


        for (String name: schedule){
            enemies.add(new Text(name));
            points.add(new Text("0"));
        }
        int[] x_list= {0,1000/2-120/2,1000-120};
        int[] y_list = {height/2-60,0,height/2-60};

        int[] text_x_list= {0,1000/2+50,1000-140};
        int[] text_y_list = {height/2 - 130,0,height/2-130};

        int index = 0;
        if (enemies.size() == 2){
            for (int i =0; i < 2;i++){
                enemies.get(i).setFont(Font.font("verdana",FontWeight.BOLD,FontPosture.REGULAR,20));
                points.get(i).setFont(Font.font("verdana",FontWeight.BOLD,FontPosture.REGULAR,20));}
            for (int i =0; i < 2;i++){
                // human player
                if (enemies.get(i).getText().equals(player.getName())){
                    enemies.get(i).setText(name);
                    enemies.get(i).setX(200); enemies.get(i).setY(550);
                    points.get(i).setX(200); points.get(i).setY(570);
                }
                // robot
                else{
                    ImageView user = getImage("robot.png");
                    user.setFitWidth(120);user.setFitHeight(120);
                    user.setX(1000/2-120/2); user.setY(0);
                    aPane.getChildren().addAll(user);
                    enemies.get(i).setX(570); enemies.get(i).setY(30);
                    points.get(i).setX(570); points.get(i).setY(60);
                }
            }
        }
        else {
            for (int i =0; i < player.getNumber_of_player();i++){
                enemies.get(i).setFont(Font.font("verdana",FontWeight.BOLD,FontPosture.REGULAR,15));
                points.get(i).setFont(Font.font("verdana",FontWeight.BOLD,FontPosture.REGULAR,15));
                points.get(i).setFill(Color.YELLOW);
            }
            for (int i =0; i < player.getNumber_of_player();i++){
                // human player
                if (enemies.get(i).getText().equals(player.getName())){
                    enemies.get(i).setText(name);
                    enemies.get(i).setX(200); enemies.get(i).setY(550);
                    points.get(i).setX(200); points.get(i).setY(570);
                }
                // robot
                else{
                    ImageView user = getImage("robot.png");
                    user.setFitWidth(120);user.setFitHeight(120);
                    user.setX(x_list[index]); user.setY(y_list[index]);
                    aPane.getChildren().addAll(user);
                    enemies.get(i).setX(text_x_list[index]+20); enemies.get(i).setY(text_y_list[index]+30);
                    points.get(i).setX(text_x_list[index]+20); points.get(i).setY(text_y_list[index] + 60);
                    index += 1;
                }
            }
        }

        aPane.getChildren().addAll(enemies); aPane.getChildren().addAll(points);




        int button_x = 750,button_y = 530;
        Button play = new Button("Play");
        play.setPrefSize(100,70);
        play.relocate(button_x,button_y);

        Button pass = new Button("Pass");
        pass.setPrefSize(100,70);
        pass.relocate(button_x + 120,button_y);

        aPane.getChildren().addAll(play,pass);
        // play action button
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Collections.sort(chosenCard, new sort());
                String[] msg = convertToMessage(chosenCard);
                String s = "";
                for (String card : msg){
                    s += card; s += ";";}
                // send message to check for the correctness
                if(s.contains("2")){
                    s = s.replace("2","L");
                    for (int i =0; i < msg.length;i++){
                        if(msg[i].contains("2"))
                            msg[i] = msg[i].replace("2","L");
                    }
                }
                else if (s.contains("10")){
                    s = s.replace("10","X");
                    for (int i =0; i < msg.length;i++){
                        if(msg[i].contains("10"))
                            msg[i] = msg[i].replace("10","X");
                    }
                }
                String sms = "HUMAN:::" + s;
                System.out.println("you play " + s);
                // pop up alert
                if (player.turns.size() > 0 &&
                        player.turns.peek().equals(player.name) && !check.checkMeld(msg)){
                    attempt--;
                    invalidMove();
                    if(attempt == 0){
                        attempt = 3;
                        player.turns.remove(player.getName());
                        if(player.turns.size() == 1){
                            reNewTurn(player);
                            chosenCard = new ArrayList<>();
                        }
                        sms = "HUMAN:::" + " ";
                        try {
                            out.writeUTF(secure_system.encrypt(player.public_key,player.N,sms).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (player.turns.peek().equals(player.name) && check.checkMeld(msg)){
                    try {
                        out.writeUTF(secure_system.encrypt(player.public_key,player.N,sms).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    attempt = 3;
                    console.appendText("You played " + s + "\n");
                    table.addAll(chosenCard);

                    if (display_x == 500) display_x = 480;
                    else display_x = 500;

                    if (display_y == 270) display_y = 250;
                    else display_y = 270;

                    int x = display_x,y = display_y, ran = 30;
                    // chosen the x index to display card
                    x = x - chosenCard.size()*30/2;

                    // delete all image from player hand
                    for (String card: player.getHand()){
                        for (int i =0; i < aPane.getChildren().size();i++){
                            if(aPane.getChildren().get(i).getId() != null &&
                                    !aPane.getChildren().get(i).getId().equals("back")){
                                // convert
                                if(card.charAt(0) == 'X')
                                    card =card.replace("X","10");
                                else if (card.charAt(0) == 'L')
                                    card = card.replace("L","2");

                                int index = aPane.getChildren().get(i).getId().indexOf(".");
                                if(card.equals(aPane.getChildren().get(i).getId().substring(0,index))){
                                    aPane.getChildren().remove(i);i--;}
                            }
                        }
                    }
                    for (String card: chosenCard){
                        ImageView imageview = getImage(card);
                        imageview.setId(card);
                        // remove card from player hand
                        if(card.contains("2"))
                            card = card.replace("2","L");
                        else if (card.contains("10"))
                            card = card.replace("10","X");
                        int index = card.indexOf(".");
                        player.getHand().remove(card.substring(0,index));
                        // set Id to the card so that we would be able to remove it

                        // card width
                        imageview.setFitHeight(110);
                        imageview.setFitWidth(70);
                        // set location for X
                        imageview.setX(x);

                        x = x + ran;
                        imageview.setY(y);
                        aPane.getChildren().addAll(imageview);
                    }
                    chosenCard = new ArrayList<>();
                    if (player.getHand().size() == 0)
                        rank.add(player.getName());
                    // display player hand
                    displayPlayerHand();
                    check.setValue(convertToMessage(chosenCard));
                    player.turns.addLast(player.turns.poll());
                    upgradeTurn();
                    // flip all the cards in the table
                    if(player.getHand().size() == 0){
                        upsideDown();
                        rank.add(player.getName());
                    }
                }
                else{
                    attempt--;
                    invalidMove();
                    if(attempt == 0){
                        attempt = 3;
                        player.turns.remove(player.getName());
                        if(player.turns.size() == 1){
                            reNewTurn(player);
                            chosenCard = new ArrayList<>();
                        }
                    }
                }

            }
        });
        // pass action button
        pass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String[] x = convertToMessage(chosenCard);
                String s = "";
                for (String card : x)
                    s += card;
                String msg = "HUMAN::: ";
                // send message to the server
                if(player.turns.peek().equals(player.name)){
                    // remove the player from turn
                    player.turns.poll();
                    if(player.turns.size() == 1){
                        reNewTurn(player);
                        //clearTable();
                        upsideDown();
                        //  displayPlayerHand();
                        chosenCard = new ArrayList<>();
                    }
                    try {
                        out.writeUTF(secure_system.encrypt(player.public_key,player.N,msg).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // upgrade turns
                    upgradeTurn();
                }


            }
        });
    }

    public void start(Stage primaryStage){ begin(primaryStage);}

    public static void main (String[] args) throws IOException{
        //
        name = "Testing";
        rank = new ArrayList<String>();
        chosenCard = new ArrayList(); table = new ArrayList<>();
        player = new Human("HUMAN");
        check = new Meld();
        player.schedule = new ArrayList<>();
        player.turns = new ArrayDeque<>();
        check = new Meld();
        Scanner scn = new Scanner(System.in);
        secure_system = new EncryptSystem();
        // getting localhost ip
        ip = InetAddress.getByName("localhost");

        readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    try {
                        // read the message server sent to this client
                        String msg = in.readUTF();
                        // receiving the public key and N
                        if(player.num_message == 0)
                        {
                            int x = msg.indexOf(';');
                            player.N = new BigInteger(msg.substring(0,x));
                            player.public_key = new BigInteger(msg.substring(x+1));
                            player.num_message++; }
                        // receive cards
                        else if (player.num_message == 1){
                            msg = secure_system.decrypt(player.public_key,player.N, new BigInteger(msg));
                            msg = msg.substring(3);
                            while(msg.contains(";") && msg.length() > 1){
                                int x = msg.indexOf(";");
                                player.hand.add(msg.substring(0,x));
                                msg = msg.substring(x+1);
                            }
                            Collections.sort(player.hand, new sort());
                            player.num_message++;
                            // platform to handle 2 threads
                            Platform.runLater(() ->  {
                                displayPlayerHand();
                            });
                        }
                        // receive the list of players in play order
                        else if (player.num_message == 2){
                            while(msg.contains(";")){
                                int x = msg.indexOf(";");
                                player.schedule.add(msg.substring(0,x));
                                player.turns.add(msg.substring(0,x));
                                player.updateKnowledge(msg.substring(0,x),"9999");
                                msg = msg.substring(x+1);
                                player.number_of_player++;
                            }
                            player.num_message++;

                        }
                        else if (player.new_round){
                            player.schedule.clear(); player.turns.clear();
                            check = new Meld();
                            while(msg.contains(";")) {
                                int x = msg.indexOf(";");
                                player.schedule.add(msg.substring(0, x));
                                player.turns.add(msg.substring(0, x));
                                msg = msg.substring(x + 1);
                                player.number_of_player++;
                            }
                            player.new_round = false;
                            // clear the table
                            Platform.runLater(() ->  {
                                clearTable();
                                displayPlayerHand();
                            });
                        }
                        // upgrade player point
                        else if (player.upgrade_point){
                            String cards= msg;
                            while(cards.contains(";")){
                                int i = cards.indexOf(";");
                                // get name
                                String p = cards.substring(0,i);
                                cards = cards.substring(i+1);
                                i = cards.indexOf(";");
                                // get point
                                String num = cards.substring(0,i);
                                cards = cards.substring(i+1);
                                System.out.println("name " + p);
                                if(player.getListOfThePlayer().containsKey(p)){
                                    player.getPoints().put(p,Integer.valueOf(num));
                                }
                                if (cards.length() == 1 || cards.length() == 0)
                                    break;
                            }
                            displayPoint();
                            upgradeTurn();
                            player.new_round = true;
                            player.upgrade_point = false;
                        }
                        else {
                            msg = secure_system.decrypt(player.public_key,player.N, new BigInteger(msg));
                            int index = msg.indexOf(":::");
                            String cards = msg.substring(index+3);
                            String enemy = msg.substring(0,index);
                            System.out.println(msg);
                            // receiving dealing cards
                            if (enemy.equals("SERVER") && cards.length() == 3*13){
                                check = new Meld();
                                chosenCard = new ArrayList<>();
                                Platform.runLater(() ->  {
                                    alert = new Alert(Alert.AlertType.INFORMATION);
                                    Stage s = (Stage) alert.getDialogPane().getScene() .getWindow();
                                    s.getIcons().add(getIcon());
                                    alert.setTitle("NEW GAME!!!");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Here is the previous game "+rank+"\nNEW GAME BEGINS");
                                    rank = new ArrayList<>();
                                    alert.show();});

                                console.clear();
                                player.hand.clear();
                                for(Map.Entry x : player.list_of_the_player.entrySet()){
                                    player.list_of_the_player.get(x.getKey()).clear();
                                }
                                while(cards.contains(";") && cards.length() > 1){
                                    int x = cards.indexOf(";");
                                    player.hand.add(cards.substring(0,x));
                                    cards = cards.substring(x+1);
                                }
                                Collections.sort(player.hand, new Human.sort());
                                System.out.println("New hand " + player.hand);
                                player.finish = false;
                                player.upgrade_point = true;
                            }
                            // somebody quits their turns
                            else if (!enemy.equals("SERVER") && !cards.equals("03;")){
                                boolean new_round = false;
                                cards = convertCardsToString(cards);
                                player.updateKnowledge(enemy,cards);
                                player.setCurrentMeld(cards);
                                if(player.getListOfThePlayer().get(enemy).size() >= 13){
                                    rank.add(enemy);
                                    reNewRound(player,enemy);
                                    player.schedule.remove(enemy);
                                    check = new Meld();
                                    new_round = true;
                                    if(player.schedule.size() == 1)
                                        rank.add(player.schedule.get(0));
                                }
                                else{
                                    check.setValue(convertString(cards));
                                    player.turns.addLast(player.turns.poll());}

                                final String message = enemy + " played "+ cards +
                                        ". Card Left" + (13-player.getListOfThePlayer().get(enemy).size() + "\n");
                                String x = "";
                                if (player.turns.peek().equals("HUMAN")) x = name;
                                else x = enemy;
                                String next_round  = "The one who play next is " + x + "\n";
                                // clear the table
                                final String copy_cards = cards;
                                final boolean finalNew_round = new_round;
                                Platform.runLater(() ->  {
                                    displayCardPlayedByOthers(copy_cards);
                                    if (finalNew_round){
                                        // clearTable();
                                        Task<Void> sleeper = new Task<Void>() {
                                            @Override
                                            protected Void call() throws Exception {
                                                try {
                                                    Thread.sleep(350);
                                                } catch (InterruptedException e) {
                                                }
                                                return null;
                                            }
                                        };
                                        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                                            @Override
                                            public void handle(WorkerStateEvent event) {
                                                System.out.println("upside down is called");
                                                upsideDown();
                                            }
                                        });
                                        new Thread(sleeper).start();
                                        //   displayPlayerHand();
                                        console.appendText(message + " STARTING NEW ROUND!!!");
                                    }
                                    else{
                                        console.appendText(message);
                                        if(!player.finish)
                                            console.appendText(next_round);
                                    }
                                    upgradeTurn();
                                });
                            }
                            // someone plays a meld
                            else if (!enemy.equals("SERVER") && cards.equals("03;")){
                                player.turns.remove(enemy);
                                boolean renew = false;
                                if(player.turns.size() == 1){
                                    if(player.turns.peek().equals(name) &&  !player.finish){
                                        // check if the it plays out of cards
                                        if(player.getHand().size() == 0){
                                            player.finish= true;
                                        }
                                        // re new the turns; because the robot play, therefore, put the robot to the last of the deque
                                        reNewTurnAgressiveTakingTheLead(player);
                                    }
                                    // re new the turns in case the current play is not the robot.
                                    else reNewTurn(player);
                                    check = new Meld();
                                    chosenCard = new ArrayList<>();
                                    renew = true;
                                }
                                final String message = enemy + " quit his turn! \n";
                                // clear the table and update player hand
                                final boolean finalRenew = renew;
                                Platform.runLater(() ->  {
                                    if (finalRenew){
                                        //clearTable();
                                        upsideDown();
                                        // displayPlayerHand();
                                    }
                                    console.appendText(message);
                                    upgradeTurn();
                                });
                            }

                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
        launch(args);
    }
    // display player point
    private static void displayPoint() {
        // upgrade points
        for (int i =0; i< enemies.size();i++){
            for(Map.Entry<String,Integer> entry: player.getPoints().entrySet()){
                if(enemies.get(i).getText().equals(entry.getKey()))
                    points.get(i).setText(String.valueOf(entry.getValue()));
                else if (enemies.get(i).getText().equals(name) && entry.getKey().equals("HUMAN"))
                    points.get(i).setText(String.valueOf(entry.getValue()));
            }
        }
    }

    // display player turn
    private static void upgradeTurn() {
        String current_player = player.turns.peek();
        for (int i =0; i< enemies.size();i++){
            if(enemies.get(i).getText().equals(current_player)){
                enemies.get(i).setFill(Color.RED); }
            else if (enemies.get(i).getText().equals(name) && current_player.equals("HUMAN")){
                enemies.get(i).setFill(Color.RED); }
            else{ enemies.get(i).setFill(Color.BLACK); }
        }

        // set black list

        for (int i =0; i< enemies.size();i++){
            String current = enemies.get(i).getText();
            if(current.equals(name)) current = "HUMAN";

            if(!player.turns.contains(current))
                enemies.get(i).setFill(Color.BLUE);

        }


    }

    private static String convertCardsToString(String msg){
        String out ="";
        while(msg.contains(";") && msg.length() > 1){
            int x = msg.indexOf(";");
            out += (msg.substring(0,x));
            msg = msg.substring(x+1);
        }
        return out;
    }
    private static String[] convertToMessage(List<String> l){
        // sort then convert
        Collections.sort(l, new sort());

        String[] output = new String[l.size()];
        int i = 0;
        for (String str: l){
            int index = str.indexOf(".");
            output[i] = str.substring(0,index);
            i++;
        }
        return output;
    }
    public static class sort implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            ArrayList<Character> l = new ArrayList<>(); l.add('S');l.add('C');l.add('D');l.add('H');
            ArrayList<Character> l1 = new ArrayList<>();
            l1.add('3');l1.add('4');l1.add('5');l1.add('6');l1.add('7');l1.add('8');l1.add('9');l1.add('X');
            l1.add('1'); l1.add('J');l1.add('Q');l1.add('K');l1.add('A');l1.add('2');l1.add('L');
            if(a.charAt(0) == b.charAt(0)) return (l.indexOf(a.charAt(1)) - l.indexOf(b.charAt(1)));
            else if (l1.contains(a.charAt(0)) && !l1.contains(b.charAt(0))) return 1;
            else if (!l1.contains(a.charAt(0)) && l1.contains(b.charAt(0))) return -1;
            else return l1.indexOf(a.charAt(0)) - l1.indexOf(b.charAt(0));
        }
    }
    // renew the turn if the robot is not the controller
    private static void reNewTurn(Human player){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(player.turns.poll());
        if(index_current_player == -1) index_current_player = 0;

        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
        //   System.out.println("Renew Turn is Called " + player.turns);
    }
    private static void reNewTurnAgressiveTakingTheLead(Human player){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(player.turns.peek());
        if (index_current_player == player.number_of_player-1) index_current_player = 0;
        else index_current_player ++;
        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
    }
    private static String[] convertString(String str){
        System.out.println(str);
        String[] out = new String[str.length()/2];
        int i =0;
        while(str.length() > 0){
            out[i] = str.substring(0,2);
            str = str.substring(2);
            i+= 1;
        }
        return  out;
    }
    private static void reNewRound (Human player, String enemy){
        ArrayList<String> sample = new ArrayList<>();
        sample.addAll(player.schedule);
        int index_current_player = sample.indexOf(enemy);

        player.turns.clear();
        player.turns.addAll(sample.subList(index_current_player+1,sample.size()));
        player.turns.addAll(sample.subList(0,index_current_player));
        //  System.out.println("Renew Turn is Called " + player.turns);
    }
}
