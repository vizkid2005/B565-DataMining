package HW1;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Linear Regression for Question 2
 *
 */
public class Question2a {

    //This method handles input from a CSV file. Specify path and the filename in this method
    //Returns an ArrayList of all Node ojects
    public ArrayList<Node> fileReader(){
        String path = new String("E:\\College - Spring 2015\\Data Mining\\DataMiningAssignments\\src\\HW1");
        String fileName = new String("dataFile.txt");
        String filePath = new String(path+"\\"+fileName);

        ArrayList<Node> bigDelta = new ArrayList<Node>();
        try {
            Scanner sc = new Scanner(new File(filePath));

            while(sc.hasNextLine()) {
                String line[] = sc.next().split(",");
                double d = Double.parseDouble(line[0]);
                double l = Double.parseDouble(line[1]);
                Node node = new Node(d,l);
                bigDelta.add(node);
            }
        }catch(FileNotFoundException fn){
            System.out.println("File not found");
            System.exit(0);
        }

        if(bigDelta.isEmpty()){
            return null;
        }

        return bigDelta;
    }
    //Each data point is an object of the type Node
    public class Node{
        double d;
        double l;

        public Node(double dataPoint, double label){
            d = dataPoint;
            l = label;
        }
    }

    //Contains all the important information
    //Specify the constantDisplacement here based on the data
    public class Container {
        double minX, minY, maxX, maxY;
        double minXPair, minYPair, maxXPair, maxYPair;
        double a,b,c,d;
        double bestBeta0,bestBeta1;
        double constDisplacement;

        public Container() {
            minX = minY = 5000;
            maxX = maxY = -5000;
            minXPair = minYPair = maxXPair = maxYPair = 0.0;
            a=b=c=d=0.0;
            bestBeta0=bestBeta1=0.0;
            constDisplacement = 0.2;
        }
    }
    public static void main(String args[])throws Exception {

        Question2a obj = new Question2a();
        ArrayList<Node> bigDelta = obj.fileReader();
        if(bigDelta == null || bigDelta.size() > 50){
            System.out.println("File either empty or size > 50");
            System.exit(0);
        }

        double beta0 = 0.0;
        double beta1 = 0.0;
        obj.calculateABCD(obj,bigDelta);
    }
    //Returns the cost of drawing a regression line with beta0 and beta1.
    public double costFunction(ArrayList<Node> bigDelta, double beta0, double beta1){
        int size = bigDelta.size();
        double cost = 0.0;
        for(int i =0;i<size;i++){
            double diff = (bigDelta.get(i).l - (beta0+beta1*bigDelta.get(i).d));
            double temp = Math.pow(diff,2);
            cost = cost + temp;
        }
        return cost;
    }
    //Does a Bruteforce search over the bounds of Beta0 and Beta1
    public void fitRegressionLine(ArrayList<Node> bigDelta, Container c){
        double beta0Upper = c.c;
        double beta0Lower = c.a;
        double beta1Upper = c.d;
        double beta1Lower = c.b;
        double step = 0.001;

        double minCost = 999999999;
        long overallCount = 0;
        //O(n square) complexity bounded by beta0 and beta1
        for(double i = beta1Lower;i<=beta1Upper;i=i+step){
            for(double j = beta0Lower;j<=beta0Upper;j=j+step){
                overallCount++;
                double currentCost = costFunction(bigDelta,j,i);
                if(currentCost < minCost){
                    minCost = currentCost;
                    c.bestBeta0 = j;
                    c.bestBeta1 = i;
                }
            }
        }
        System.out.println("Best Params : Beta0: "+c.bestBeta0+" Beta1: "+c.bestBeta1);
        System.out.println("Number of Iterations = "+overallCount);
    }
    //Returns the corresponding values of the corresponding pairs of minX,minY,maxX,maxY
    public Container getPairs(Question2a obj,ArrayList<Node> bigDelta){

        Container c = new Container();
        int size = bigDelta.size();

        for(int i=0;i<size;i++){
            double delta = bigDelta.get(i).d;
            double label = bigDelta.get(i).l;

            if(delta < c.minX){
                c.minX = delta;
                c.minXPair = label;
            }
            else if(delta > c.maxX){
                c.maxX = delta;
                c.maxXPair = label;
            }

            if(label < c.minY){
                c.minY = label;
                c.minYPair = delta;
            }
            else if(label > c.maxY){
                c.maxY = label;
                c.maxYPair = delta;
            }
        }
        return c;
    }
    //Calculates the values of a, b, c, d
    public Container calculateABCD(Question2a obj,ArrayList<Node> bigDelta){

        Container c = obj.getPairs(obj, bigDelta);
        int size = bigDelta.size();
        if(c.minY <= 0){
            bigDelta=obj.transformData(bigDelta,c.minY);
            c = obj.getPairs(obj, bigDelta);
        }
        double slope1,slope2,constant1,constant2;
        //Handling the case when the slope of the regression line can be negative
        if(c.minXPair > c.maxXPair){
            slope1 = (c.maxY - c.maxXPair)/(c.maxYPair- c.maxX);
            slope2 = (c.minXPair - c.minY)/(c.minX - c.minYPair);
            constant1 = (c.maxY - slope1*c.maxYPair) + c.constDisplacement;
            constant2 = (c.minY - slope2*c.minYPair) - c.constDisplacement;
        }
        else{
            slope1 = (c.maxY - c.minXPair)/(c.maxYPair- c.minX);
            slope2 = (c.maxXPair - c.minY)/(c.maxX - c.minYPair);
            constant1 = (c.maxY - slope1*c.maxYPair) + c.constDisplacement;
            constant2 = (c.minY - slope2*c.minYPair) - c.constDisplacement;
        }

        if(slope1 < slope2){
            c.b = slope1;
            c.d = slope2;
        }else{
            c.b = slope2;
            c.d = slope1;
        }

        if(constant1 < constant2){
            c.a = constant1;
            c.c = constant2;
        }
        else{
            c.a = constant2;
            c.c = constant1;
        }

        System.out.println(c.a+"<-Beta0->"+c.c+";"+c.b+"<-Beta1->"+c.d);
        obj.fitRegressionLine(bigDelta,c);
        return c;
    }
    //Take the log10 transform of the labels
    public ArrayList<Node> transformData(ArrayList<Node> bigDelta,double minY){

        int size = bigDelta.size();
        for(int i=0;i<size;i++){
            double temp = bigDelta.get(i).l - minY + 2;
            bigDelta.get(i).l = Math.log10(temp);
        }
        for(int i=0;i<size;i++){
            System.out.println(bigDelta.get(i).d+","+bigDelta.get(i).l);
        }
        return bigDelta;
    }

}
