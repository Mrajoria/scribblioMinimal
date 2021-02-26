package scribblioMinimal.server_cli;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import scribblioMinimal.DrawCoordinates;

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
		}
		else { 
			ArrayList<DrawCoordinates> list = null;
			
			try {
		    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(packet_recvd.getData()));
		    list = (ArrayList<DrawCoordinates>)inputStream.readObject();
		    
			System.out.println("showing list corrdinates X and Y ");
			for(int i=0;i<list.size();i++) {
		    System.out.println(list.get(i).getx()+" "+list.get(i).gety());
		    inputStream.close();
			}
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
