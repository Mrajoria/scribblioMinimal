package scribblioMinimal.server_cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import scribblioMinimal.DrawList;

public class Server implements Runnable {

DatagramSocket socket;
private int port;
public boolean connect;
private List<ServerClients> clients = new ArrayList<ServerClients>();
private int counter = 0;

private Thread serverRun, recieve, manage;

Server(int port){
	this.port = port;
	this.connect = OpenConnection(port);
	if(this.connect == true) {
		System.out.println("Listening o/n port "+this.port);
		recieve();
	}
	
	serverRun = new Thread(this,"server");
	serverRun.start();
}

public void run() {
	manage();
	recieve();
	 boolean breakvar = true;
	   Scanner scanner = new Scanner(System.in);
	   while(breakvar) {
		   String text = scanner.nextLine();
		   if(text.contains("/clients")) {
			   System.out.println("......................................");
			   for(int i=0;i<clients.size();i++) {
				  System.out.println(clients.get(i).Username+" from "+clients.get(i).a.toString()+" at Port "+clients.get(i).port+" ID" +clients.get(i).getID());
			   }
			   System.out.println("......................................");
		   }
		   if(text.equals("/break")) breakvar = !breakvar;
	   }
	   scanner.close();
 
}
   public void manage() {
	   
   }
	
	public void recieve() {
    recieve = new Thread("recieve") {
			public void run() {
				while(true) {
					byte[] data = new byte[100000];
					DatagramPacket packet = new DatagramPacket(data, data.length);	
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}	
		     process(packet);
			}
			}
		};
		recieve.start();
	}
	
	
	public void process(DatagramPacket packet_recvd) {
		String data_recvd = new String(packet_recvd.getData());
		
		if(data_recvd.startsWith("/c/")) {
			String name = data_recvd.split("/c/|/e/")[1];
			counter++;
			clients.add(new ServerClients(name,packet_recvd.getAddress(),packet_recvd.getPort(),counter));
			System.out.println("client conncected, UserName "+clients.get(counter-1).Username+" from "+clients.get(counter-1).a+" at port "+clients.get(0).port);
			System.out.println("client ID is "+clients.get(counter-1).getID());
			String ID = "/Sc/"+counter+"/e/";
			send(ID, packet_recvd.getAddress(),packet_recvd.getPort());
		}
		else if(data_recvd.startsWith("/clr/")) {
			String message ="/clear/";
			sendToAll(message);
		}
		
		else { 
			
		 
			try {
		    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(packet_recvd.getData()));
		   
		    DrawList listObject = (DrawList)inputStream.readObject();
		    
		    inputStream.close();

			System.out.println("showing list corrdinates X and Y and Who sent "+listObject.getID());
			for(int i=0;i<listObject.list.size();i++) {
		    System.out.println(listObject.list.get(i).x+" "+listObject.list.get(i).y);
		    }
			sendToAllList(listObject);
			listObject = null;
		   
			}
			     catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
			}
			 
		
	}
	
	public void send(String message, InetAddress a, int port) {
		byte[]data = new byte[1024];
		data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length,a, port);
		try {
			socket.send(packet);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
    public void sendToAll(String message) {
    	for(int i=0;i<clients.size();i++) {
    		try {
    			byte[] data = new byte[1024];
    			data = message.getBytes();
    			DatagramPacket packet = new DatagramPacket(data,data.length,clients.get(i).a, clients.get(i).port);
    			socket.send(packet);
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
	
	public void sendToAllList(DrawList listObject) {
		int forbiddenID = listObject.getID();
		for(int i=0;i<clients.size();i++) {
	      if(clients.get(i).getID() ==forbiddenID) {
			System.out.println("we wont be sending this to ID "+clients.get(i).getID()+" "+clients.get(i).Username);
	      }
	      else {
	    	  System.out.println("sending to clients "+clients.get(i).Username);
	    	  try {
	    		  listObject.setID(clients.get(i).getID());
					byte[] data = new byte[100000];
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream OBJos = new ObjectOutputStream(bos);
					OBJos.writeObject(listObject);
					data = bos.toByteArray();
					DatagramPacket packet = new DatagramPacket(data, data.length,clients.get(i).a, clients.get(i).port);
					socket.send(packet);
					OBJos.close();
					bos.close();
				}catch (IOException e) {

					e.printStackTrace();
				}
	      }
	      
		}
	}
		
	public boolean OpenConnection(int port) {
		this.port = port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void main(String args[]) {
		
		Server s = new Server(Integer.parseInt(args[0]));
		
	}
	
	

}
