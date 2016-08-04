package xugl.immediatelychat.models;

public class UDPSendModel {
	private String datastr;
	private String ip;
	private int port;

	public UDPSendModel() {
	}

	public UDPSendModel(String datastr, String ip, int port) {
		this.datastr = datastr;
		this.ip = ip;
		this.port = port;
	}

	public String getDatastr() {
		return datastr;
	}

	public void setDatastr(String datastr) {
		this.datastr = datastr;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String iP) {
		this.ip = iP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
