package scribblioMinimal;

import java.io.Serializable;

public class DrawCoordinates implements Serializable  {
private static final long serialVersionUID = 4461510354735551162L;
public int x;
public int y;

DrawCoordinates(int x, int y){
	this.x = x;
	this.y = y;
}


}
