package scribblioMinimal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
	
public class draw extends JPanel  {

	Scribble_gui scrbl;
	int x =-1;
	int y =-1;
	DrawList listObject = new DrawList();
	boolean isAuthorizedToken = false;
	private int ID;
	
	DatagramSocket socket;
    Thread clientRecieve;
    boolean recvRunning = true;
	
    draw(Scribble_gui reference)
	{   
    	
		this.scrbl = reference;
	    
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				
				if(scrbl.connect == true && isAuthorizedToken == true ) {
					
				x = e.getX();
				y = e.getY();
			
              	listObject.list.add(new DrawCoordinates(x,y));
              	repaint();
              	sendList(listObject);
			    System.out.print("LIST: "+x+" "+y);
			    System.out.print(" ");
			
				}
			}

		});
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if(scrbl.connect == true && isAuthorizedToken == true) {
				listObject.list.add(new DrawCoordinates(-1,-1));
				}
			}
		});
		
	}
   

	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
		this.setBackground(Color.white);
		if(this.scrbl.connect == true) {
	    g.setColor(Color.red);
	 
	       for(int x=0;x<listObject.list.size();x++) {
	        g.fillOval((int)listObject.list.get(x).x, (int)listObject.list.get(x).y, 1, 1);
	        System.out.print("POINTS "+(int)listObject.list.get(x).x+" "+(int)listObject.list.get(x).y);
	        System.out.println();
	       
	    	int temp = x;
        	temp--;
        
	            if(x>=1 && (int)listObject.list.get(temp).x != -1 && (int)listObject.list.get(temp).y !=-1 &&(int)listObject.list.get(x).x!=-1 && (int)listObject.list.get(x).y != -1) {
	            	
	            	g.drawLine((int)listObject.list.get(temp).x, (int)listObject.list.get(temp).y, (int)listObject.list.get(x).x,(int)listObject.list.get(x).y);
	             }
	         }
	}
	}
	
	public void send(String message) {
		
		byte[] data = new byte[1024];
		data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, this.scrbl.ip, this.scrbl.portnum);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendList(DrawList someListObject) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(out);
			outputStream.reset();
			outputStream.writeObject(someListObject);
		    byte[] data = new byte[100000];
            data = out.toByteArray();
		    DatagramPacket packet = new DatagramPacket(data, data.length, this.scrbl.ip, this.scrbl.portnum);
		    socket.send(packet);
		    outputStream.close();
		    out.close();
		}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 public void recieve() {
	clientRecieve =  new Thread("Clientrecieve") {
		 public void run() {
			 while(recvRunning) {
				 byte[] data= new byte[100000];
				 DatagramPacket packet = new DatagramPacket(data, data.length);
				 try {
					socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				 packet_process(packet);
			 }
		 }
	 };
		clientRecieve.start();
	}
	 
	 public void packet_process(DatagramPacket packet) {
		 String incoming = new String(packet.getData());
		 if(incoming.startsWith("/Sc/")){
			 String text = incoming.split("/Sc/|/e/")[1];
			 listObject.setID(Integer.parseInt(text));
			 System.out.println("Client ID recieved and set on listObject "+listObject.getID());
			 setIDforThisClient(Integer.parseInt(text));
		 }
		 else if(incoming.startsWith("/clear/")){
			 listObject.list = null;
			 listObject.list = new ArrayList<DrawCoordinates>();
			 repaint();
		 }
		 else if(incoming.startsWith("/i/server")) {
			 String ping = "/ping/"+this.getID()+"/e/";
			 send(ping);
		 }
		 	
		 else {
		
			 ByteArrayInputStream inputS = new ByteArrayInputStream(packet.getData());
			 try {
				ObjectInputStream objIstream = new ObjectInputStream(inputS);
				listObject = (DrawList)objIstream.readObject();
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 catch(ClassNotFoundException  e) {
				 e.printStackTrace();
			 }
			 SwingUtilities.invokeLater(new Thread() {
				 public void run() {
					 System.out.println("ID for this listObject is "+listObject.getID());
					 System.err.print("ID for this ListObject SHOULD BE "+getID());
					repaint();
				 }
			 });	 
		 }
		 
	 }
	 public void close() {
		 new Thread("socketClose") {
			 public void run() {
				 socket.close();
			 }
		 }.start();
	 }
	 public int getID() {
		 return ID;
	 }
	 
	 public void setIDforThisClient(int ID) {
		 this.ID = ID;
	 }

	}