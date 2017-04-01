package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	public static String inputStreamToString(InputStream is) throws IOException {
	    String s = "";
	    String line = "";
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    
	    // Read response until the end
	    while ((line = rd.readLine()) != null) { s += line; }
	    
	    // Return full string
	    return s;
	}
	
	
}
