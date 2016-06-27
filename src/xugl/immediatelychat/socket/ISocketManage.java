package xugl.immediatelychat.socket;

public interface ISocketManage {
	String sendMsgWithReceive(String ip,int port,String msg);
	void sendMsg(String ip,int port,String msg);
}
