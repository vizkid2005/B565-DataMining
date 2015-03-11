package HW2;
import java.sql.Connection;
import java.sql.DriverManager;

public class MySqlConnection {

    Connection conn ;
    public MySqlConnection(){
        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String serverName = "localhost";
            String mydatabase = "dmAssignment2";
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            String username = "root";
            String password = "";
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Success");
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
