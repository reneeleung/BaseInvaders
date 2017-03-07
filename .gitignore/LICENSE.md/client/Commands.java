//package ExchangeClient;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Commands{
  static final String server = "codebb.cloudapp.net";
  //static final String server = "localhost";
  static final String port = "17429";
  static final String username = "technologic";
  //static final String username = "a";
  //static final String pw = "a";
  static final String pw = "attwnml";
  static double x = 0;
  static double y = 0;
  static double vx = 0;
  static double vy = 0;
  static String mineOwner = "";
  static double minex = -10000;
  static double miney = -10000;
  static boolean playerNearby=false;
  static double nextPoint[][];
  static int gotoInd = 0;
  static int numMines = 0;
  static double myMines[][] = new double[2][100000];
  static Socket socket;

  static PrintWriter pout;

  static BufferedReader bin;


  public static void main(String[] args) throws IOException{
    socket = new Socket(server, Integer.parseInt(port));
    pout = new PrintWriter(socket.getOutputStream());
    bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    pout.println(username + " " + pw);
    pout.flush();

    //set next search points
    nextPoint = new double[3][100]; //x,y,oldMine
    for (int i =0;i<2;i++){
      for (int j =0;j<100;j++){
        nextPoint[i][j]=Math.random()*10000;
        nextPoint[2][j]=0;
      }
    }
    gotoInd=0;



    getStatus();
    System.out.println(x+" "+y);

    //acc(0,1);
    int count=0; //number of attempts per mine

    for (int i=0;i<1000000000;i++){
      if (minex>=0 && !mineOwner.equals(username) && !isOwner(minex, miney)){ //we have a mine nearby




        if(Math.abs(x-minex)<150 && Math.abs(y-miney)<150){

          gotoXY(minex,miney, 0.25);
          pause(70);
          gotoXY(minex,miney, 0.25);
          pause(70);
          gotoXY(minex,miney, 0.25);
          pause(70);
          gotoXY(minex,miney, 0.25);
          pause(70);
          gotoXY(minex,miney, 0.3);
          pause(600);
          gotoXY(minex,miney, 0.4);
          pause(1800);
          brake();
          pause(900);
          /*brake();
          pause(1000);
          gotoXY(minex,miney, 0.5);
          pause(100);
          gotoXY(minex,miney, 0.5);
          pause(100);
          gotoXY(minex,miney, 0.5);
          pause(100);
            gotoXY(minex,miney, 0.5);
            pause(2000);*/
          //acc(Math.atan2(y,x), 0.8);
          //pause(400);
          count++;
          if (count>7){
            //back off
            System.out.println("BACK OFF");
            acc(0,1);
            acc(0,1);
            pause(700);
            count=0;
          }
        }
        /*else if(Math.abs(x-minex)<200 && Math.abs(y-miney)<200){
          brake();
          System.out.println("BIG PAUSE");
          pause(500);
        }*/
        else {
          gotoXY(minex,miney, 1);
        }
        System.out.println("On my way to m "+nextPoint[0][gotoInd%nextPoint[0].length]+" "+nextPoint[1][gotoInd%nextPoint[0].length]+"\t"+x+"\t"+y+"\t"+vx+"\t"+vy);

        //System.out.println("On my way to m "+minex+"\t"+miney+"\t"+vx+" "+vy);

        //reached the mine
        if (mineOwner.equals(username) && !contains(myMines[0],minex) && !contains(myMines[1], miney)){
          myMines[0][numMines] = minex;
          myMines[1][numMines] = miney;
          numMines++;
          minex = -10000;
          miney = -10000;
          mineOwner = "";
        }
      }
      else{ //no mine nearby
        if (isOwner(nextPoint[0][gotoInd], nextPoint[1][gotoInd])){
          gotoInd++;
        }
        int closest = gotoInd;
        double clo_dis = Integer.MAX_VALUE;
        if (nextPoint[2][gotoInd%nextPoint[0].length] == 1){
          System.out.println("should search for closest now");
          for(int p = gotoInd; p < nextPoint[0].length ;p++){
            if(nextPoint[2][p] == 1){
              double dist = (x-nextPoint[0][p]) * (x-nextPoint[0][p]) + (y-nextPoint[1][p]) * (y-nextPoint[1][p]);
              if(dist < clo_dis){
                closest = p;
                clo_dis = dist;
                double temp[] = {nextPoint[0][closest],nextPoint[1][closest],nextPoint[2][closest]};
                nextPoint[0][closest]=nextPoint[0][gotoInd];
                nextPoint[1][closest]=nextPoint[1][gotoInd];
                nextPoint[2][closest]=nextPoint[2][gotoInd];
                nextPoint[0][gotoInd]=temp[0];
                nextPoint[1][gotoInd]=temp[1];
                nextPoint[2][gotoInd]=temp[2];
              }
            }
          }
          //go to the next invaded mine
          minex = nextPoint[0][gotoInd%nextPoint[0].length];
          miney = nextPoint[1][gotoInd%nextPoint[0].length];
          nextPoint[2][gotoInd%nextPoint[0].length] = 0; //set it to
          //mineOwner="someone else";
          gotoXY(minex,miney);

          System.out.println("On my way to R "+nextPoint[0][gotoInd%nextPoint[0].length]+" "+nextPoint[1][gotoInd%nextPoint[0].length]+"\t"+x+"\t"+y+"\t"+vx+"\t"+vy+"\t"+mineOwner);
        }
        else if(Math.abs(x-nextPoint[0][gotoInd%nextPoint[0].length])< 500 && Math.abs(y-nextPoint[1][gotoInd%nextPoint[0].length])< 500 ){
          gotoInd++; //if you're roughly there it's good
        }
        gotoXY(nextPoint[0][gotoInd%nextPoint[0].length], nextPoint[1][gotoInd%nextPoint[0].length]);

        //mine at 6091 9055, 5717 8951
        System.out.println("On my way to "+nextPoint[0][gotoInd%nextPoint[0].length]+" "+nextPoint[1][gotoInd%nextPoint[0].length]+"\t"+x+"\t"+y+"\t"+vx+"\t"+vy);

        bombBehind(x,y);

      }
      //System.out.println(x+" "+y+" "+vx+" "+vy);
      scanMines();
    }


    pout.close();
    bin.close();


  }

  public static void getStatus() throws IOException{
    String args[] = {"STATUS"};
    for (int i = 0; i < args.length; i++) {
      pout.println(args[i]);
    }
    pout.flush();
    String str = bin.readLine();
    String array[] = str.split(" ");
    x = Double.parseDouble(array[1]); //x
    y = Double.parseDouble(array[2]); //y
    vx = Double.parseDouble(array[3]); //vx
    vy = Double.parseDouble(array[4]);

    //find array index of MINES list
    int search_mine = 0;
    for(int i = 0; i < array.length; i++){
      if(array[i].equals("MINES")){
        search_mine = i;
        break;
      }
    }
    //get location of first mine if any exist
    if (Double.parseDouble(array[search_mine+1]) > 0){
      mineOwner = array[search_mine+2];
      minex = Double.parseDouble(array[search_mine +3]);
      miney = Double.parseDouble(array[search_mine +4]);
    }

    //find array index of PLAYERS list
    for(int i = 0; i < array.length; i++){
      if(array[i].equals("PLAYERS")){
        playerNearby = Double.parseDouble(array[i+1])>0;
        break;
      }
    }
  }
  public static void acc(double rad, double speed) throws IOException{
    String args[] = {"ACCELERATE "+rad+" "+speed};
    for (int i = 0; i < args.length; i++) {
      pout.println(args[i]);
    }
    pout.flush();
    String line;
    line = bin.readLine();
    /*while ((line = bin.readLine()) != null) {
     }*/
    getStatus();
  }
  public static void brake() throws IOException{
    String args[] = {"BRAKE"};
    for (int i = 0; i < args.length; i++) {
      pout.println(args[i]);
    }
    pout.flush();
    String line;
    line = bin.readLine();
  }
  public static void bomb(double px, double py) throws IOException{
    getStatus();
    String args[] = {"BOMB "+px+" "+py+" 20"};
    for (int i = 0; i < args.length; i++) {
      pout.println(args[i]);
    }
    pout.flush();
    String line;
    line = bin.readLine();
    if(line.contains("BOMB_OUT")){
      System.out.println("BOMBED");
    }
  }
  public static void bombBehind(double px, double py) throws IOException {
    double nx = 3*vx/Math.sqrt(vx*vx+vy*vy);
    double ny = 3*vy/Math.sqrt(vx*vx+vy*vy);
    px = px - nx;
    py = py - ny;
    bomb(px,py);
  }

  public static void gotoXY (double x2, double y2, double mag){
    double dx = x2 - x;
    double dy = y2 - y;
    /*double norm = Math.sqrt(dx*dx + dy*dy);
     double v2x = vmax * (dx / norm);
     double v2y = vmax * (dy / norm);
     double ax = v2x - vx;
     double ay = v2y - vy;*/
    double accelerate = Math.atan2(dy, dx);
    try{
      acc(accelerate, mag);
    }catch (IOException e){
    }
  }
  public static void gotoXY (double x2, double y2){
    gotoXY(x2,y2,1);
  }
  public static void randomXY(){
    x = Math.random()*100000;
    y = Math.random()*100000;
    System.out.println("Random x "+x);
    System.out.println("Random y "+y);
  }
  public static void scanMines() throws IOException{
    //find owner of myMines
    for (int i = 0; i < numMines; i++){
      double px = myMines[0][i];
      double py = myMines[1][i];
      String args[] = {"SCAN "+px+" "+py};
      for (int k = 0; k < args.length; k++) {
        pout.println(args[k]);
      }
      pout.flush();
      String line;
      line = bin.readLine();
      String array[] = line.split(" ");
      //if owner is not mine anymore, place into nextPoint array
      //find array index of MINES list
      int search_mine = 0;
      for(int k = 0; k < array.length; k++){
        if(array[k].equals("MINES")){
          search_mine = k;
          break;
        }
      }
      //go to invaded mines next
      if(!array[search_mine + 2].equals(username)){
        //System.out.println(px+" "+py+" was taken from us!");
        nextPoint[0][gotoInd+i+1] = px;
        nextPoint[1][gotoInd+i+1] = py;
        nextPoint[2][gotoInd+i+1] = 1;
      }
    }
  }
  public static boolean isOwner(double px, double py) throws IOException{
    String args[] = {"SCAN "+px+" "+py};
    for (int k = 0; k < args.length; k++) {
      pout.println(args[k]);
    }
    pout.flush();
    String line;
    line = bin.readLine();
    String array[] = line.split(" ");
    int search_mine = 0;
    for(int k = 0; k < array.length; k++){
      if(array[k].equals("MINES")){
        search_mine = k;
        break;
      }
    }
    return array[search_mine + 2].equals(username);
  }
  public static void look(){
    //getStatus();
  }

  public static boolean contains(double[] arr, double x){
    for (int i =0;i<arr.length;i++){
      if (x==arr[i])
      return true;
    }
    return false;
  }
  public static void pause(int MS){
    try{
      Thread.sleep(MS);
    } catch (InterruptedException e){
    }
  }
}
