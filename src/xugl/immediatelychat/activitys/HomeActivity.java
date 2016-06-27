package xugl.immediatelychat.activitys;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;

public class HomeActivity extends BaseActivity {
	private LinearLayout mainLayout;
	//private ScrollView scrollView;
	private ReceiveBroadCast receiveBroadCast;
	private Handler mHandler = new Handler();
	
	private class ReceiveBroadCast extends BroadcastReceiver
	{
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //得到广播中得到的数据，并显示出来
            final String msg = intent.getStringExtra("Msg");
            if(msg.equals("NewMsg"))
            {
            	mHandler.post(new Runnable() {  
    				public void run() {  
    					ArrayList<ChatModel> chatModels=CommonVariables.getChatOperate().GetChats(HomeActivity.this);
    					if(chatModels==null || chatModels.size()<1)
    					{
    						return;
    					}
    					CleanViews();
    					for(int i=0;i<chatModels.size();i++)
    					{
    						addChatIntoView(chatModels.get(i));
    					}
    					//mHandler.post(this);
    				}  
    			}); 
            }
        }
	}
	
	@Override
	protected void setView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_home);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
    	filter.addAction(CommonVariables.getObjectID() + "Home");
    	registerReceiver(receiveBroadCast, filter);

		mainLayout=(LinearLayout)findViewById(R.id.charsLayout);
		//scrollView=(ScrollView)findViewById(R.id.chartsScrollView);
	}
	
	private void CleanViews()
	{
		mainLayout.removeAllViews();
	}
	
	private void addChatIntoView(final ChatModel chatModel)
	{
		ImageView pic=new ImageView(HomeActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		TextView name=new TextView(HomeActivity.this);
		if(chatModel.getChatType()==1)
		{
			name.setText(chatModel.getContactPersonName());
		}
		else if(chatModel.getChatType()==2)
		{
			name.setText(chatModel.getGroupName());
		}
		
		TextView content=new TextView(HomeActivity.this);
		LinearLayout.LayoutParams contentpara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		content.setText(chatModel.getLatestMsg());
		content.setLayoutParams(contentpara);
		
		LinearLayout line=new LinearLayout(HomeActivity.this);
		LinearLayout.LayoutParams linepara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,4);
		linepara.setMargins(5, 0, 0, 0);
		line.setLayoutParams(linepara);
		line.setOrientation(LinearLayout.VERTICAL);
		line.addView(name);
		line.addView(content);

		TextView contentTime=new TextView(HomeActivity.this);
		if(!chatModel.getLatestTime().isEmpty()&&chatModel.getLatestTime().length()>0)
		{
			contentTime.setText(String.copyValueOf(chatModel.getLatestTime().toCharArray(), 0, CommonVariables.getDateFormat2().length()));
		}
		TextView unreadcount=new TextView(HomeActivity.this);
		if(chatModel.getUnReadCount()>0)
		{
			unreadcount.setText( "(" + String.valueOf(chatModel.getUnReadCount()) + ")" );
		}
		
		LinearLayout timeline=new LinearLayout(HomeActivity.this);
		LinearLayout.LayoutParams timelinepara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		timelinepara.setMargins(5, 0, 0, 0);
		timeline.setLayoutParams(timelinepara);
		timeline.setOrientation(LinearLayout.VERTICAL);
		timeline.addView(contentTime);
		timeline.addView(unreadcount);

		
		LinearLayout wholeline=new LinearLayout(HomeActivity.this);
		LinearLayout.LayoutParams wholelinepara = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		wholelinepara.setMargins(5, 5, 5, 0);
		wholeline.setLayoutParams(wholelinepara);
		wholeline.addView(pic);
		wholeline.addView(line);
		wholeline.addView(timeline);
		
		wholeline.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.putExtra("ChatModel", chatModel);
				intent.setClass(HomeActivity.this, ChatActivity.class);
				startActivity(intent);
			}
			
		});

		mainLayout.addView(wholeline);
	}
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ArrayList<ChatModel> chatModels=CommonVariables.getChatOperate().GetChats(HomeActivity.this);
		if(chatModels==null || chatModels.size()<1)
		{
			return;
		}
		CleanViews();
		for(int i=0;i<chatModels.size();i++)
		{
			addChatIntoView(chatModels.get(i));
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(receiveBroadCast!=null)
		{
			unregisterReceiver(receiveBroadCast);
		}
	}

}
