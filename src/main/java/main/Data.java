package main;

import java.util.ArrayList;
import java.util.List;
 
public class Data {
 
	public String OriginalText;
	//private String data2 = "hello";
	public List<Mention> HYENAOutput = new ArrayList<Mention>();
 
	//getter and setter methods
 
	@Override
	public String toString() {
		return OriginalText + ", HYENAOutput=" + HYENAOutput ;
	   //return "DataObject [data1=" + OriginalText + ", data2=" + data2 + ", list=" + list + "]";
	}
 
}