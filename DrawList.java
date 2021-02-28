package scribblioMinimal;

import java.io.Serializable;
import java.util.ArrayList;

public class DrawList implements Serializable{
	
private static final long serialVersionUID = 6227933405944739586L;

private int Client_ID = -1;
public ArrayList<DrawCoordinates> list;

public DrawList(){
	this.list = new ArrayList<DrawCoordinates>();
}
public void setID(int ID) {
	this.Client_ID = ID;
	
}

public int getID() {
	return this.Client_ID;
}

}
