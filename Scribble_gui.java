
package scribblioMinimal;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Scribble_gui extends JFrame{

	private JPanel childpanel1, cp1, cp2, cp3;
	draw drawpanel;
	private JButton drawButton;
	private JButton sendButton;
	private JButton clearButton;
	private JLabel UserName;
	private JTextField name;
	private JLabel ipadrs;
	private JTextField ipfield;
	private JLabel port;
	private JTextField portfield;
	
	public String ipaddress;
	public int portnum;
	public String uName;
	
	public boolean connect = false;
	InetAddress ip;
	
	Scribble_gui(){
		this.setLayout(new GridBagLayout());
		childpanel1 = new JPanel();
		drawpanel = new draw(this);
		drawButton = new JButton();
		sendButton = new JButton();
		clearButton = new JButton("Clear");
		showGui();
	}
	
	public void showGui() {
		
		childpanel1.setForeground(Color.red);
		
		Dimension childSize = new Dimension(150,100);
		UserName = new JLabel("        UserName        ");
		name = new JTextField();
		cp1 = new JPanel();
		cp1.setLayout(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		cp1.setPreferredSize(childSize);
		gbc1.gridx =0;
		gbc1.gridy =0;
		cp1.add(UserName,gbc1);
		gbc1.gridx =0;
		gbc1.gridy =1;
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		cp1.add(name,gbc1);


		ipadrs = new JLabel("    IP Address   ");
		ipfield = new JTextField();
		cp2 = new JPanel();
		cp2.setPreferredSize(childSize);
		cp2.setLayout(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx =0;
		gbc2.gridy =0;
		cp2.add(ipadrs,gbc2);
		gbc2.gridx =0;
		gbc2.gridy =1;
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		cp2.add(ipfield,gbc2);



		port = new JLabel("  Port  ");
		portfield = new JTextField("");
		cp3 = new JPanel();
		cp3.setLayout(new GridBagLayout());
		cp3.setPreferredSize(childSize);
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx =0;
		gbc3.gridy =0;
		cp3.add(port,gbc3);
		gbc3.gridx =0;
		gbc3.gridy =1;
		gbc3.fill = GridBagConstraints.HORIZONTAL;

		cp3.add(portfield,gbc3);

		childpanel1 = new JPanel();
		childpanel1.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy =0;
		gbc.weightx = 1;
		gbc.weighty =0.3;
		childpanel1.add(cp1,gbc);


		gbc.gridx = 0;
		gbc.gridy =1;
		gbc.weightx = 1;
		gbc.weighty =0.3;
		childpanel1.add(cp2,gbc);

		gbc.gridx = 0;
		gbc.gridy =2;
		gbc.weightx = 1;
		gbc.weighty =0.3;
		childpanel1.add(cp3,gbc);
		
		sendButton= new JButton("Login");
		sendButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			
			String somename = name.getText();
			String ipaddrs = ipfield.getText();
			int portnumber = Integer.parseInt(portfield.getText());
			
		    connect = OpenConnection(somename, ipaddrs,portnumber );
			if(connect) {	
			System.out.println("socket binding succesfull");
			String message = "/c/"+somename+"/e/";
			drawpanel.send(message);
		    drawpanel.recieve();
			remove();
			}
			}
		});

		gbc.gridx = 0;
		gbc.gridy =3;
		gbc.weightx = 1;
		gbc.weighty =1;;
		childpanel1.add(sendButton,gbc);
	
	
		drawpanel.setBackground(Color.white);
		drawpanel.setLayout(new GridBagLayout());
		Dimension cp2 = new Dimension(500,400);
		drawpanel.setPreferredSize(cp2);
		drawpanel.setBorder(BorderFactory.createLineBorder(Color.red));
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
    	gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		drawButton.setSize(57, 57);
		drawButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				drawpanel.isAuthorizedToken = !drawpanel.isAuthorizedToken;
			}
		});
		 try {
			    Image img = ImageIO.read(getClass().getResource("resources/0.png"));
			    drawButton.setIcon(new ImageIcon(img));
			  } catch (Exception ex) {
			    System.out.println(ex);
			  }
		 drawpanel.add(drawButton,gbc);
		 gbc.anchor = GridBagConstraints.CENTER;
		 gbc.gridx = 0;
		 gbc.gridy = 1;
		 gbc.weightx = 1;
		 gbc.weighty = 0;
		 clearButton.setSize(100, 100);
		 clearButton.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				 String clearmessage = "/clr/";
				 drawpanel.send(clearmessage);
			 }
		 });
		 drawpanel.add(clearButton,gbc);
		
	
		
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx =0;
		gbc.gridy =0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		this.add(childpanel1,gbc);
		
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx =1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(drawpanel,gbc);
		this.pack();
		this.setVisible(true);
	    this.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		String disconnect = "/d/"+drawpanel.getID()+"/e/";
	    		drawpanel.send(disconnect);
	    		drawpanel.close();
	    		drawpanel.recvRunning = false;
	    		dispose();
	    	}
	    });
	}
	
	public void remove() {
		this.remove(childpanel1);
		this.setTitle(uName);
		revalidate();
	}
	
	private boolean OpenConnection(String name, String ipadrs, int port) {
		this.uName = name;
		this.ipaddress = ipadrs;
		this.portnum = port;
		
		try {
			drawpanel.socket = new DatagramSocket();
		    ip =   InetAddress.getByName(ipadrs);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	 public static void main(String args[]) {
		SwingUtilities.invokeLater(new Thread() {
			public void run() {
			Scribble_gui gui =	new Scribble_gui();
		
			//gui.drawpanel.repaint();
			}
		});
		
	}
}
