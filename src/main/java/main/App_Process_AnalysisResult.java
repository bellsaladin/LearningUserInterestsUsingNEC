package main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App_Process_AnalysisResult {
	public static void main( String[] args ) throws MalformedURLException, IOException, InterruptedException
    {
        //String analysisResult = PhraseAnalyzer.analyzePhrase("Ronaldo is a geat player", false);
        //System.out.println("Analysis Result : " + analysisResult);
        
    	try {
    	    //String query = "SELECT * FROM tweets WHERE tweet_id not in (SELECT tweet_id from tweet_analysis)";
    		String query = "SELECT tweet_analysis.tweet_id, analysis_result FROM tweet_analysis, tweets_of_users_with_many_tweets " +
		    				"WHERE tweet_analysis.tweet_id  = tweets_of_users_with_many_tweets.tweet_id " +
		    				"AND analysis_result like '%decision%' AND tweet_analysis.tweet_id NOT IN (SELECT tweet_id FROM tweet_analysis_entities)";
    		Connection connection  = MySQLConnection.getConnection();
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        while (resultSet.next()) {
	        	// load data of a tweet
		        int tweet_id = resultSet.getInt(1);
		        String analysis_result = resultSet.getString(2);	  
		        // analysis the tweet
		        //System.out.println("Analysis of tweet with id : " + tweet_id);
		        //System.out.println("Tweet content : " + tweet_content);		        
		        PhraseAnalyzer.showEntitiesClassesTree(tweet_id, analysis_result, true);
		        
		        System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	        }
	        connection.close();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
  
    }
}
