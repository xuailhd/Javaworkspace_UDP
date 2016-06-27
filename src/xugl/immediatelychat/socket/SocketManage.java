package xugl.immediatelychat.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import android.util.Log;

public class SocketManage implements ISocketManage {
	byte[] buffer = new byte[1024];
	
	private int port =0;
	private DatagramSocket ds = null;
	public SocketManage()
	{
	}
	
	@Override
	public String sendMsgWithReceive(String ip, int port, String msg) {
		// TODO Auto-generated method stub
		String info = null;
		try {
			byte[] tempbuffer = null;
			tempbuffer = msg.getBytes("UTF-8");
			DatagramPacket dp;
			dp = new DatagramPacket(tempbuffer, tempbuffer.length, 
				InetAddress.getByName(ip), port);
			ds.send(dp);
			ds.setSoTimeout(5000);
			dp = new DatagramPacket(buffer, buffer.length);
			try
			{
				ds.receive(dp);
				info = new String(dp.getData(), 0, dp.getLength(),"UTF-8");  
				ds.close();
				return info;
			}catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void sendMsg(String ip, int port, String msg) {
		// TODO Auto-generated method stub
		try {
			byte[] tempbuffer = null;
			tempbuffer = msg.getBytes("UTF-8");
			DatagramPacket dp;
			dp = new DatagramPacket(tempbuffer, tempbuffer.length, 
				InetAddress.getByName(ip), port);
			ds.send(dp);
			ds.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
