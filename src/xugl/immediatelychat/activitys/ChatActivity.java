package xugl.immediatelychat.activitys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.MsgRecord;

public class ChatActivity extends Activity {
	
	private Button button;
	private EditText editText;
	private LinearLayout chatLayout;
	private ScrollView scrollView;
	private Handler mHandler = new Handler();
	private ReceiveBroadCast receiveBroadCast;
	private ChatModel chatModel;
	private HashMap<String,Integer> unSendMap = new HashMap<String,Integer>();
	private int unSendWaterCount = 10000; 

	protected void setView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_chat);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	
	private class ReceiveBroadCast extends BroadcastReceiver
	{
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	
        	if(intent.hasExtra("FinishSend"))
			{
        		final String msgID = intent.getStringExtra("FinishSend");
        		
        		if(unSendMap.containsKey(msgID))
        		{
        			final int ingid = unSendMap.get(msgID);
        			mHandler.post(new Runnable() {  
         				public void run() {  
         					ImageView ing = (ImageView)findViewById(ingid);
         					if(ing!=null)
         					{
         						ing.setVisibility(View.GONE);
         					}
         				}  
         			}); 
        		}
        		
			}
        	else if(intent.hasExtra("MsgRecord"))
        	{
        		 final MsgRecord msgRecord = intent.getParcelableExtra("MsgRecord");
                 if(msgRecord.getMsgSenderObjectID().equals(CommonVariables.getObjectID()))
                 {
                 	return;
                 }
                CommonVariables.getChatOperate().UpdateUnReadCount(chatModel.getChatID(), ChatActivity.this);
                 
                mHandler.post(new Runnable() {  
     				public void run() {  
     					AddChatIntoView(msgRecord);
     					//mHandler.post(this);
     					scrollView.post(new Runnable() {  
     		            	public void run() {  
     		            		scrollView.fullScroll(ScrollView.FOCUS_DOWN); 
     						}  
     		            });
     				}  
     			}); 
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


	protected void init() {
		// TODO Auto-generated method stub
		chatLayout= (LinearLayout)findViewById(R.id.mainchatLayout);
		button=(Button)findViewById(R.id.mainbuttonSend);
		editText=(EditText)findViewById(R.id.mainchatinput);
		scrollView=(ScrollView)findViewById(R.id.mainchatScrollView);
		
		Intent intent=getIntent();
		
		chatModel= intent.getParcelableExtra("ChatModel");
		
		ArrayList<MsgRecord> msgRecords= CommonVariables.getMsgRecordOperate().GetMsgRecord(chatModel.getChatID(),ChatActivity.this);

		for(int i=0;i<msgRecords.size();i++)
		{
			AddChatIntoView(msgRecords.get(i));
		}

		CommonVariables.getChatOperate().UpdateUnReadCount(chatModel.getChatID(), ChatActivity.this);
		
    	receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
    	filter.addAction(chatModel.getChatID());
    	registerReceiver(receiveBroadCast, filter);
		
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text=editText.getText().toString();
				if(!text.isEmpty())
				{
					MsgRecord msgRecord = new MsgRecord();
					msgRecord.setMsgContent(text);
					msgRecord.setMsgID(UUID.randomUUID().toString());
					
					if(chatModel.getChatType()==1)
					{
						msgRecord.setMsgRecipientObjectID(chatModel.getDestinationObjectID());
					}
					else if(chatModel.getChatType()==2)
					{
						msgRecord.setMsgRecipientGroupID(chatModel.getGroupID());
					}
					
					msgRecord.setMsgSenderName(CommonVariables.getAccount());
					msgRecord.setMsgSenderObjectID(CommonVariables.getObjectID());
					msgRecord.setMsgType(1);
					msgRecord.setSendTime(CommonVariables.getSimpleDateFormat().format(new Date()));
					msgRecord.setIsSend(0);
					msgRecord.setChatID(chatModel.getChatID());
					AddChatIntoView(msgRecord); 					
					CommonVariables.getSendMsg().doSend(msgRecord,ChatActivity.this);
					
					editText.setText(null);
					editText.clearFocus();
				    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
				    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
					scrollView.setFocusable(true);
					scrollView.setFocusableInTouchMode(true);
					scrollView.requestFocus();
					scrollView.requestFocusFromTouch();
					scrollView.post(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							scrollView.fullScroll(ScrollView.FOCUS_DOWN); 
						}});
				}
			}});
		scrollView.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrollView.fullScroll(ScrollView.FOCUS_DOWN); 
			}});
		editText.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
	    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
		scrollView.setFocusable(true);
		scrollView.setFocusableInTouchMode(true);
		scrollView.requestFocus();
		scrollView.requestFocusFromTouch();
	} 
	
	
	private void AddChatIntoView(MsgRecord msgRecord)
	{
		ImageView pic=new ImageView(ChatActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		
		TextView name=new TextView(ChatActivity.this);
		name.setText(msgRecord.getMsgSenderName());
		name.setGravity(Gravity.CENTER);
		
		LinearLayout linearLayout=new LinearLayout(ChatActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(pic);
		linearLayout.addView(name);
		
		
		TextView textView=new TextView(ChatActivity.this);
		textView.setText(msgRecord.getMsgContent());
		
		
		LinearLayout linearLayout2=new LinearLayout(ChatActivity.this);
		if(!msgRecord.getMsgSenderObjectID().equals(CommonVariables.getObjectID()))
		{			
			linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout2.addView(linearLayout);
			linearLayout2.addView(textView);
		}
		else
		{
			if(msgRecord.getIsSend()<1)
			{
				ImageView ing=new ImageView(ChatActivity.this);
				ing.setId(unSendWaterCount++);
				ing.setImageResource(R.drawable.ing);
				linearLayout2.addView(ing);
				unSendMap.put(msgRecord.getMsgID(), ing.getId());
			}
			linearLayout2.addView(textView);
			linearLayout2.addView(linearLayout);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
			lp.gravity = Gravity.END;  
			linearLayout2.setLayoutParams(lp);
		}
		chatLayout.addView(linearLayout2);
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
