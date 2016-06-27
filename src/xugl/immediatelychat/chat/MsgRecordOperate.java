package xugl.immediatelychat.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import xugl.immediatelychat.common.CommonFlag;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.MsgRecord;

public class MsgRecordOperate implements IMsgRecordOperate {

	@Override
	public ArrayList<MsgRecord> GetMsgRecord(String chatID,Context packageContext) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = null;
		ArrayList<MsgRecord> msgRecords= null;
		MsgRecord msgRecord = null;
		try {
			Map<String, ?> map=CommonVariables.getLocalDataManager().GetAllData("MSG" + chatID,packageContext);
			
			if(map!=null)
			{				
				msgRecords = new ArrayList<MsgRecord>();
				Iterator iter = map.entrySet().iterator();  
			    while (iter.hasNext()) {  
			        Map.Entry entry = (Map.Entry) iter.next();   
			        String val = (String) entry.getValue();
			        jsonObject = new JSONObject(val);
			        msgRecord = new MsgRecord();
			        msgRecord.setMsgContent(jsonObject.getString("MsgContent"));
			        if(jsonObject.has("MsgID"))
			        {
			        	msgRecord.setMsgID(jsonObject.getString("MsgID"));
			        }
			        else
			        {
			        	msgRecord.setMsgID(UUID.randomUUID().toString());
			        }
			        
			        if(jsonObject.has("MsgRecipientGroupID"))
			        {
			        	msgRecord.setMsgRecipientGroupID(jsonObject.getString("MsgRecipientGroupID"));
			        }
			        if(jsonObject.has("MsgRecipientObjectID"))
			        {
			        	msgRecord.setMsgRecipientObjectID(jsonObject.getString("MsgRecipientObjectID"));
			        }
			        msgRecord.setMsgSenderName(jsonObject.getString("MsgSenderName"));
			        msgRecord.setMsgSenderObjectID(jsonObject.getString("MsgSenderObjectID"));
			        msgRecord.setMsgType(jsonObject.getInt("MsgType"));
			        msgRecord.setSendTime(jsonObject.getString("SendTime"));
			        
			        if(jsonObject.has("ChatID"))
			        {
			        	msgRecord.setChatID(jsonObject.getString("ChatID"));
			        }
			        
			        if(jsonObject.has("IsSend"))
			        {
			        	msgRecord.setIsSend(jsonObject.getInt("IsSend"));
			        }
			        else
			        {
			        	msgRecord.setIsSend(1);
			        }
			        
		        	
			        msgRecords.add(msgRecord);
			    }
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return OrderbyList(msgRecords);
	}
	
	private ArrayList<MsgRecord> OrderbyList(ArrayList<MsgRecord> msgRecords)
	{
		if(msgRecords== null)
		{
			return null;
		}
		MsgRecord msgRecord = null;
		for(int i=1;i<msgRecords.size();i++)
		{
			msgRecord = msgRecords.get(i);
			int j = i - 1;
			while(j >= 0 && msgRecords.get(j).getSendTime().compareToIgnoreCase(msgRecord.getSendTime())>0)
			{
				msgRecords.set(j + 1, msgRecords.get(j)) ;
				j -- ;
			}
			msgRecords.set(j + 1, msgRecord);
		}
		return msgRecords;
	}


	@Override
	public void SaveMsgRecord(MsgRecord msgRecord, Context packageContext) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = null;
		
		try {
			if(msgRecord.getMsgID() == null)
			{
				return;
			}
			if(CommonVariables.getLocalDataManager().FindData("MSG" + msgRecord.getChatID(), msgRecord.getMsgID(), packageContext)==null)
			{
				jsonObject =new JSONObject();
				jsonObject.put(CommonFlag.getF_MsgID(), msgRecord.getMsgID());
				jsonObject.put(CommonFlag.getF_MsgSenderObjectID(),msgRecord.getMsgSenderObjectID());
				jsonObject.put(CommonFlag.getF_MsgSenderName(), msgRecord.getMsgSenderName());
				jsonObject.put(CommonFlag.getF_MsgContent(), msgRecord.getMsgContent());
				jsonObject.put(CommonFlag.getF_MsgRecipientObjectID(),msgRecord.getMsgRecipientObjectID());
				jsonObject.put(CommonFlag.getF_MsgRecipientGroupID(),msgRecord.getMsgRecipientGroupID());
				jsonObject.put(CommonFlag.getF_MsgType(), msgRecord.getMsgType());
				jsonObject.put(CommonFlag.getF_SendTime(), msgRecord.getSendTime());
				jsonObject.put("IsSend", msgRecord.getIsSend());
				jsonObject.put("ChatID", msgRecord.getChatID());
				
				CommonVariables.getLocalDataManager().SaveData("MSG" + msgRecord.getChatID(),msgRecord.getMsgID(),jsonObject.toString(), packageContext);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public MsgRecord SaveMsgRecord(JSONObject jsonObject,String chatID,Context packageContext) {
		// TODO Auto-generated method stub
		MsgRecord msgRecord=null;
		try {
			
			if(CommonVariables.getLocalDataManager().FindData("MSG" + chatID, jsonObject.getString(CommonFlag.getF_MsgID()), packageContext)==null)
			{
				msgRecord=new MsgRecord();
				msgRecord.setMsgID(jsonObject.getString(CommonFlag.getF_MsgID()));
				msgRecord.setMsgSenderObjectID(jsonObject.getString(CommonFlag.getF_MsgSenderObjectID()));
				msgRecord.setMsgSenderName(jsonObject.getString(CommonFlag.getF_MsgSenderName()));
				msgRecord.setMsgContent(jsonObject.getString(CommonFlag.getF_MsgContent()));
				msgRecord.setMsgRecipientObjectID(jsonObject.getString(CommonFlag.getF_MsgRecipientObjectID()));
				msgRecord.setMsgRecipientGroupID(jsonObject.getString(CommonFlag.getF_MsgRecipientGroupID()));
				msgRecord.setMsgType(jsonObject.getInt(CommonFlag.getF_MsgType()));
				msgRecord.setSendTime(jsonObject.getString(CommonFlag.getF_SendTime()));
				msgRecord.setIsSend(1);
				msgRecord.setChatID(chatID);
				
				jsonObject.put("IsSend", 1);
				jsonObject.put("ChatID", chatID);
				
				CommonVariables.getLocalDataManager().SaveData("MSG" + chatID,msgRecord.getMsgID(),jsonObject.toString(), packageContext);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgRecord;
	}



	@Override
	public void UpdateIsSend(String msgID, String chatID, Context packageContext) {
		// TODO Auto-generated method stub
		String localDataStr = null;
		JSONObject jsonObject = null;
		try {
			localDataStr = CommonVariables.getLocalDataManager().GetData("MSG" + chatID,msgID, packageContext);
			
			if(localDataStr!=null)
			{
				jsonObject = new JSONObject(localDataStr);
				
				jsonObject.put("IsSend", 1);
				
				CommonVariables.getLocalDataManager().SaveData("MSG" + chatID,msgID,jsonObject.toString(), packageContext);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
