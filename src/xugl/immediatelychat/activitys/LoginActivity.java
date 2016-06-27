package xugl.immediatelychat.activitys;

import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.common.ConnectServer;
import xugl.immediatelychat.common.ConnectServerUDP;
import xugl.immediatelychat.common.IConnectServer;
import xugl.immediatelychat.services.ReciveMsgService;
import android.content.BroadcastReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;

public class LoginActivity extends Activity {
	private Button login;
	private Button cancel;
	private EditText serverIP;
//	private EditText serverPort;
	private EditText account;
	private EditText password;
	private TextView errorMsg;
	private Handler mHandler; 
	private ReceiveBroadCast receiveBroadCast;
	
	private class ReceiveBroadCast extends BroadcastReceiver
	{
        @Override
        public void onReceive(Context context, Intent intent)
        {
        	//得到广播中得到的数据，并显示出来
            final String message = intent.getStringExtra("MSG");
            if(message.equals("Success"))
            {	
//            	CommonVariables.getChatOperate().CleanChats(LoginActivity.this);
            	startService(new Intent().setClass(LoginActivity.this, ReciveMsgService.class));
            	
            	Intent intent2 = new Intent();
            	intent2.setClass(LoginActivity.this, HomeActivity.class);
	            startActivity(intent2);
	            finish();
            }
            else
            {
            	mHandler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						errorMsg.setText(message);
					}
            		
            	});
            }
        }
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiveBroadCast);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_activity);
		
		mHandler = new Handler();
		
		// 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("LoginActivity");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
		
		login=(Button)findViewById(R.id.login);
		cancel=(Button)findViewById(R.id.cancel);
		serverIP=(EditText)findViewById(R.id.serverip);
//		serverPort=(EditText)findViewById(R.id.port);
		account=(EditText)findViewById(R.id.account);
		password=(EditText)findViewById(R.id.password);
		errorMsg=(TextView)findViewById(R.id.errorMsg);
		
		//读取本地配置文件
		SharedPreferences settings = getSharedPreferences("PSConfig", Activity.MODE_PRIVATE);  
		serverIP.setText(settings.getString("PSIP", ""));
//		serverPort.setText(settings.getString("PSPort", ""));
		account.setText(settings.getString("Account", ""));
		password.setText(settings.getString("Password", ""));
		
		login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				IConnectServer connectServer=new ConnectServer();
				
				//存储本地配置文件
				SharedPreferences settings = getSharedPreferences("PSConfig", Activity.MODE_PRIVATE);  
				SharedPreferences.Editor editor = settings.edit();  
				editor.putString("PSIP", serverIP.getText().toString()); 
//				editor.putString("PSPort", serverPort.getText().toString()); 
				editor.putString("PSPort", "9000"); 
				editor.putString("Account", account.getText().toString()); 
				editor.putString("Password", password.getText().toString()); 
				editor.commit();  
				
				CommonVariables.setPSIP(serverIP.getText().toString());
//				CommonVariables.setPSPort(Integer.parseInt(serverPort.getText().toString()) );
				CommonVariables.setPSPort(9000);
				connectServer.postAccount(account.getText().toString(),password.getText().toString(),LoginActivity.this);
				
				errorMsg.setText(R.string.conneting);
				
			}
			
		});
		
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
	}
	

}
