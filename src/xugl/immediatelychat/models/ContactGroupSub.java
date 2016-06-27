package xugl.immediatelychat.models;

public class ContactGroupSub {
	private String ContactPersonObjectID;
	private String ContactGroupID;
	private Boolean IsDelete;
	private String UpdateTime;
	public String getContactPersonObjectID() {
		return ContactPersonObjectID;
	}
	public void setContactPersonObjectID(String contactPersonObjectID) {
		ContactPersonObjectID = contactPersonObjectID;
	}
	public String getContactGroupID() {
		return ContactGroupID;
	}
	public void setContactGroupID(String contactGroupID) {
		ContactGroupID = contactGroupID;
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
