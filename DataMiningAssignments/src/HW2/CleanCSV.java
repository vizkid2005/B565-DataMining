package HW2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A General purpose CSV Cleaner that I hope will be useful for future projects.
 */
public class CleanCSV {

    public ArrayList<int[]> clean()throws IOException {

        ArrayList<int[]> dataset = new ArrayList<int[]>();
        String filePath = new String("D:\\College - Spring 2015\\Data Mining\\HW\\HW2\\Question 3\\distinctdata.csv");
        FileReader fr = new FileReader(new File(filePath));
        BufferedReader br = new BufferedReader(fr);
        String line;
        int badDataPoints = 0;
        int bigCounter = 0;
        while((line = br.readLine()) != null){

            //Age, Occupation, Rating, Genre
            int[] featureVector = new int[21];
            String[] parts = line.split(",");
            for(int i = 0;i<parts.length;i++){
                parts[i] = parts[i].replace("\"","");
            }
            bigCounter++;
            //We now skip the undesirable columns from the data.
            //This completely depends on the dataset and the desirability of data.
            int counter = 0;
            for(int i = 0;i<parts.length;i++){
                try {
                    featureVector[counter] = Integer.parseInt(parts[i]);
                }catch(Exception e){
                    //System.out.println(parts[i]);
                    badDataPoints++;
                }
                counter++;
            }
            /*System.out.println();
            for(int i=0;i<featureVector;i++){
                System.out.print(featureVector[i]+" ");
            }*/
            dataset.add(featureVector);
        }
    return dataset;
    }
}
