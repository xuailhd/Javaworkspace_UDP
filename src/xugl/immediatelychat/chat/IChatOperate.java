package xugl.immediatelychat.chat;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.MsgRecord;

public interface IChatOperate {
	ArrayList<ChatModel> GetChats(Context packageContext);
	
	ChatModel GetChat(JSONObject jsonObject,Context packageContext);
	
	ChatModel GetChat(String destinationObjectID,String destinationName,int chatType, Context packageContext);
	
	void UpdateUnReadCount(String chatID, Context packageContext);
	
	void UpdateLatestMsg(MsgRecord msgRecord, Context packageContext);
	
	void CleanChats(Context packageContext);
}
