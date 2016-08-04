package xugl.immediatelychat.socket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.UDPSendModel;
import android.util.Log;

public abstract class SocketManage implements ISocketManage {
	private int maxSize = 8192;
	private ByteBuffer clientBuffer = ByteBuffer.allocate(maxSize);
	private Selector selector;
	private boolean isGoonRunning = true;
	private int port = 0;
	private CharsetDecoder decoder;
	private CharsetEncoder encoder;
	private DatagramChannel datagramChannel;
	private UDPSendModel[] needsendModels;

	private boolean IsChannelError = false;

	public SocketManage() {
		getSelector();
		Charset charset = Charset.forName("UTF-8");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
	}

	

	protected void getSelector() {
		Selector sel = null;
		try {
			if (selector != null && selector.isOpen()) {
				selector.close();
			}

			if (datagramChannel != null && datagramChannel.isOpen()) {
				datagramChannel.close();
			}

			datagramChannel = DatagramChannel.open();
			selector = Selector.open();
			BindPort(datagramChannel.socket());
			datagramChannel.configureBlocking(false);
			datagramChannel.register(sel, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public abstract UDPSendModel[] handleData(String msg);

	public void listen() {
		while (isGoonRunning) {
			try {
				selector.select();
				if (IsChannelError) {
					getSelector();
					IsChannelError = false;
					continue;
				}
				Iterator iter = selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = (SelectionKey) iter.next();
					if (key.isValid()) {
						process(key);
					}
					iter.remove();
				}
			} catch (IOException e) {
				e.printStackTrace();
				getSelector();
			}
		}

	}

	protected void process(SelectionKey key) throws IOException {
		UDPSendModel[] udpSendModels = null;
		try {
			if (key.isReadable()) { // 读信息
				DatagramChannel channel = (DatagramChannel) key.channel();
				int count = channel.read(clientBuffer);
				if (count > 0) {
					clientBuffer.flip();
					CharBuffer charBuffer = decoder.decode(clientBuffer);
					udpSendModels = handleData(charBuffer.toString());

					if (udpSendModels != null && udpSendModels.length>0) {
						SelectionKey sKey = channel.register(selector,
								SelectionKey.OP_WRITE);
						sKey.attach(udpSendModels);
					}
					// } else {
					// channel.close();
				}
				clientBuffer.clear();
			} else if (key.isWritable()) { // 写事件
				DatagramChannel channel = (DatagramChannel) key.channel();
				udpSendModels = (UDPSendModel[]) key.attachment();
				if(udpSendModels != null && udpSendModels.length>0)
				{
					for(int i =0 ; i<udpSendModels.length ; i++)
					{
						channel.send(encoder.encode(CharBuffer.wrap(udpSendModels[i]
								.getDatastr())),
								new InetSocketAddress(udpSendModels[i].getIP(),
										udpSendModels[i].getPort()));
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			if( udpSendModels!=null && udpSendModels.length>0)
			{
				needsendModels = udpSendModels;
			}
			getSelector();
		}
	}

	public void sendMsg(String Msg) {
		// System.out.println(name);
		SelectionKey sKey;
		try {
			sKey = datagramChannel.register(selector, SelectionKey.OP_WRITE);
			sKey.attach(Msg);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			IsChannelError = true;
			selector.wakeup();
			e.printStackTrace();
		}
	}

	private void BindPort(DatagramSocket datagramSocket) {
		Random random = new Random();
		port = random.nextInt(65535);

		try {
			datagramSocket.bind(new InetSocketAddress(port));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			BindPort(datagramSocket);
		}
	}

	// @Override
	// public String sendMsgWithReceive(String ip, int port, String msg) {
	// // TODO Auto-generated method stub
	// String info = null;
	// try {
	// byte[] tempbuffer = null;
	// tempbuffer = msg.getBytes("UTF-8");
	// DatagramPacket dp;
	// dp = new DatagramPacket(tempbuffer, tempbuffer.length,
	// InetAddress.getByName(ip), port);
	// CommonVariables.getDatagramSocket().
	// ds.setSoTimeout(5000);
	// dp = new DatagramPacket(buffer, buffer.length);
	// try
	// {
	// ds.receive(dp);
	// info = new String(dp.getData(), 0, dp.getLength(),"UTF-8");
	// ds.close();
	// return info;
	// }catch (IOException e) {
	// // TODO Auto-generated catch block
	// // e.printStackTrace();
	// }
	// } catch (SocketException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// public void sendMsg(String ip, int port, String msg) {
	// // TODO Auto-generated method stub
	// try {
	// byte[] tempbuffer = null;
	// tempbuffer = msg.getBytes("UTF-8");
	// DatagramPacket dp;
	// dp = new DatagramPacket(tempbuffer, tempbuffer.length,
	// InetAddress.getByName(ip), port);
	// ds.send(dp);
	// ds.close();
	// } catch (SocketException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
