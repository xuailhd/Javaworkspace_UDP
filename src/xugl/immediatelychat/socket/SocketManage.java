package xugl.immediatelychat.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Random;

import xugl.immediatelychat.common.CommonVariables;
import android.util.Log;

public class SocketManage implements ISocketManage {
	private int maxSize = 1024;
	private ByteBuffer clientBuffer = ByteBuffer.allocate(maxSize);   
	private Selector selector; 
	private boolean isGoonRunning;
	private int port =0;
	private CharsetDecoder decoder;   

	public SocketManage()
	{
		selector = getSelector();
		Charset charset = Charset.forName("UTF-8");   
		decoder = charset.newDecoder();   
	}
	
	protected Selector getSelector()
	{
		Selector sel = null;
		try {
			DatagramChannel datagramChannel = DatagramChannel.open();
			sel = Selector.open();
			BindPort(datagramChannel.socket());
			datagramChannel.configureBlocking(false);
			datagramChannel.register(sel, SelectionKey.OP_READ);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sel;
	}
	
	public void listen()
	{
		 try {   
	            while(isGoonRunning) {   
	                selector.select();   
	                Iterator iter = selector.selectedKeys().iterator();   
	                while (iter.hasNext()) {   
	                    SelectionKey key = (SelectionKey) iter.next();   
	                    iter.remove();   
	                    process(key);   
	                }   
	            }   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }  
	}
	
	protected void process(SelectionKey key) throws IOException {   
		String datastr = null;
		if (key.isReadable()) { // 读信息   
			DatagramChannel channel = (DatagramChannel) key.channel();   
            int count = channel.read(clientBuffer);   
            if (count > 0) {   
                clientBuffer.flip();   
                CharBuffer charBuffer = decoder.decode(clientBuffer);   
                datastr = charBuffer.toString();   
                // System.out.println(name);   
                SelectionKey sKey = channel.register(selector,   
                        SelectionKey.OP_WRITE);   
                sKey.attach(datastr);   
            } else {   
                channel.close();   
            }   


            clientBuffer.clear();   
        } else if (key.isWritable()) { // 写事件   
        	key.
            SocketChannel channel = (SocketChannel) key.channel();   
            String name = (String) key.attachment();   
               
            ByteBuffer block = encoder.encode(CharBuffer   
                    .wrap("Hello !" + name));   
               


            channel.write(block);   
        }   
    }   
	
	private void BindPort(DatagramSocket datagramSocket)
	{
		Random random = new Random();
		port = random.nextInt(65535);
		
		try {
			datagramSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			BindPort(datagramSocket);
		}
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
			CommonVariables.getDatagramSocket().
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
