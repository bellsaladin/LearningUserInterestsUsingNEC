package main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cue.lang.WordIterator;
import cue.lang.stop.StopWords;
import cue.lang.unicode.BlockUtil;

public class App_Process_IdentifyLanguage {
	public static void main( String[] args ) throws MalformedURLException, IOException, InterruptedException
    {
        //String analysisResult = PhraseAnalyzer.analyzePhrase("Ronaldo is a geat player", false);
        //System.out.println("Analysis Result : " + analysisResult);
        
    	try {
    	    //String query = "SELECT * FROM tweets WHERE tweet_id not in (SELECT tweet_id from tweet_analysis)";
    		String query = "SELECT message FROM tweets_of_users_with_many_tweets";
    		Connection connection  = MySQLConnection.getConnection();
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        while (resultSet.next()) {
		        String message = resultSet.getString(1);	  
		        // language identification
		        System.out.println(BlockUtil.guessUnicodeBlock(message) + " : " + message);
		        System.out.println(StopWords.guess(message) + " : " + message);
		        
		        System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	        }
	        connection.close();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
  
    }
}
