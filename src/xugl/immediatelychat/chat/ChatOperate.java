package xugl.immediatelychat.chat;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import xugl.immediatelychat.common.CommonFlag;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.MsgRecord;

public class ChatOperate implements IChatOperate {

	@Override
	public ArrayList<ChatModel> GetChats(Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		ArrayList<ChatModel> chatModels=null;
		ChatModel chatModel = null;
		int chatType = 0;
		int i = 0;
		try
		{
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr!=null && localDataStr.length()>0)
			{
				chatModels = new ArrayList<ChatModel>();
				jsonArray=new JSONArray(localDataStr);
				for(;i<jsonArray.length();i++)
				{
					chatModel = new ChatModel();
					jsonObject = jsonArray.getJSONObject(i);
					chatType = jsonObject.getInt("ChatType");
					chatModel.setChatType(chatType);
					if(chatType == 1)
					{
						chatModel.setChatID(jsonObject.getString("ChatID"));
						chatModel.setDestinationObjectID(jsonObject.getString("DestinationObjectID"));
						chatModel.setContactPersonName(jsonObject.getString("ContactPersonName"));
					}
					else if (chatType == 2)
					{
						chatModel.setChatID(jsonObject.getString("ChatID"));
						chatModel.setGroupID(jsonObject.getString("GroupID"));
						chatModel.setGroupName(jsonObject.getString("GroupName"));
					}
					if(jsonObject.has("LatestMsg"))
					{
						chatModel.setLatestMsg(jsonObject.getString("LatestMsg"));
					}
					else
					{
						chatModel.setLatestMsg("");
					}
					if(jsonObject.has("LatestTime"))
					{
						chatModel.setLatestTime(jsonObject.getString("LatestTime"));
					}
					else
					{
						chatModel.setLatestTime("");
					}
					chatModel.setUnReadCount(jsonObject.getInt("UnReadCount"));
					chatModels.add(chatModel);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return OrderbyList(chatModels);
	}
	
	private ArrayList<ChatModel> OrderbyList(ArrayList<ChatModel> chatModels)
	{
		if(chatModels== null)
		{
			return null;
		}
		ChatModel chatModel = null;
		String latestTime = CommonVariables.getMinDate();
		for(int i=1;i<chatModels.size();i++)
		{
			chatModel = chatModels.get(i);
			int j = i - 1;
			if(chatModel.getLatestTime() != null)
			{
				latestTime = chatModel.getLatestTime();
			}
			while(j >= 0 && chatModels.get(j).getLatestTime().compareToIgnoreCase(latestTime)<0)
			{
				chatModels.set(j + 1, chatModels.get(j)) ;
				j -- ;
			}
			chatModels.set(j + 1, chatModel);
		}
		return chatModels;
	}

	@Override
	public ChatModel GetChat(JSONObject jsonObjecttemp,Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		ChatModel chatModel = null;
		int findindex = -1;
		int i = 0;
		int chatType = 0;
		String destinationObjectID = null;
		String destinationName = null;
		try
		{
			String groupid = jsonObjecttemp.getString(CommonFlag.getF_MsgRecipientGroupID());
			if(groupid != null && groupid.length()>0 && !groupid.equalsIgnoreCase("null"))
			{
				chatType=2;
				destinationObjectID = groupid;
				destinationName = CommonVariables.getContactDataOperate().GetContactGroupname(CommonVariables.getObjectID(), destinationObjectID, packageContext);
			}
			else
			{
				chatType=1;
				destinationObjectID = jsonObjecttemp.getString(CommonFlag.getF_MsgSenderObjectID());
				destinationName = jsonObjecttemp.getString(CommonFlag.getF_MsgSenderName());
			}
			
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr==null || localDataStr.length()<=0)
			{
				jsonArray=new JSONArray();
			}
			else
			{
				jsonArray=new JSONArray(localDataStr);
			}
			while(i<jsonArray.length())
			{
				if(jsonArray.getJSONObject(i).getInt("ChatType")==1)
				{
					if(jsonArray.getJSONObject(i).getString("DestinationObjectID").equalsIgnoreCase(destinationObjectID))
					{
						findindex=i;
						break;
					}
				}
				else if (jsonArray.getJSONObject(i).getInt("ChatType")==2)
				{
					if(jsonArray.getJSONObject(i).getString("GroupID").equalsIgnoreCase(destinationObjectID))
					{
						findindex=i;
						break;
					}
				}
				i++;
			}

			if(findindex == -1)
			{
				jsonObject=new JSONObject();
				jsonObject.put("ChatType", chatType);	
				if(chatType == 1)
				{
					jsonObject.put("ChatID", UUID.randomUUID().toString());
					jsonObject.put("DestinationObjectID", destinationObjectID);
					jsonObject.put("ContactPersonName", destinationName);
					jsonObject.put("LatestMsg", jsonObjecttemp.getString(CommonFlag.getF_MsgContent()));
				}
				else if (chatType == 2)
				{
					jsonObject.put("ChatID", UUID.randomUUID().toString());
					jsonObject.put("GroupID", destinationObjectID);
					jsonObject.put("GroupName", destinationName);
					jsonObject.put("LatestMsg", jsonObjecttemp.getString(CommonFlag.getF_MsgSenderName()) + ": " 
							+ jsonObjecttemp.getString(CommonFlag.getF_MsgContent()));
				}
				
				jsonObject.put("LatestTime", jsonObjecttemp.getString(CommonFlag.getF_SendTime()));
				jsonObject.put("UnReadCount", 1);
				jsonArray.put(jsonObject);
				CommonVariables.getLocalDataManager().SaveData(CommonVariables.getObjectID(),"ChatModels", jsonArray.toString(), packageContext);
			}
			else
			{
				jsonObject=jsonArray.getJSONObject(findindex);
				if(jsonObject.has("UnReadCount"))
				{
					jsonObject.put("UnReadCount", jsonObject.getInt("UnReadCount")+1);
				}
				else
				{
					jsonObject.put("UnReadCount", 1);
				}
				if(chatType==1)
				{
					jsonObject.put("LatestMsg", jsonObjecttemp.getString(CommonFlag.getF_MsgContent()));
				}
				else if (chatType == 2)
				{
					jsonObject.put("LatestMsg", jsonObjecttemp.getString(CommonFlag.getF_MsgSenderName()) + ": " 
							+ jsonObjecttemp.getString(CommonFlag.getF_MsgContent()));
				}
				
				jsonObject.put("LatestTime", jsonObjecttemp.getString(CommonFlag.getF_SendTime()));
				jsonArray.put(findindex,jsonObject);
				CommonVariables.getLocalDataManager().SaveData(CommonVariables.getObjectID(),"ChatModels", jsonArray.toString(), packageContext);
			}

			chatModel = new ChatModel();
			chatModel.setChatType(chatType);
			if(chatType == 1)
			{
				chatModel.setChatID(jsonObject.getString("ChatID"));
				chatModel.setDestinationObjectID(destinationObjectID);
				chatModel.setContactPersonName(destinationName);
				if(!jsonObject.isNull("LatestMsg"))
				{
					chatModel.setLatestMsg(jsonObject.getString("LatestMsg"));
				}
				else
				{
					chatModel.setLatestMsg("");
				}
			}
			else if (chatType == 2)
			{
				chatModel.setChatID(jsonObject.getString("ChatID"));
				chatModel.setGroupID(destinationObjectID);
				chatModel.setGroupName(destinationName);
				if(!jsonObject.isNull("LatestMsg"))
				{
					chatModel.setLatestMsg(jsonObject.getString("LatestMsg"));
				}
				else
				{
					chatModel.setLatestMsg("");
				}
			}
			
			
			
			if(!jsonObject.isNull("LatestTime"))
			{
				chatModel.setLatestTime(jsonObject.getString("LatestTime"));
			}
			else
			{
				chatModel.setLatestTime("");
			}
			chatModel.setUnReadCount(jsonObject.getInt("UnReadCount"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chatModel;
	}

	@Override
	public ChatModel GetChat(String destinationObjectID,
			String destinationName, int chatType, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		ChatModel chatModel = null;
		int findindex = -1;
		int i = 0;
		try
		{	
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr==null || localDataStr.length()<=0)
			{
				jsonArray=new JSONArray();
			}
			else
			{
				jsonArray=new JSONArray(localDataStr);
			}
			
			while(i<jsonArray.length())
			{
				if(jsonArray.getJSONObject(i).getInt("ChatType")==1)
				{
					if(jsonArray.getJSONObject(i).getString("DestinationObjectID").equalsIgnoreCase(destinationObjectID))
					{
						findindex=i;
						break;
					}
				}
				else if (jsonArray.getJSONObject(i).getInt("ChatType")==2)
				{
					if(jsonArray.getJSONObject(i).getString("GroupID").equalsIgnoreCase(destinationObjectID))
					{
						findindex=i;
						break;
					}
				}
				i++;
			}

			if(findindex == -1)
			{
				
				jsonObject=new JSONObject();
				jsonObject.put("ChatType", chatType);	
				if(chatType == 1)
				{
					jsonObject.put("ChatID", UUID.randomUUID().toString());
					jsonObject.put("DestinationObjectID", destinationObjectID);
					jsonObject.put("ContactPersonName", destinationName);
				}
				else if (chatType == 2)
				{
					jsonObject.put("ChatID", UUID.randomUUID().toString());
					jsonObject.put("GroupID", destinationObjectID);
					jsonObject.put("GroupName", destinationName);
				}
				jsonArray.put(jsonObject);
				CommonVariables.getLocalDataManager().SaveData(CommonVariables.getObjectID(),"ChatModels", jsonArray.toString(), packageContext);
			}
			else
			{
				jsonObject=jsonArray.getJSONObject(findindex);
			}

			chatModel = new ChatModel();
			chatModel.setChatType(chatType);
			if(chatType == 1)
			{
				chatModel.setChatID(jsonObject.getString("ChatID"));
				chatModel.setDestinationObjectID(destinationObjectID);
				chatModel.setContactPersonName(destinationName);
			}
			else if (chatType == 2)
			{
				chatModel.setChatID(jsonObject.getString("ChatID"));
				chatModel.setGroupID(destinationObjectID);
				chatModel.setGroupName(destinationName);
			}
			if(jsonObject.has("LatestMsg"))
			{
				chatModel.setLatestMsg(jsonObject.getString("LatestMsg"));
			}
			else
			{
				chatModel.setLatestMsg("");
			}
			
			if(jsonObject.has("LatestTime"))
			{
				chatModel.setLatestTime(jsonObject.getString("LatestTime"));
			}
			else
			{
				chatModel.setLatestTime("");
			}
			
			if(jsonObject.has("UnReadCount"))
			{
				chatModel.setUnReadCount(jsonObject.getInt("UnReadCount"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chatModel;
	}

	@Override
	public void CleanChats(Context packageContext) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		int i = 0;
		try
		{
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr!=null && localDataStr.length()>0)
			{
				jsonArray=new JSONArray(localDataStr);
				for(;i<jsonArray.length();i++)
				{
					jsonObject = jsonArray.getJSONObject(i);
					CommonVariables.getLocalDataManager().DeleteFile("MSG" + jsonObject.getString("ChatID"), packageContext); ;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CommonVariables.getLocalDataManager().DeleteData(CommonVariables.getObjectID(), "ChatModels", packageContext);
	}

	@Override
	public void UpdateUnReadCount(String chatID, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		int findindex = -1;
		int i = 0;
		try
		{	
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr==null || localDataStr.length()<=0)
			{
				return;
			}
			else
			{
				jsonArray=new JSONArray(localDataStr);
			}
			
			while(i<jsonArray.length())
			{

				if(jsonArray.getJSONObject(i).getString("ChatID").equalsIgnoreCase(chatID))
				{
					findindex=i;
					break;
				}
				i++;
			}

			if(findindex>=0)
			{
				jsonObject=jsonArray.getJSONObject(findindex);
				jsonObject.put("UnReadCount", 0);
				jsonArray.put(findindex, jsonObject);
				CommonVariables.getLocalDataManager().SaveData(CommonVariables.getObjectID(),"ChatModels", jsonArray.toString(), packageContext);
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void UpdateLatestMsg(MsgRecord msgRecord, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		int findindex = -1;
		int i = 0;
		try
		{	
			localDataStr = CommonVariables.getLocalDataManager().GetData(CommonVariables.getObjectID(),"ChatModels", packageContext);
			if(localDataStr==null || localDataStr.length()<=0)
			{
				return;
			}
			else
			{
				jsonArray=new JSONArray(localDataStr);
			}
			
			while(i<jsonArray.length())
			{

				if(jsonArray.getJSONObject(i).getString("ChatID").equalsIgnoreCase(msgRecord.getChatID()))
				{
					findindex=i;
					break;
				}
				i++;
			}

			if(findindex>=0)
			{
				jsonObject=jsonArray.getJSONObject(findindex);
				jsonObject.put("LatestMsg", msgRecord.getMsgContent());
				jsonObject.put("LatestTime", msgRecord.getSendTime());
				jsonArray.put(findindex, jsonObject);
				CommonVariables.getLocalDataManager().SaveData(CommonVariables.getObjectID(),"ChatModels", jsonArray.toString(), packageContext);
			}
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
