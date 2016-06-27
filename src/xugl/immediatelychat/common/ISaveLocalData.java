package xugl.immediatelychat.common;

import java.util.Map;

import android.content.Context;

public interface ISaveLocalData {
	void SaveData(String fieldName,String fieldValue,Context packageContext);
	
	void SaveData(String filename, String fieldName,String fieldValue,Context packageContext);
	
	String GetData(String filename,String fieldName,Context packageContext);
	
	String GetData(String fieldName,Context packageContext);
	
	String FindData(String filename,String fieldName,Context packageContext);
	
	String FindData(String fieldName,Context packageContext);
	
	Map<String, ?> GetAllData(String fieldName,Context packageContext);
	
	void DeleteData(String fieldName,Context packageContext);
	
	void DeleteData(String filename, String fieldName,Context packageContext);
	
	void DeleteFile(String filename,Context packageContext);
}
