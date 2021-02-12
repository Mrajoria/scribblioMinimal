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
			    
			    try {
					Thread.sleep(35);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			    
				repaint();
				
				}
			}

		});
		
	
	}
    
  
    
	public void paintComponent(Graphics g) {
		
		
	    super.paintComponent(g);
		this.setBackground(Color.white);
		if(this.scrbl.connect == true) {
	    g.setColor(Color.red);
	   
	    for(int x=0;x<listx.size();x++) {
	    	
	    	int y = x++;
	    	if(y<listx.size()-1) {
	       
		        g.fillOval((int)listx.get(x), (int)listy.get(x), 5, 5);

	        	g.drawLine((int)listx.get(x), (int)listy.get(x), (int)listx.get(y), (int)listy.get(y));
	        	System.err.print((int)listx.get(x)+" "+ (int)listy.get(x)+" "+ (int)listx.get(y)+" "+ (int)listy.get(y));
				System.err.println();
				
			
	        
	 
	        g.fillOval((int)listx.get(x), (int)listy.get(x), 5, 5);
	        System.out.print("POINTS "+(int)listx.get(x)+" "+(int)listy.get(x));
	        System.out.println();
	    	
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