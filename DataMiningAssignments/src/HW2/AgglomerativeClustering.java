package HW2;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AgglomerativeClustering {

    static ArrayList<int[]> featureVectors;

    public static void main(String args[])throws Exception{
        //Initial Importing of the data
        CleanCSV cs = new CleanCSV();
        featureVectors = cs.clean();
        /* Code to check proper loading of data
        int i =0;
        while(i<10){
            int[] temp = featureVectors.get(i);
            for(int j =0;j<temp.length;j++){
                System.out.print(temp[j]+" ");
            }
            System.out.println();
            i++;
        }*/

        //We create an arraylist of all datapoints, without the data, just the index
        ArrayList<Cluster> dataList = new ArrayList<Cluster>();

        for(int i =0;i<featureVectors.size();i++){

            int[] temp = featureVectors.get(i);
            double age = (double) temp[0];
            double profession = (double) temp[1];
            //Creating a representative for a cluster
            Representative rep = new Representative(age,profession);
            //Creating a new cluster group for each data point.
            Cluster c = new Cluster(rep,i);
            dataList.add(c);
        }

        System.out.println("Size of clusterset : "+dataList.size());

        //Specify the total number of rows to iterate through
         List<Cluster> temp = dataList.subList(0,1000);
         ArrayList<Cluster> sublist = new ArrayList<Cluster>(temp);

        //Specify the total number of clusters required
        int numberOfTotalClusters = 5;
        while(sublist.size()>numberOfTotalClusters) {
            double maxDist = -1;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < sublist.size() - 1; i++) {
                for (int j = i + 1; j < sublist.size(); j++) {
                    if (calcCosineDistance(sublist.get(i).value, sublist.get(j).value) > maxDist) {
                        maxDist = calcCosineDistance(sublist.get(i).value, sublist.get(j).value);
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            Cluster i = sublist.get(bestI);
            Cluster j = sublist.get(bestJ);

            double repAge = (i.value.age + j.value.age) / 2;
            double repProfession = (i.value.profession + j.value.profession) / 2;
            Representative r = new Representative(repAge, repProfession);
            //displayList(sublist);
            Cluster newObject = new Cluster(r, i, j);
            sublist.remove(i);
            sublist.remove(j);
            sublist.add(newObject);
            //displayList(sublist);
        }

        /*ArrayList<Cluster> result = new ArrayList<Cluster>();
        printTree(sublist.get(0),"",result);
        System.out.println("Printing the result list ");
        displayList(result);
        */
        ArrayList<Cluster> result = new ArrayList<Cluster>();
        for(int i=0;i<sublist.size();i++){
            Cluster tempCluster = sublist.get(i);
            result.clear();
            printTree(tempCluster, "", result);
            PrintWriter pw = new PrintWriter("cluster"+i+".csv","UTF-8");

            for(int j=0;j<result.size();j++){
                int index = result.get(j).index;
                System.out.println("Index is : "+index);
                int[] featureVector = featureVectors.get(index);
                StringBuilder sb = new StringBuilder();
                for(int k = 0;k<featureVector.length;k++){
                    sb.append(featureVector[k]);
                    sb.append(',');
                }
                String line  = sb.substring(0,sb.length()-1);
                pw.println(line);
            }
            pw.close();
        }
    }

    //Prints the tree of clusters in an indented format and also returns all children of the particular clusters
    public static void printTree(Cluster root,String indent,ArrayList<Cluster> result){
        Cluster current = root;
        if(current==null){
            return;
        }
        System.out.println(indent+current.value.age+" -- "+current.value.profession);
        if(current.index == -1){
            printTree(current.leftChild,indent+"    ",result);
            printTree(current.rightChild,indent+"    ",result);
        }
        else{
            result.add(current);
        }
    }

    //Just displaying the members of the list
    public static void displayList(ArrayList<Cluster> list){

        System.out.println();
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i).value.age+" -- "+list.get(i).value.profession);
        }
    }

    //Calculating the cosine distance between age and profession
    public static double calcCosineDistance(Representative r1, Representative r2){
        double dist = (double) ((r1.age*r2.age+r1.profession*r2.profession)/(Math.sqrt(r1.age*r1.age+r1.profession*r1.profession)+Math.sqrt(r2.age*r2.age+r2.profession*r2.profession)));
        return dist;
    }
}
