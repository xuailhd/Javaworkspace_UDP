package xugl.immediatelychat.models;

public class ContactPersonList {
	private String ContactPersonName;
	private String ObjectID;
	private String DestinationObjectID;
	private Boolean IsDelete;
	private String UpdateTime;
	public String getContactPersonName() {
		return ContactPersonName;
	}
	public void setContactPersonName(String contactPersonName) {
		ContactPersonName = contactPersonName;
	}
	public String getObjectID() {
		return ObjectID;
	}
	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}
	public String getDestinationObjectID() {
		return DestinationObjectID;
	}
	public void setDestinationObjectID(String destinationObjectID) {
		DestinationObjectID = destinationObjectID;
	}
	public Boolean getIsDelete() {
		return IsDelete;
	}
	public void setIsDelete(Boolean isDelete) {
		IsDelete = isDelete;
	}
	public String getUpdateTime() {
		return UpdateTime;
	}
	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}
	
	
}
