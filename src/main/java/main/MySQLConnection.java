package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection{
	
	public static Connection connection = null;
	
    public static Connection getConnection() {
    	if(connection != null) return connection;
    	
	    String dbUrl = "jdbc:mysql://localhost/_twitter_dataset";
	    String dbClass = "com.mysql.jdbc.Driver";
	    String username = "root";
	    String password = "root";
	    try {	
	        Class.forName(dbClass);
	        connection = DriverManager.getConnection(dbUrl,
	            username, password);
	        return connection;
	        /*Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        while (resultSet.next()) {
	        String tableName = resultSet.getString(2);
	        System.out.println("Table name : " + tableName);
	        }*/
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
    }
    public static void closeConnection() throws SQLException{
    	connection.close();
    }
    
    public static void testStatement(){
    	try {
    	    String query = "Select * from tweets";
    		Connection connection  = MySQLConnection.getConnection();
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        while (resultSet.next()) {
	        String tableName = resultSet.getString(2);
	        System.out.println("Table name : " + tableName);
	        }
	        connection.close();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    }
}

