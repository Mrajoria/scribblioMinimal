package scribblioMinimal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
	
	public class draw extends JPanel {
		
	Scribble_gui scrbl;
	int x =-1;
	int y =-1;
	ArrayList listx= new ArrayList();
	ArrayList listy = new ArrayList();
	int index =0;
	boolean isclicked = false;
	String xycordinates;
	
	
    draw(Scribble_gui reference)
	{
		this.scrbl = reference;
	    
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				
				if(scrbl.connect == true) {
					
				x = e.getX();
				y = e.getY();
			
              	listx.add((int)x);
				listy.add((int)y);
			    System.out.print("LIST: "+x+" "+y);
			    System.out.print(" ");
			    
				repaint();
				
				}
			}

		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				listx.add(-1);
				listy.add(-1);
				
			}
		});
		
	
	}
    
  
    
	public void paintComponent(Graphics g) {
		
		
	    super.paintComponent(g);
		this.setBackground(Color.white);
		if(this.scrbl.connect == true) {
	    g.setColor(Color.red);
	 
	       for(int x=0;x<listx.size();x++) {
	        g.fillOval((int)listx.get(x), (int)listy.get(x), 1, 1);
	        System.out.print("POINTS "+(int)listx.get(x)+" "+(int)listy.get(x));
	        System.out.println();
	       
	    	int temp = x;
        	temp--;
        
	            if(x>=1 && (int)listx.get(temp) != -1 && (int)listy.get(temp) !=-1 &&(int)listx.get(x)!=-1 && (int)listy.get(x) != -1) {
	            	
	            	g.drawLine((int)listx.get(temp), (int)listy.get(temp), (int)listx.get(x),(int)listy.get(x));
	             }
	         }
	}
	}
	
	public void send(String message) {
		
		byte[] data = new byte[1024];
		data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, this.scrbl.ip, this.scrbl.portnum);
		try {
			this.scrbl.socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void recieve() {
		
	}
	
	}