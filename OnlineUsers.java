package scribblioMinimal;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OnlineUsers extends JPanel {

	
	
	 String path = "resources/icons";
	 int lastUserCount =0;
	 class Users{
		 int x;
		 int y;
		 BufferedImage users;
		 
	 }
	 
	 List<Users> Online = new ArrayList<Users>();
	 
	public OnlineUsers(){
		Online = new ArrayList<Users>();
	}
	
	public void showOnline(String[] incoming) {
		
			if(incoming.length >lastUserCount) {
				for(int i=lastUserCount;i<incoming.length;i++) {
					 Online.add(new Users());
					 lastUserCount = Online.size();
					 if(lastUserCount ==1) {
					 Online.get(i).x = 150;
					 Online.get(i).y =5;
                     repaint();		 
					 }
					 else if(lastUserCount >1) {
						 int temp = i;
						 temp--;
						 int increment = Online.get(temp).x;
						 increment+=70;
						 Online.get(i).x =  Online.get(i).x +increment;
						 Online.get(i).y =5;
                         repaint();
					 }
				}
			}
			
			if(incoming.length <lastUserCount) {
				for(int i=incoming.length;i<lastUserCount;i++) {
					Online.remove(Online.size()-1);
					lastUserCount = Online.size();
					repaint();
				}
			}
		
		}
	
	public void paintComponent(Graphics g) {
		BufferedImage img = new BufferedImage(57,57,BufferedImage.TYPE_INT_RGB);
		try {
			img = ImageIO.read(getClass().getResource("resources/icons/OnlineUserscIcon.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.drawImage(img,67,5,this );
		
	
       if(Online!= null ) {
		for(int i=0;i<Online.size();i++) {
			System.out.println(Online.get(i));
			Online.get(i).users = new BufferedImage(57,57,BufferedImage.TYPE_INT_RGB);
			path = path+"/"+Integer.toString(i)+".png";
			try {
				Online.get(i).users = ImageIO.read(getClass().getResource(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("The Current index is "+i+" path is- "+path);
			g.drawImage(Online.get(i).users,Online.get(i).x,Online.get(i).y,this );
		    path = "resources/icons";
		    
		}
	    }
		
	}

 
}
