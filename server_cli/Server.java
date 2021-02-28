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
import java.util.Random;
import java.util.Scanner;

import scribblioMinimal.DrawList;

public class Server implements Runnable {

DatagramSocket socket;
private int port;
public boolean connect;
public boolean running = false;
private List<ServerClients> clients = new ArrayList<ServerClients>();
private List<Integer> clientResponse = new ArrayList<Integer>();
private final int MAX_ATTEMPTS =5;

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
	running = true;
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
		manage = new Thread("manage") {
			public void run() {
				while(running) {
					sendToAll("/i/server");

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("exception");
					}
				for(int i=0;i<clients.size();i++) {
					ServerClients c = clients.get(i);
					if(!clientResponse.contains(c.getID())){
						if(c.attempt >=MAX_ATTEMPTS) {
							disconnect(c.getID(), false);
						}
						else {
							c.attempt++;
						}
					}
					else {
						Object o = c.getID();
						clientResponse.remove(o);
						c.attempt = 0;
					}
				}
			}
			}
			
	};
	manage.start();
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
			Random r = new Random();
			counter = r.nextInt(10000);
			clients.add(new ServerClients(name,packet_recvd.getAddress(),packet_recvd.getPort(),counter));
			System.out.println("client conncected, UserName "+clients.get(clients.size()-1).Username+" from "+clients.get(clients.size()-1).a+" at port "+clients.get(clients.size()-1).port);
			System.out.println("client ID is "+clients.get(clients.size()-1).getID());
			String ID = "/Sc/"+counter+"/e/";
			send(ID, packet_recvd.getAddress(),packet_recvd.getPort());
		}
		else if(data_recvd.startsWith("/clr/")) {
			String message ="/clear/";
			sendToAll(message);
		}
		
		else if(data_recvd.startsWith("/d/")) {
			String ID = data_recvd.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(ID),true);
		}
		
		else if(data_recvd.startsWith("/ping/")) {
			clientResponse.add(Integer.parseInt(data_recvd.split("/ping/|/e/")[1]));
 
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
	
	public void disconnect(int ID, boolean status) {
		ServerClients client = null;
		boolean existed = false;
		for (int x =0; x<clients.size();x++) {
			if(clients.get(x).getID() == 	ID)
			{
			client = clients.get(x);
			clients.remove(x);
			existed = true;
			break;
			}
		}
		if(existed == false) return;
		String message = "";
		if(status == true) {
			message = "client "+ "( "+client.getID()+" ) @ "+client.a.toString()+ ":" +client.port+" disconnected";
		}
		else {
			message = "client "+ "( "+client.getID()+" ) @ "+client.a.toString()+ ":" +client.port+" timeout";
		}
		
		System.out.println(message);
		System.out.println("TOTAL CONNECTED CLIENTS "+clients.size());
	}
	
	public static void main(String args[]) {
		
		Server s = new Server(Integer.parseInt(args[0]));
		
	}


}
