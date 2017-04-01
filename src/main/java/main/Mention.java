package main;

import java.util.ArrayList;
import java.util.List;

public class Mention {

	public String mention;
	public List<Prediction> predictions = new ArrayList<Prediction>();

	public String toString(){
		return "[ Mention : " + mention + ", predictions : " + predictions + "]";
	}
}
