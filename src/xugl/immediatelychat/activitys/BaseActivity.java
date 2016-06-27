package xugl.immediatelychat.activitys;

import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.services.ReciveMsgService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public abstract class BaseActivity extends Activity {
	private TextView charsView;
	private TextView personsView;
	private TextView groupsView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CommonVariables.setBaseActivityCount(CommonVariables.getBaseActivityCount() + 1);
		super.onCreate(savedInstanceState);
		setView();
		initBottom();
		setBottomListner();
		init();
	}
	
	protected abstract void setView();
	protected abstract void init();

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CommonVariables.setBaseActivityCount(CommonVariables.getBaseActivityCount() - 1);
		if(CommonVariables.getBaseActivityCount()==0)
		{
			stopService(new Intent(BaseActivity.this,ReciveMsgService.class));
		}
	}
	
	private void initBottom()
	{
		charsView= (TextView)findViewById(R.id.chatsView);
		personsView=(TextView)findViewById(R.id.personsView);
		groupsView=(TextView)findViewById(R.id.groupsView);
	}
	
	private void setBottomListner()
	{
		charsView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BaseActivity.this, HomeActivity.class);
	            startActivity(intent);
	            finish();
			}
		});
		
		personsView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BaseActivity.this, PersonsActivity.class);
	            startActivity(intent);
	            finish();
			}
		});
		
		groupsView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BaseActivity.this, GroupsActivity.class);
	            startActivity(intent);
	            finish();
			}
		});
	}
}
