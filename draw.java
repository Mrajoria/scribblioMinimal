package scribblioMinimal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
	
	public class draw extends JPanel {
		
	Scribble_gui scrbl;
	int x =-1;
	int y =-1;
	ArrayList<DrawCoordinates> list = new ArrayList<DrawCoordinates>();
	int index =0;
	boolean isAuthorizedToken = false;
	String xycordinates;
	DatagramSocket socket;
	
    draw(Scribble_gui reference)
	{
		this.scrbl = reference;
	    
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				
				if(scrbl.connect == true && isAuthorizedToken == true ) {
					
				x = e.getX();
				y = e.getY();
			
              	list.add(new DrawCoordinates(x,y));
              	sendList(list);
              	
			    System.out.print("LIST: "+x+" "+y);
			    System.out.print(" ");
			    
				repaint();
				
				}
			}

		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if(isAuthorizedToken == true) {
				list.add(new DrawCoordinates(-1,-1));
				}
			}
		});
		
	
	}
    
  
    
	public void paintComponent(Graphics g) {
		
		
	    super.paintComponent(g);
		this.setBackground(Color.white);
		if(this.scrbl.connect == true) {
	    g.setColor(Color.red);
	 
	       for(int x=0;x<list.size();x++) {
	        g.fillOval((int)list.get(x).getx(), (int)list.get(x).gety(), 1, 1);
	        System.out.print("POINTS "+(int)list.get(x).getx()+" "+(int)list.get(x).gety());
	        System.out.println();
	       
	    	int temp = x;
        	temp--;
        
	            if(x>=1 && (int)list.get(temp).getx() != -1 && (int)list.get(temp).gety() !=-1 &&(int)list.get(x).getx()!=-1 && (int)list.get(x).gety() != -1) {
	            	
	            	g.drawLine((int)list.get(temp).getx(), (int)list.get(temp).gety(), (int)list.get(x).getx(),(int)list.get(x).gety());
	             }
	         }
	}
	}
	
	public void send(String message) {
		
		byte[] data = new byte[4096];
		data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, this.scrbl.ip, this.scrbl.portnum);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendList(ArrayList<DrawCoordinates> list) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(list);
		    byte[] data = new byte[100000];
            data = out.toByteArray();
		    DatagramPacket packet = new DatagramPacket(data, data.length, this.scrbl.ip, this.scrbl.portnum);
		    socket.send(packet);
		    outputStream.close(); 
		}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
		
	
	private void recieve() {
		
	}
	
	}