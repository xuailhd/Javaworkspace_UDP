package xugl.immediatelychat.chat;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import xugl.immediatelychat.models.MsgRecord;

public interface IMsgRecordOperate {
	ArrayList<MsgRecord> GetMsgRecord(String chatID,Context packageContext);
	
	void SaveMsgRecord(MsgRecord msgRecord,Context packageContext);
	
	MsgRecord SaveMsgRecord(JSONObject msgRecord,String chatID,Context packageContext);
	
//	String SaveMsgRecord(JSONObject msgRecord,Context packageContext);
	
	void UpdateIsSend(String msgID, String chatID,Context packageContext);
}
