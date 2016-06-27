package xugl.immediatelychat.common;

import xugl.immediatelychat.models.MsgRecord;
import android.content.Context;

public interface ISendMsg {
	void doSend(MsgRecord Msgstr, Context packageContext);

	void sendSearchRequest(String key,int type,Context packageContext);
	
	void sendAddPersonRequest(String objectID,Context packageContext);
	
	void sendAddGroupRequest(String objectID,Context packageContext);
}
