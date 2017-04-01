package main;

public class Prediction {
	public String parent;
	public String type;
	public int decision;
	public float value;
	
	public String toString(){
		return "[ parent : " + parent + ", type : " + type + ", decision : " + decision + ", value : " + value + "]";
	}
}
