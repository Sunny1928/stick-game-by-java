package Msg;

import character.GameFrame;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import character.Bullet;

public class New_Bullet_Msg implements Msg{
	private int msgType = Msg.NEW_BULLET_MSG;
	private GameFrame client;
	private Bullet bullet;
	
	public New_Bullet_Msg(GameFrame client) {
		this.client = client;
	}
	
	public New_Bullet_Msg(Bullet bullet) {
		this.bullet = bullet;
	}
	
	@Override
	public void send(DatagramSocket ds , String serverIP, int Server_UDP_Port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(30);
		DataOutputStream dos = new DataOutputStream(baos);
		
		try {
			dos.writeInt(msgType);
			dos.writeInt(bullet.getStickmanID());
			dos.writeInt(bullet.getID());
			dos.writeDouble(bullet.getBulletX());
			dos.writeDouble(bullet.getBulletY());
			dos.writeDouble(bullet.getValX());
			dos.writeDouble(bullet.getValY());
			
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket d_packet = new DatagramPacket(buf,buf.length,new InetSocketAddress(serverIP,Server_UDP_Port));
			ds.send(d_packet);
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	@Override
	public void parse(DataInputStream dis) {
		try {
			/*
			 * 判斷子彈的ID是從哪個stickman發送過來的
			 */
			int stickmanID = dis.readInt();
					
			if(stickmanID == client.getStickMan().getID()) {
				return;
			}
			
			
			int id = dis.readInt();
			Double update_x = dis.readDouble();
			Double update_y = dis.readDouble();
			Double valx = dis.readDouble();
			Double valy = dis.readDouble();
			
			
			Bullet newBullet = new Bullet(update_x, update_y, valx, valy, client);
			newBullet.setID(id);
			client.getController().addBullet(newBullet);
			
			System.out.println(client.getStickMan().getID());
			System.out.println("updatex: "+update_x+" updatey: "+update_y+" valx:"+valx+" valy"+valy);
			System.out.println("BulletList size"+client.getController().getBulletList().size());
 		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
