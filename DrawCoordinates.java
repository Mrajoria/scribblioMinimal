package scribblioMinimal;

import java.io.Serializable;
import java.util.ArrayList;

public class DrawCoordinates implements Serializable {
	
private static final long serialVersionUID = 1L;

private int x;
private int y;

DrawCoordinates(int x, int y){
	this.x = x;
	this.y = y;
}

public int getx() {
	return this.x;
}

public int gety() {
	return this.y;
	
}


}
