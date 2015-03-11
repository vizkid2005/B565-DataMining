package HW3;

import com.mysql.jdbc.MySQLConnection;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This is the class that will read data from MySQL
 * and the configuration parameters from the input file and build tree file,
 * call the function ID3 and output the result.
 * Everything is considered as a String, the values are converted to the desired data type at runtime, for example for Binning.
 *
 */

public class DecTree {

    char classLabel;
    ArrayList<Character> features;
    static String[] columnNames;
    static int numberOfColumns;
    static int totalNumOfColumns;

    public static void main(String args[]){
       MySqlConnection msc = new MySqlConnection();
        readFiles(msc.conn);
        binTheData(msc.conn);
        buildDecTree(msc.conn);
    }
    public static void readFiles(Connection conn){

        /*
        readFiles(Connection conn) takes the existing connection to the established database and creates a new table inputdata.
        The number of columns are determined at runtime. The code is very generic and can handle any number of columns.
        The number of columns are not limited to 4, like the example.
        */

        String buildTreeFilePath = new String("D:\\College - Spring 2015\\Data Mining\\DataMiningAssignments\\src\\HW3\\tree.txt");
        String testFilePath = new String("D:\\College - Spring 2015\\Data Mining\\DataMiningAssignments\\src\\HW3\\test.txt");
        String inputFilePath;
        try {

            //Extracting the inputFilePath from buildTree.txt
            FileReader fr = new FileReader(new File(buildTreeFilePath));
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            inputFilePath = "D:\\College - Spring 2015\\Data Mining\\DataMiningAssignments\\src\\HW3\\"+line;

            //Extracting the column names from table.txt or whatever the filename is.
            BufferedReader inputbr = new BufferedReader(new FileReader(new File(inputFilePath)));
            line = inputbr.readLine();
            String[] features = line.split("\t");

            //Creating MySQL Table here
            StringBuilder sb = new StringBuilder();
            sb.append("drop table if exists inputdata\n");
            String query = sb.toString();
            try {
                Statement stmnt = conn.createStatement();
                stmnt.executeUpdate(query);
                System.out.println("Dropped existing table :D");
            }
            catch(SQLException sE){
                System.out.println("SQL Error :(");
                sE.printStackTrace();
            }
            sb.setLength(0);

            /*
            Creating table inputdata from the data found in the table.txt file
            Again, the number of columns are decided at runtime. One can have as many columns as one likes.
             */
            numberOfColumns = features.length;
            totalNumOfColumns = numberOfColumns + 1; //To compensate for tID in the database
            sb.append("Create table inputdata (tid integer(5) not null auto_increment ");
            for(int i=0;i<numberOfColumns;i++){
                sb.append(", \n"+features[i]+" varchar(10) ");
            }
            sb.append(", primary key(tid))");
            query = sb.toString();
            try {
                Statement stmnt = conn.createStatement();
                stmnt.executeUpdate(query);
                System.out.println("Successfully created table :D");
            }
            catch(SQLException sE){
                System.out.println("SQL Error :(");
                sE.printStackTrace();
            }
            //Table inputdata created. Add breakpoint here to verify before trying to insert data.

            //Now inserting rows to populate the table...........
            while((line = inputbr.readLine()) != null){
                String[] values = line.split("\t");
                StringBuilder insertSB = new StringBuilder();
                insertSB.append("insert into inputdata values(null,");
                for(int i =0;i<numberOfColumns;i++){
                    insertSB.append("\'"+values[i]+"\',");
                }
                insertSB.setLength(insertSB.length()-1);
                insertSB.append(")");
                try{
                    Statement stmnt = conn.createStatement();
                    int rowsReturned = stmnt.executeUpdate(insertSB.toString());
                }catch(SQLException sqlE){
                    System.out.println("Unable to insert values into table :(");
                    sqlE.printStackTrace();
                }
            }

        //Successfully inserted the data into table.
        }catch(FileNotFoundException fnfE){
            System.out.println("File not Found ");
            fnfE.printStackTrace();
        }catch(IOException ioE){
            System.out.println("Incorrect Input");
            ioE.printStackTrace();
        }

        //After the values are inserted into the table, we now extract the column names
        try{
            Statement stmnt = conn.createStatement();
            ResultSet rs = stmnt.executeQuery("select * from inputdata");
            ResultSetMetaData rsmd = rs.getMetaData();

            //+1 to compensate for the tID column
            columnNames = new String[totalNumOfColumns];
            //Getting all column names excluding tid;
            for(int i =0; i < totalNumOfColumns;i++) {
                columnNames[i] = rsmd.getColumnLabel(i+1);
            }
            for(int i = 0; i < totalNumOfColumns;i++) {
                System.out.println(columnNames[i]);
            }
        }catch(SQLException sqlE){
            System.out.println("Can not retrieve column names correctly :(");
            sqlE.printStackTrace();
        }
    }
    public static void binTheData(Connection conn){
        /*
        This method will ask the user whether or not he/she wants to bin the data ?
        If yes, what is the desired column on which the binning is desired. Binning function will bin only
        NUMERIC Data. The user is asked for the number of bins desired.
        Bins with equal intervals between the minimum and maximum values are created.
        The data is modified in the database itself.
        This process can be skipped altogether if binning is not desired.
        Binning the data can be considered a preProcessing step to reduce the complexity of the tree and
        prevent overfitting.
         */

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("Do you want to bin any data ?(y/n) :");
            String response = br.readLine();
            if(!response.equals("y")){
                System.out.println("You chose not to bin data (or you put anything else other than y ) ");
                return;
            }

            System.out.println("Which column do you want to bin the data on ? ");
            response = br.readLine();
            String columnToBin;
            boolean exitFlag = false;
            for(int i=0;i<totalNumOfColumns;i++){
                if(response.equals(columnNames[i])){
                    columnToBin = response;
                    exitFlag = true;
                    break;
                }
            }
            if(exitFlag){
                //IF the column does exists then we try to bin the data.
                //We first check if the values are in fact numeric;
                Statement stmnt = conn.createStatement();
                ResultSet rs = stmnt.executeQuery("select "+response+" from inputdata");
                double max = 0;
                double min = 100000;
                int numberOfBins = 0;

                while(rs.next()){
                    double current = Double.valueOf(rs.getString(response)).doubleValue();
                    if(current > max ){
                        max = current;
                    }
                    if(current < min){
                        min = current;
                    }
                }

                System.out.println("Verified all values are numeric :)");
                System.out.println("Enter the number of desired bins : ");
                numberOfBins = Integer.parseInt(br.readLine());

                double interval;//The interval between bins

                if(numberOfBins <= 0){
                    System.out.println("Enter atleast 1 bin. Continuing without binning .. ");
                    return;
                }
                else{
                    interval = (max - min)/numberOfBins;
                }

                double[] bins = new double[numberOfBins];
                bins[0] = min;
                for(int i =1; i< numberOfBins;i++){
                    bins[i] = bins[i-1]+interval;
                }

                System.out.println("The bins are : ");
                for(int i=0;i<bins.length;i++){
                    System.out.print(bins[i]+" ");
                }
                System.out.println();
                //Now that bins have been created, we now modify the dataset in MySQL to reflect the binned data.
                try {
                    stmnt = conn.createStatement();
                    rs = stmnt.executeQuery("select * from inputdata");

                    int tId;
                    double assignedBin;
                    double currentValue;
                    while(rs.next()){
                        tId = rs.getInt("tid");
                        currentValue = Double.valueOf(rs.getString(response)).doubleValue();
                        assignedBin = nearestBin(bins,currentValue);
                        String query = "update inputdata set "+response+"='"+assignedBin+"' where tid="+tId;
                        //System.out.println(query);
                        Statement updateStmnt = conn.createStatement();
                        int update = updateStmnt.executeUpdate(query);
                        if(update == -1){
                            System.out.println("Unsuccessful in executing : "+query);
                        }
                    }
                }catch(SQLException sqlE){
                    System.out.println("Error while retrieving data for binning. ");
                    sqlE.printStackTrace();
                    return;
                }
                System.out.println("Successfully binned the data in the database.");
                return;
            }
            else{
                System.out.println("Can not bin of the specified column");
            }
        }catch(IOException ioE){
            System.out.println("Incorrect input :p");
            ioE.printStackTrace();
        }
        catch(SQLException sqlE){
            System.out.println("Binning returned an error");
            sqlE.printStackTrace();
        }catch(NumberFormatException nfE){
            System.out.println("Non numerical data .. Cannot bin .. Continuing without binning ... ");
            return;
        }

    }
    public static double nearestBin(double[] bins, double number){

        //This method will return the nearest bin available to the given value.
        int nearest = -1;
        double distance = 1000;
        double difference;
        for(int i=0;i<bins.length;i++){
            difference = Math.abs(number - bins[i]);
            if(difference < distance){
                nearest = i;
                distance = difference;
            }
        }
        return (double) Math.round(bins[nearest]*100)/100;
    }
    public static void buildDecTree(Connection conn){

    }
}
