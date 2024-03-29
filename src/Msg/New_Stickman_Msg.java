package Msg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import character.StickMan;
import character.GameFrame;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class New_Stickman_Msg implements Msg{
	private int MsgType = Msg.NEW_STICKMAN_MSG;
	private StickMan stickman;
	private GameFrame client;
	
	public New_Stickman_Msg(GameFrame client) {
		this.client = client;
	}
	public New_Stickman_Msg(StickMan stickman) {
		this.stickman = stickman;
	}
	
	@Override
	public void send(DatagramSocket ds, String serverIP, int Server_UDP_Port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(30);
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(MsgType);
			dos.writeInt(stickman.getID());
			dos.writeInt(stickman.getStickManX());
			dos.writeInt(stickman.getStickManY());
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket d_packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(serverIP,Server_UDP_Port));
			ds.send(d_packet);
			
			
			System.out.println("msg send successfully");  //test
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			System.out.println("id"+id);
			
			if(id == this.client.getStickMan().getID()) {
				return;
			}
			
			int update_x = dis.readInt();
			int update_y = dis.readInt();
			
			StickMan newStickman = new StickMan(client,StickMan.initialX,StickMan.initialY);
			newStickman.setID(id);
			client.getStickManSet().add(newStickman);
			
			/*
			 * 在收到新stickman的訊息時，表示有新的客戶端加入，需要回傳自己存在
			 * 讓新客戶端將自己加進去。
			 */
			Stickman_Exist_Msg msg = new Stickman_Exist_Msg(this.client.getStickMan());
			this.client.getNetClient().send(msg);;
			
			
			System.out.println("msg parse successfully");  //test
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
