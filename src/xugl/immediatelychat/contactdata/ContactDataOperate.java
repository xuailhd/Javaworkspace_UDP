package xugl.immediatelychat.contactdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ContactGroup;
import xugl.immediatelychat.models.ContactGroupSub;
import xugl.immediatelychat.models.ContactPersonList;

public class ContactDataOperate implements IContactDataOperate {
	public String SaveContactData(String objectID,JSONObject contactData,Context packageContext)
	{
		String contactDataID=null;
		try {
			String localDataStr=null;
			JSONObject jsonObject=null;
			JSONArray jsonArray=null;
			int i=0;
			int findindex=-1;
	
			if(contactData.getInt("DataType")==1)
			{
				localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPersonLists", packageContext);
				
				if(localDataStr==null)
				{
					jsonArray=new JSONArray();
				}
				else
				{
					jsonArray=new JSONArray(localDataStr);
				}
				
				
				while(i<jsonArray.length())
				{
					if(jsonArray.getJSONObject(i).getString("DestinationObjectID").equalsIgnoreCase(contactData.getString("DestinationObjectID"))
							&& jsonArray.getJSONObject(i).getString("ObjectID").equalsIgnoreCase( contactData.getString("ObjectID")))
					{
						findindex=i;
						break;
					}
					i++;
				}
				
				if(findindex>=0)
				{
					jsonArray.getJSONObject(findindex).put("IsDelete", contactData.getBoolean("IsDelete"));
					jsonArray.getJSONObject(findindex).put("ContactPersonName", contactData.getString("ContactPersonName"));
				}
				else
				{
					jsonObject=new JSONObject();

					jsonObject.put("ObjectID", contactData.getString("ObjectID"));				
					jsonObject.put("DestinationObjectID", contactData.getString("DestinationObjectID"));
					jsonObject.put("ContactPersonName", contactData.getString("ContactPersonName"));
					jsonObject.put("IsDelete", contactData.getBoolean("IsDelete"));
					jsonArray.put(jsonObject);
				}
				
				CommonVariables.getLocalDataManager().SaveData(objectID,"ContactPersonLists", jsonArray.toString(), packageContext);
			}
			
			if(contactData.getInt("DataType")==2)
			{
				localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactGroups", packageContext);
				if(localDataStr==null)
				{
					jsonArray=new JSONArray();
				}
				else
				{
					jsonArray=new JSONArray(localDataStr);
				}
				
				while(i<jsonArray.length())
				{
					if(jsonArray.getJSONObject(i).getString("GroupObjectID").equalsIgnoreCase(contactData.getString("GroupObjectID")))
					{
						findindex=i;
						break;
					}
					i++;
				}
				
				if(findindex>=0)
				{
					jsonArray.getJSONObject(findindex).put("GroupName", contactData.getString("GroupName"));
					jsonArray.getJSONObject(findindex).put("IsDelete", contactData.getBoolean("IsDelete"));
				}
				else
				{
					jsonObject=new JSONObject();

					jsonObject.put("GroupObjectID", contactData.getString("GroupObjectID"));				
					jsonObject.put("GroupName", contactData.getString("GroupName"));
					jsonObject.put("IsDelete", contactData.getBoolean("IsDelete"));
					jsonArray.put(jsonObject);
				}
				
				CommonVariables.getLocalDataManager().SaveData(objectID,"ContactGroups", jsonArray.toString(), packageContext);
			}

			if(contactData.getInt("DataType")==3)
			{
				localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactGroupSubs", packageContext);
				if(localDataStr==null)
				{
					jsonArray=new JSONArray();
				}
				else
				{
					jsonArray=new JSONArray(localDataStr);
				}
				
				while(i<jsonArray.length())
				{
					if(jsonArray.getJSONObject(i).getString("ContactPersonObjectID").equalsIgnoreCase(contactData.getString("ContactPersonObjectID"))
							&& jsonArray.getJSONObject(i).getString("ContactGroupID").equalsIgnoreCase( contactData.getString("ContactGroupID")))
					{
						findindex=i;
						break;
					}
					i++;
				}
				
				if(findindex>=0)
				{
					jsonArray.getJSONObject(findindex).put("IsDelete", contactData.getBoolean("IsDelete"));
				}
				else
				{
					jsonObject=new JSONObject();

					jsonObject.put("ContactPersonObjectID", contactData.getString("ContactPersonObjectID"));				
					jsonObject.put("ContactGroupID", contactData.getString("ContactGroupID"));
					jsonObject.put("IsDelete", contactData.getBoolean("IsDelete"));
					jsonArray.put(jsonObject);
				}
				
				CommonVariables.getLocalDataManager().SaveData(objectID,"ContactGroupSubs", jsonArray.toString(), packageContext);
			}
			
//			if(contactData.getInt("DataType")==0)
//			{
//				localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPerson", packageContext);
//
//				if(localDataStr==null)
//				{
//					jsonObject=new JSONObject();
//				}
//				else
//				{
//					jsonObject=new JSONObject(localDataStr);
//				}
//				
//				CommonVariables.getLocalDataManager().SaveData(objectID,"ContactPerson", jsonObject.toString(), packageContext);
//			}
			
			if(contactData.has("UpdateTime"))
			{
				if(CommonVariables.getUpdateTime().compareTo(contactData.getString("UpdateTime"))<0)
				{
					CommonVariables.setUpdateTime(contactData.getString("UpdateTime"));
					
					localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPerson", packageContext);
					
					if(localDataStr==null)
					{
						jsonObject=new JSONObject();
					}
					else
					{
						jsonObject=new JSONObject(localDataStr);
					}
					jsonObject.put("UpdateTime",  contactData.getString("UpdateTime"));
					CommonVariables.getLocalDataManager().SaveData(objectID,"ContactPerson", jsonObject.toString(), packageContext);
				}
			}
			
			localDataStr=null;
			jsonObject=null;
			jsonArray=null;
			
			contactDataID= contactData.getString("ContactDataID");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactDataID;
	}

	public ContactPersonList[] LoadContactPersonList(String objectID,Context packageContext)
	{
		String localDataStr=null;
		JSONArray jsonArray=null;
		ContactPersonList[] contactPersonLists=null;
		ContactPersonList contactPersonList=null;
		int i=0;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPersonLists", packageContext);

			if(localDataStr==null)
			{
				return contactPersonLists;
			}
			
			jsonArray=new JSONArray(localDataStr);
			
			contactPersonLists=new ContactPersonList[jsonArray.length()];
			
			while(i<jsonArray.length())
			{
				contactPersonList=new ContactPersonList();
				contactPersonList.setContactPersonName(jsonArray.getJSONObject(i).getString("ContactPersonName"));
				contactPersonList.setDestinationObjectID(jsonArray.getJSONObject(i).getString("DestinationObjectID"));
				contactPersonList.setIsDelete(jsonArray.getJSONObject(i).getBoolean("IsDelete"));
				contactPersonList.setObjectID(jsonArray.getJSONObject(i).getString("ObjectID"));
				contactPersonLists[i]=contactPersonList;
				i++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonArray=null;
		return contactPersonLists;
	}
	
	public ContactGroup[] LoadContactGroup(String objectID,Context packageContext)
	{
		String localDataStr=null;
		JSONArray jsonArray=null;
		ContactGroup[] contactGroups=null;
		ContactGroup contactGroup;
		int i=0;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactGroups", packageContext);

			if(localDataStr==null)
			{
				return contactGroups;
			}
			jsonArray=new JSONArray(localDataStr);
			
			contactGroups=new ContactGroup[jsonArray.length()];
			
			while(i<jsonArray.length())
			{
				contactGroup=new ContactGroup();
				contactGroup.setGroupName(jsonArray.getJSONObject(i).getString("GroupName"));
				contactGroup.setGroupObjectID(jsonArray.getJSONObject(i).getString("GroupObjectID"));
				contactGroup.setIsDelete(jsonArray.getJSONObject(i).getBoolean("IsDelete"));
				contactGroups[i]=contactGroup;
				i++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonArray=null;
		return contactGroups;
	}
	
	public ContactGroupSub[] LoadContactGroupSub(String objectID,Context packageContext)
	{
		String localDataStr=null;
		JSONArray jsonArray=null;
		ContactGroupSub[] contactGroupSubs=null;
		ContactGroupSub contactGroupSub;
		int i=0;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactGroupSubs", packageContext);

			if(localDataStr==null)
			{
				return contactGroupSubs;
			}
			
			jsonArray=new JSONArray(localDataStr);
			
			contactGroupSubs=new ContactGroupSub[jsonArray.length()];
			
			while(i<jsonArray.length())
			{
				contactGroupSub=new ContactGroupSub();
				contactGroupSub.setContactGroupID(jsonArray.getJSONObject(i).getString("ContactGroupID"));
				contactGroupSub.setContactPersonObjectID(jsonArray.getJSONObject(i).getString("ContactPersonObjectID"));
				contactGroupSub.setIsDelete(jsonArray.getJSONObject(i).getBoolean("IsDelete"));
				contactGroupSubs[i]=contactGroupSub;
				i++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonArray=null;
		return contactGroupSubs;
	}

	@Override
	public void InitContactPersonInfo(String objectID, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr=null;
		JSONObject jsonObject=null;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPerson", packageContext);
			
			if(localDataStr==null)
			{
				jsonObject=new JSONObject();
				jsonObject.put("UpdateTime", CommonVariables.getMinDate());
				jsonObject.put("LatestTime", CommonVariables.getMinDate());
				CommonVariables.getLocalDataManager().SaveData(objectID, "ContactPerson",jsonObject.toString(), packageContext);
			}
			else
			{
				jsonObject=new JSONObject(localDataStr);
			}
			CommonVariables.setUpdateTime(jsonObject.getString("UpdateTime"));
			CommonVariables.setLatestTime(jsonObject.getString("LatestTime"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonObject=null;
	}

	@Override
	public String GetContactGroupname(String objectID, String groupID,
			Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr=null;
		JSONArray jsonArray=null;
		String groupName = null;
		int i=0;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactGroups", packageContext);

			if(localDataStr==null)
			{
				return null;
			}
			
			jsonArray=new JSONArray(localDataStr);

			while(i<jsonArray.length())
			{
				if(groupID.equalsIgnoreCase(jsonArray.getJSONObject(i).getString("GroupObjectID")))
				{
					groupName = jsonArray.getJSONObject(i).getString("GroupName");
					localDataStr=null;
					jsonArray=null;
					return groupName;
				}
				i++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonArray=null;
		return null;
	}

	@Override
	public void UpdateLatestTime(String objectID, String latestTime, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr=null;
		JSONObject jsonObject=null;
		try {
			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPerson", packageContext);
			
			if(localDataStr==null)
			{
				jsonObject=new JSONObject();				
			}
			else
			{
				jsonObject=new JSONObject(localDataStr);
			}
			
			jsonObject.put("LatestTime", latestTime);
			
			CommonVariables.getLocalDataManager().SaveData(objectID, "ContactPerson",jsonObject.toString(), packageContext);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonObject=null;
	}

	@Override
	public void CleanPersonInfo(String objectID, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr=null;
		JSONObject jsonObject=null;
		try {
//			localDataStr=CommonVariables.getLocalDataManager().GetData(objectID, "ContactPerson", packageContext);
//			
//			if(localDataStr!=null)
//			{
//				jsonObject=new JSONObject(localDataStr);
//				jsonObject.put("LatestTime", CommonVariables.getMinDate());
//				jsonObject.put("UpdateTime", CommonVariables.getMinDate());
//				CommonVariables.getLocalDataManager().SaveData(objectID, "ContactPerson",jsonObject.toString(), packageContext);
//			}
			CommonVariables.getLocalDataManager().DeleteData(objectID,"ContactPerson", packageContext);
			CommonVariables.getLocalDataManager().DeleteData(objectID,"ContactPersonLists", packageContext);
			CommonVariables.getLocalDataManager().DeleteData(objectID,"ContactGroups", packageContext);
			CommonVariables.getLocalDataManager().DeleteData(objectID,"ContactGroupSubs", packageContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDataStr=null;
		jsonObject=null;
	}
}
