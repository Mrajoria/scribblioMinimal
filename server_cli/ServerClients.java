package scribblioMinimal.server_cli;

import java.net.InetAddress;

public class ServerClients {
	
	public InetAddress a;
	public int port;
	public String Username;
	final private int ID;
	
public ServerClients(String Username,InetAddress a,int port, final int ID){
	this.a =a;
	this.port = port;
	this.Username = Username;
	this.ID = ID;
}

public int getID () {
	return ID;
}
}