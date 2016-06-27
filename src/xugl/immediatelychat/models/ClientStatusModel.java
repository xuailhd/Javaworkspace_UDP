package xugl.immediatelychat.models;


public class ClientStatusModel{
	private String ObjectID;
	private String MCS_IP;
	private int MCS_Port;
	public String getObjectID() {
		return ObjectID;
	}
	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}
	public String getMCS_IP() {
		return MCS_IP;
	}
	public void setMCS_IP(String mCS_IP) {
		MCS_IP = mCS_IP;
	}
	public int getMCS_Port() {
		return MCS_Port;
	}
	public void setMCS_Port(int mCS_Port) {
		MCS_Port = mCS_Port;
	}
	
}
