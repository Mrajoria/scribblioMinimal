package scribblioMinimal.server_cli;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {
	
DatagramSocket socket;
private int port;
public boolean connect;

	private boolean OpenConnection(int port) {
		this.port = port;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void recieve() {
		
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
	while(true) {	
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	String  recieved = new String(packet.getData());
	System.out.println(recieved);
	}
	}
	
	public static void main(String args[]) {
		
		Server s = new Server();
		
		s.connect = s.OpenConnection(Integer.parseInt(args[0]));
		
		if(s.connect == true) {
			System.out.println("Listening on port "+s.port);
			s.recieve();
		}
	}
	
	

}
