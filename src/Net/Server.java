package Net;

import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	/**
	 * @param ID 			  is used to allocate ID to client
	 * @param TCP_PORT        is a Server's TCP socket port which is a fixed number.
	 * @param DISCONNECT_PORT is used as TCP port in Disconnect thread.
	 * @param BUFFER_SIZE     is specified to MAX size of packet.
	 * @param list            is used to record client online.
	 */
	
	public static int id = 100;  //new
	private static final int TCP_PORT = 10000;
	private static final int Disconnect_Port = 10001;
	private static final int UDP_PORT = 10002;
	private static final int BUFSIZE = 4096;
	private final List<Client_SInfo> list = new ArrayList<Client_SInfo>();
	private int mapID;
	private int mapImageID;

	public static void main(String[] args) {
		Server server = new Server();
		System.out.println("test");
	}

	private Server() {

		/*
		 * Create ServerSocket to listen to connection.
		 */
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(TCP_PORT);
			System.out.println("Server is created , waiting for connection...");
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		DisconnectThread dis_th = new DisconnectThread();
		new Thread(dis_th).start();
		
		UDPThread UDP_thread = new UDPThread();
		new Thread(UDP_thread).start();
		
		
		while (true) {
			Socket s = null;
			try {

				s = ss.accept();
				System.out.println("connected from client " + s.getInetAddress().getHostAddress());

				
				System.out.println("Create UDP socket for this Client");

				/**
				 * @param instream  is used to receive Client UDP socket's info to Server side
				 *                  by TCP socket.
				 * @param outstream is used to send Server side UDP socket's info to Client side
				 *                  by TCP socket.
				 */
				DataInputStream instream = new DataInputStream(s.getInputStream());
				DataOutputStream outstream = new DataOutputStream(s.getOutputStream());
				
				
				if(list.size() == 0) {
					mapID = randomMapID();
					mapImageID =  randomMapImageID();
				}
//				System.out.println(mapID);
//				System.out.println(mapImageID);
//				System.out.println(list.size());
				
				/*
				 * Read Client side ip and UDP_port from instream, and add client info to client
				 * list
				 */
				String Client_IP = (String) (instream.readUTF());
				int Client_UDP_Port = instream.readInt();
				Client_SInfo client = new Client_SInfo(Client_IP, Client_UDP_Port, id);
				list.add(client);
				
				
				/*
				 * Write Server side ip, UDP_port, id to Client side.
				 */
				outstream.writeUTF(InetAddress.getLocalHost().getHostAddress());
				outstream.writeInt(UDP_PORT);
//				outstream.writeInt(UDP_thread.ds.getLocalPort());
				outstream.writeInt(id++);
				outstream.writeInt(mapID);
				outstream.writeInt(mapImageID);

				// test list
				System.out.println(list);
			}

			catch (IOException ioe) {

				System.err.println("Error occurs opening socket...");
				ioe.printStackTrace();
			}
			/*
			 * Free the resource of TCP socket.
			 */
			finally {

				try {
					if (s != null) {
						s.close();
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

	}

	/*
	 * UDPThread is used to receive packet from Client, and then send packet through
	 * UDP socket to every client online.
	 */
	private class UDPThread implements Runnable {
		DatagramSocket ds;

		public UDPThread() {
			try {
				/*
				 * When object is created, create a UDP socket for transmission, set UDP_Port
				 * for the object, and then we can return port number to Client.
				 */
				
				this.ds = new DatagramSocket(UDP_PORT);
				
//				this.ds = new DatagramSocket();
				
				System.out.println(ds.getLocalPort());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		@Override
		public void run() {
			byte[] buf = new byte[BUFSIZE];
			
			System.out.println("ds"+this.ds);// test
			while (ds != null) {
				DatagramPacket d_packet = new DatagramPacket(buf, buf.length);

				/**
				 * DatagramSocket.receive() method will block the thread, so we can not insert
				 * it in synchronized block, or the thread which get CPU control will occupy the
				 * right to use list object,thus causes other thread cannot receive packet in
				 * synchronized block.
				 */
				try {
					ds.receive(d_packet); 
//					System.out.println("packet received!");
					
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				synchronized (list) {
					try {
						/*
						 * str below is used to test;
						 */
						String str = new String(d_packet.getData(), 0, d_packet.getLength());
						System.out.println(str);
						
						for (Client_SInfo c : list) {
							d_packet.setSocketAddress(new InetSocketAddress("192.168.1.255", c.UDP_PORT));
							ds.send(d_packet);							
						}
						 
//						System.out.println("packet send!");//test

					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * DisconnectThread is used to remove connection between server and client
	 */
	private class DisconnectThread implements Runnable {
		byte[] buf = new byte[BUFSIZE];
		DatagramSocket ds = null;

		@Override
		public void run() {

			try {
				ds = new DatagramSocket(Disconnect_Port);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			while (ds != null) {

				DatagramPacket d_packet = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(d_packet);
				} 
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				/*
				 * The one who has same port number in list with client that sends
				 * DisconnectRequest, it's the client we want to remove.
				 */
				synchronized(list){					
					String Remove_Client_IP = d_packet.getAddress().getHostAddress();
					int Remove_client_UDP_Port = d_packet.getPort();
					
					for (Client_SInfo c : list) {
						if (c.UDP_PORT == Remove_client_UDP_Port) {		
							list.remove(c);
							System.out.println("Remove Client : "+Remove_Client_IP);
							break;
						}
					}
				}
				
				/*
				 * TBD : parse message from d_packet, remove connection from client, and send
				 * removing message back to each client online.
				 */
			}
		}
	}

	private class Client_SInfo {
		String ip;
		int UDP_PORT;
		int id;
		
		Client_SInfo(String ip, int UDP_PORT, int id) {
			this.ip = ip;
			this.UDP_PORT = UDP_PORT;
			this.id = id;
		}
	}
	
	private int randomMapID() {
		int r = (int)(Math.random()*3);
		return r;
	}
	
	private int randomMapImageID() {
		int r = (int)(Math.random()*2);
		return r;
	}
}
