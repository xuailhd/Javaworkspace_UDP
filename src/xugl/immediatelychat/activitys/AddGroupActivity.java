package xugl.immediatelychat.activitys;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ContactGroup;
import xugl.immediatelychat.models.ContactPerson;

public class AddGroupActivity extends Activity {
	private Button search;
	private EditText searchinput;
	private LinearLayout searchlayout;
	private Handler mHandler=new Handler();
	private SearchBroadCast searchBroadCast;
	
	private class SearchBroadCast extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			JSONArray jsonArray =null;
        	ContactGroup contactGroup=null;
        	ContactGroup[] contactGroups=null;

        	String broadCastType=intent.getStringExtra("BroadCastType");
        	
        	if(broadCastType.equals("Search"))
        	{
	            String message = intent.getStringExtra("SearchResult");
	
	            try {
	            	if(!message.equals("No Result") && !message.equals("No Connect"))
	                {
	            		jsonArray=new JSONArray(message);
	            		
	            		if(jsonArray!=null && jsonArray.length()>0)
	            		{
	            			contactGroups=new ContactGroup[jsonArray.length()];
	            			for(int i=0;i<jsonArray.length();i++)
	                		{
	            				contactGroup = new ContactGroup();
	            				
	            				contactGroup.setGroupName(jsonArray.getJSONObject(i).getString("GroupName"));
	            				contactGroup.setGroupObjectID(jsonArray.getJSONObject(i).getString("GroupObjectID"));
	            				contactGroup.setIsDelete(jsonArray.getJSONObject(i).getBoolean("IsDelete"));

	            				contactGroups[i] = contactGroup;
	                		}
	            		}
	                }
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            final ContactGroup[] contactGroupsnew=contactGroups;
	
	        	mHandler.post(new Runnable() {  
					public void run() {  
			            if(contactGroupsnew!=null && contactGroupsnew.length>0)
			            {
	    					for(int i=0;i<contactGroupsnew.length;i++)
	    	            	{
	    						addGroupIntoView(contactGroupsnew[i]);
	    	            	}
			            }
			            search.setEnabled(true);
					}	
				}); 
        	}
        	else if (broadCastType.equals("Add"))
        	{
        		 
        		int status=intent.getIntExtra("Status", -1);
        		if(status==0)
        		{
        			finish();
        		}
        	}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView();
		init();
	}
	
	protected void setView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_findcontact);
	}

	protected void init() {
		// TODO Auto-generated method stub
		search=(Button)findViewById(R.id.search);
		searchinput=(EditText)findViewById(R.id.searchinput);
		searchlayout=(LinearLayout)findViewById(R.id.searchlayout);
		
		search.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String key=searchinput.getText().toString();
					CommonVariables.getSendMsg().sendSearchRequest(key, 2, AddGroupActivity.this);
					CleanViews();
					search.setEnabled(false);
				}
			}
		);
		
		// ×¢²á¹ã²¥½ÓÊÕ
		searchBroadCast = new SearchBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SearchGroup");    
        registerReceiver(searchBroadCast, filter);
	}
	
	private void CleanViews()
	{
		searchlayout.removeAllViews();
	}
	
	private void addGroupIntoView(final ContactGroup contactGroup)
	{
		ImageView pic=new ImageView(AddGroupActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		
		TextView name=new TextView(AddGroupActivity.this);
		name.setText(contactGroup.getGroupName());
		
		LinearLayout linearLayout=new LinearLayout(AddGroupActivity.this);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.addView(pic);
		linearLayout.addView(name);
		
		linearLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				CommonVariables.getSendMsg().sendAddGroupRequest(contactGroup.getGroupObjectID(), AddGroupActivity.this);
			}
		});
		
		searchlayout.addView(linearLayout);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(searchBroadCast!=null)
		{
			unregisterReceiver(searchBroadCast);
		}
		super.onDestroy();
	}
	
}
