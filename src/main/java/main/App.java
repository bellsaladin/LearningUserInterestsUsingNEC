package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Random;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws MalformedURLException, IOException, InterruptedException
    {
        //String analysisResult = PhraseAnalyzer.analyzePhrase("Ronaldo is a geat player", false);
        //System.out.println("Analysis Result : " + analysisResult);
        
    	try {
    	    //String query = "SELECT * FROM tweets WHERE tweet_id not in (SELECT tweet_id from tweet_analysis)";
    		//String query = "SELECT * FROM tweets_of_users_with_many_tweets WHERE tweet_id not in (SELECT tweet_id from tweet_analysis)";
    		String query = "SELECT * FROM tweets_of_users_with_many_tweets WHERE tweet_id not in (SELECT tweet_id from tweet_analysis)";
    		Connection connection  = MySQLConnection.getConnection();
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        while (resultSet.next()) {
	        	// load data of a tweet
		        int tweet_id = resultSet.getInt(1);
		        String tweet_content = resultSet.getString(6);	  
		        // analysis the tweet
		        System.out.println("Analysis of tweet with id : " + tweet_id);
		        System.out.println("Tweet content             : " + tweet_content);		        
		        String analysisResult = PhraseAnalyzer.analyzePhrase(tweet_content);
		        // save the analysis result to DB
		        String insertIntoTweetAnalysis = "INSERT INTO tweet_analysis"
		        		+ "(tweet_id, analysis_result) VALUES"
		        		+ "(?,?)";
		        PreparedStatement preparedStatement = connection.prepareStatement(insertIntoTweetAnalysis);
		        preparedStatement.setInt(1, tweet_id);
		        preparedStatement.setString(2, analysisResult);
		        preparedStatement.executeUpdate();
		        
		        // a short & random sleep so that our analysis will not be detected
		        /*Random generator = new Random(); 
		        int sleepDuration = generator.nextInt(5000) + 1;
	            Thread.sleep(sleepDuration);
		        System.out.println("Sleep duration : " + sleepDuration);*/
		        System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - ");
	        }
	        connection.close();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    	
    	
    	
    	
    	//MySQLConnection.testStatement();
    	
    	
    }
}
