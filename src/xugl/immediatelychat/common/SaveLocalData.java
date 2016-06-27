package xugl.immediatelychat.common;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SaveLocalData implements ISaveLocalData {
	private String commonFileName="PSConfig";
	@Override
	public void SaveData(String fieldName, String fieldValue,Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(commonFileName, Activity.MODE_PRIVATE);  
		SharedPreferences.Editor editor = settings.edit();  
		editor.putString(fieldName,fieldValue); 
		editor.commit();
	}

	@Override
	public void SaveData(String filename, String fieldName, String fieldValue,Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		SharedPreferences.Editor editor = settings.edit();  
		editor.putString(fieldName,fieldValue); 
		editor.commit();
	}

	@Override
	public String GetData(String filename, String fieldName,Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		return settings.getString(fieldName, null);
	}

	@Override
	public String GetData(String fieldName,Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(commonFileName, Activity.MODE_PRIVATE);  
		return settings.getString(fieldName, null);
	}

	@Override
	public Map<String, ?> GetAllData(String filename, Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		return settings.getAll();
	}

	@Override
	public void DeleteData(String fieldName, Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(commonFileName, Activity.MODE_PRIVATE);  
		if(settings.contains(fieldName))
		{
			SharedPreferences.Editor editor = settings.edit();  
			editor.remove(fieldName);
			editor.commit();
		}
	}

	@Override
	public void DeleteData(String filename, String fieldName,
			Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		if(settings.contains(fieldName))
		{
			SharedPreferences.Editor editor = settings.edit();  
			editor.remove(fieldName);
			editor.commit();
		}
	}

	@Override
	public String FindData(String filename, String fieldName,
			Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		return settings.getString(fieldName, null);
	}

	@Override
	public String FindData(String fieldName, Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(commonFileName, Activity.MODE_PRIVATE);  
		return settings.getString(fieldName, null);
	}

	@Override
	public void DeleteFile(String filename, Context packageContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = packageContext.getSharedPreferences(filename, Activity.MODE_PRIVATE);  
		SharedPreferences.Editor editor = settings.edit();  
		editor.clear();
		editor.commit();
	}
}
