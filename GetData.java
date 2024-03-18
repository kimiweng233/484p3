import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding 
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }

    // TODO: Implement this function
    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();
        
        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            // Your implementation goes here....
            
            ResultSet rst = stmt.executeQuery(
                "SELECT * " + 
                "FROM " + userTableName
                );

            Statement stmt2 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt3 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt4 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt5 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement stmt6 = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            while (rst.next()) {
                int user_id = rst.getInt(1);    
                // friends
                JSONArray friends = new JSONArray();
                ResultSet rst2 = stmt2.executeQuery(
                    "SELECT User2_Id " + 
                    "FROM " + friendsTableName + " " +
                    "WHERE User1_Id = " + user_id + " " +
                    "AND User2_Id > " + user_id
                );
                while (rst2.next()) {
                    friends.put(rst2.getInt(1));
                }
                rst2.close();
                // current city
                JSONObject current = new JSONObject();
                ResultSet rst3 = stmt3.executeQuery(
                "SELECT Current_City_Id " + 
                "FROM " + currentCityTableName + " " +
                "WHERE User_Id = " + user_id
                );
                while (rst3.next()) {
                    int current_city_id = rst3.getInt(1);
                    ResultSet rst4 = stmt4.executeQuery(
                        "SELECT City_Name, State_Name, Country_Name " + 
                        "FROM " + cityTableName + " " +
                        "WHERE City_Id = " + current_city_id
                    );
                    if (rst4.next()) {
                        current.put("city", rst4.getString(1));
                        current.put("state", rst4.getString(2));
                        current.put("country", rst4.getString(3));
                    }
                    rst4.close();
                }
                rst3.close();
                // hometown city
                JSONObject hometown = new JSONObject();
                ResultSet rst5 = stmt5.executeQuery(
                "SELECT Hometown_City_Id " + 
                "FROM " + hometownCityTableName + " " +
                "WHERE User_Id = " + user_id
                );
                while (rst5.next()) {
                    int hometown_city_id = rst5.getInt(1);
                    ResultSet rst6 = stmt6.executeQuery(
                        "SELECT City_Name, State_Name, Country_Name " + 
                        "FROM " + cityTableName + " " +
                        "WHERE City_Id = " + hometown_city_id
                    );
                    if (rst6.next()) {
                        hometown.put("city", rst6.getString(1));
                        hometown.put("state", rst6.getString(2));
                        hometown.put("country", rst6.getString(3));
                    }
                    rst6.close();
                }
                rst5.close();
                // form user JSONobject
                JSONObject user = new JSONObject();
                user.put("MOB", rst.getInt(5));
                user.put("hometown", hometown);
                user.put("current", current);
                user.put("gender", rst.getString(7));
                user.put("user_id", user_id);
                user.put("DOB", rst.getInt(6));
                user.put("last_name", rst.getString(3));
                user.put("first_name", rst.getString(2));
                user.put("YOB", rst.getInt(4));
                user.put("friends", friends);
                users_info.put(user);
            }
            rst.close();
            stmt.close();
            stmt2.close();
            stmt3.close();
            stmt4.close();
            stmt5.close();
            stmt6.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
