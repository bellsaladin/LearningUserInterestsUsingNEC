package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class PhraseAnalyzer {
	public static String analyzePhrase(String phrase) throws MalformedURLException, IOException{
		//String phrase = "Ronaldo was the emperor of the First French Empire.";
    	phrase = URLEncoder.encode(phrase, "UTF-8");
    	
    	InputStream input = new URL("https://d5gate.ag5.mpi-sb.mpg.de/webhyena/hyena_json?inputText=" + phrase).openStream();
    	String response = IOUtils.toString(input, "UTF-8");
    	
    	return response;
	}
	
	public static void showEntitiesClassesTree(int tweet_id, String jsonResult){
		showEntitiesClassesTree(tweet_id, jsonResult);
	}
	
	public static void showEntitiesClassesTree(int tweet_id, String jsonResult, boolean saveToDB){
		System.out.println("> Tweet ID : "  + tweet_id);
		Data data;
		try{
			data = new Gson().fromJson(jsonResult, Data.class);
		}catch (Exception e){
			System.out.println("> Exception GSON : "  + e.getMessage());
			return;
		}
		
    	System.out.println("> Original Text : "  + data.OriginalText);
    	
    	for(Mention mention : data.HYENAOutput){
        	System.out.println("  > Mention : "  + mention.mention);
        	List<Prediction> validPredictions = new ArrayList<Prediction>();
        	//selected only valid predictions
        	for(Prediction prediction : mention.predictions){
        		if(prediction.decision == 1){
        			validPredictions.add(prediction);	            	
        		}
        	}        	
        	
        	//Sorting
        	Collections.sort(validPredictions, new Comparator<Prediction>() {
        	        public int compare(Prediction prediction1, Prediction  prediction2)
        	        {
        	            return new Float(prediction2.value).compareTo(prediction1.value);
        	        }
    	    });
        	
        	// order the predictions
        	for(Prediction prediction : validPredictions){
        		if(saveToDB){
        			String insertSQL = "INSERT INTO tweet_analysis_entities"
    		        		+ "(tweet_id, type, mention, decision, value, parent) VALUES"
    		        		+ "(?,?,?,?,?,?)";
    		        PreparedStatement preparedStatement;
					try {
						preparedStatement = MySQLConnection.getConnection().prepareStatement(insertSQL);
						preparedStatement.setInt(1, tweet_id);
	    		        preparedStatement.setString(2, prediction.type);
	    		        preparedStatement.setString(3, mention.mention);
	    		        preparedStatement.setInt(4, prediction.decision);
	    		        preparedStatement.setFloat(5, prediction.value);
	    		        preparedStatement.setString(6, prediction.parent);
	    		        preparedStatement.executeUpdate();	    		        
					} catch (SQLException e) {
						e.printStackTrace();
					}
        		}
        		System.out.println("   >>>> TYPE  : " + prediction.type);
        		System.out.println("   >>>> VALUE : " + prediction.value);
        	}
        	
    	}
	}
}
