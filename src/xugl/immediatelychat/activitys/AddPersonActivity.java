package xugl.immediatelychat.activitys;

import org.json.JSONArray;
import org.json.JSONException;

import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ContactPerson;
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

public class AddPersonActivity extends Activity  {
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
        	ContactPerson contactPerson=null;
        	ContactPerson[] contactPersons=null;

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
	            			contactPersons=new ContactPerson[jsonArray.length()];
	            			for(int i=0;i<jsonArray.length();i++)
	                		{
	            				contactPerson=new ContactPerson();
	            				contactPerson.setContactName(jsonArray.getJSONObject(i).getString("ContactName"));
	            				contactPerson.setObjectID(jsonArray.getJSONObject(i).getString("ObjectID"));
	            				contactPersons[i]=contactPerson;
	                		}
	            		}
	                }
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            final ContactPerson[] contactPersonsnew=contactPersons;
	
	        	mHandler.post(new Runnable() {  
					public void run() {  
			            if(contactPersonsnew!=null && contactPersonsnew.length>0)
			            {
	    					for(int i=0;i<contactPersonsnew.length;i++)
	    	            	{
	    						addPersonIntoView(contactPersonsnew[i]);
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

	
	private void CleanViews()
	{
		searchlayout.removeAllViews();
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
					CommonVariables.getSendMsg().sendSearchRequest(key, 1, AddPersonActivity.this);
					CleanViews();
					search.setEnabled(false);
				}
			}
		);
		
		// ×¢²á¹ã²¥½ÓÊÕ
		searchBroadCast = new SearchBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SearchPerson");    
        registerReceiver(searchBroadCast, filter);
	}
	
	private void addPersonIntoView(final ContactPerson contactPerson)
	{
		ImageView pic=new ImageView(AddPersonActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		
		TextView name=new TextView(AddPersonActivity.this);
		name.setText(contactPerson.getContactName());
		
		LinearLayout linearLayout=new LinearLayout(AddPersonActivity.this);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.addView(pic);
		linearLayout.addView(name);
		
		linearLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				CommonVariables.getSendMsg().sendAddPersonRequest(contactPerson.getObjectID(), AddPersonActivity.this);
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
