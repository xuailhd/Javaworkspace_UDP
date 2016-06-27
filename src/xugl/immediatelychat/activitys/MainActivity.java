package xugl.immediatelychat.activitys;


import org.json.JSONException;
import org.json.JSONObject;

import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonFlag;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.common.ISendMsg;
import xugl.immediatelychat.common.SendMsg;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button button;
	private EditText editText;
	private LinearLayout chatLayout;
	private ScrollView scrollView;
	
//	private ReciveMsgService reciveMsgService;
	private ISendMsg sendMsg;
	private ReceiveBroadCast receiveBroadCast;  //广播实例
	
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	
	private class ReceiveBroadCast extends BroadcastReceiver
	{
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	String msgcontent=null;
        	String account=null;
        	String objectID=null;
        	JSONObject msgjson=null;
        	
            //得到广播中得到的数据，并显示出来
            final String message = intent.getStringExtra("MSG");
            
            try {
				msgjson=new JSONObject(message);
//				msgcontent=msgjson.getString(CommonFlag.getF_Content());
				account=msgjson.getString(CommonFlag.getF_Account());
				objectID=msgjson.getString(CommonFlag.getF_ObjectID());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            if(objectID.equals(CommonVariables.getObjectID()))
            {
            	return;
            }
            
            final String msgcontent2=msgcontent;
            final String account2=account;

            mHandler.post(new Runnable() {  
				public void run() {  
					
					ImageView pic=new ImageView(MainActivity.this);
					pic.setImageResource(R.drawable.ic_launcher);
					
					TextView name=new TextView(MainActivity.this);
					name.setText(account2);
					
					LinearLayout linearLayout=new LinearLayout(MainActivity.this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					linearLayout.addView(pic);
					linearLayout.addView(name);
					
					
					TextView textView=new TextView(MainActivity.this);
					textView.setText(msgcontent2);

					LinearLayout linearLayout2=new LinearLayout(MainActivity.this);
					linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
					
					linearLayout2.addView(linearLayout);
					linearLayout2.addView(textView);
					

					chatLayout.addView(linearLayout2);
					
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);  
					//mHandler.post(this);
				}  
			}); 
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		chatLayout= (LinearLayout)findViewById(R.id.chatLayout);
		button=(Button)findViewById(R.id.button1);
		editText=(EditText)findViewById(R.id.chatinput);
		scrollView=(ScrollView)findViewById(R.id.scrollView1);
		
		try
		{
		//bing Service
		//Intent intent =new Intent("xugl.services.ReciveMsgService").setPackage(this.getPackageName());
		//bindService(intent,conn,Context.BIND_AUTO_CREATE);
		
		sendMsg=new SendMsg();
		
		// 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
//        filter.addAction(CommonVariables.getGroupID());    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
		
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text=editText.getText().toString();
				if(!text.isEmpty())
				{
					ImageView pic=new ImageView(MainActivity.this);
					pic.setImageResource(R.drawable.ic_launcher);
					
					TextView name=new TextView(MainActivity.this);
					name.setText(CommonVariables.getAccount());
					
					LinearLayout linearLayout=new LinearLayout(MainActivity.this);
					linearLayout.setOrientation(LinearLayout.VERTICAL);
					linearLayout.addView(pic);
					linearLayout.addView(name);
					
					
					TextView textView=new TextView(MainActivity.this);
					textView.setText(text);

					LinearLayout linearLayout2=new LinearLayout(MainActivity.this);
					linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
					linearLayout2.addView(textView);
					linearLayout2.addView(linearLayout);
					
					
					//此处相当于布局文件中的Android:layout_gravity属性  
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
					lp.gravity = Gravity.END;  
					linearLayout2.setLayoutParams(lp);

					chatLayout.addView(linearLayout2);
					
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);  
					//sendMsg.doSend(text);
				}
			}});
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	//connection of bind service
//	ServiceConnection conn=new ServiceConnection(){
//
//		@Override
//		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
//			// TODO Auto-generated method stub
//			reciveMsgService= ((ReciveMsgService.LocalBinder)arg1).getService();
//			Log.e("Test", "onServiceConnected");
//			reciveMsgService.setIUpdateChatContent(new IUpdateChatContent(){
//
//				@Override
//				public void update(final String Msg) {
//					// TODO Auto-generated method stub
//					Log.e("Test", Msg);
//					if(!Msg.isEmpty())
//					{
//						mHandler.post(new Runnable() {  
//							public void run() {  
//								ImageView imageView=new ImageView(MainActivity.this);
//								imageView.setImageResource(R.drawable.ic_launcher);
//								imageView.setLayoutParams(new TableRow.LayoutParams(1));
//								
//								
//								TextView textView=new TextView(MainActivity.this);
//								textView.setText(Msg);
//								textView.setLayoutParams(new TableRow.LayoutParams(2));
//								
//								TableRow tableRow2=new TableRow(MainActivity.this);
//								tableRow2.addView(imageView);
//								tableRow2.addView(textView);
//								tableLayout.addView(tableRow2);
//								scrollView.fullScroll(ScrollView.FOCUS_DOWN);  
//								//mHandler.post(this);
//							}  
//						}); 
//						
//					}
//					
//				}});
//			reciveMsgService.GetMSG();
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	};
	
	public void scrollToBottom(final ScrollView scroll) {  
		  
		mHandler.post(new Runnable() {  
			public void run() {  
				scroll.fullScroll(ScrollView.FOCUS_DOWN);  
			}  
		});  
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//unbindService(conn);
		unregisterReceiver(receiveBroadCast);
		super.onDestroy();
	
	} 
	
	
}
