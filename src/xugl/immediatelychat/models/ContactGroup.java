package xugl.immediatelychat.models;

public class ContactGroup {
	private String GroupObjectID;
	private String GroupName;
	private Boolean IsDelete;
	private String UpdateTime;
	public String getGroupObjectID() {
		return GroupObjectID;
	}
	public void setGroupObjectID(String groupObjectID) {
		GroupObjectID = groupObjectID;
	}
	public String getGroupName() {
		return GroupName;
	}
	public void setGroupName(String groupName) {
		GroupName = groupName;
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
