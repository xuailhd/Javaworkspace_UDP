package xugl.immediatelychat.activitys;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.ContactGroup;

public class GroupsActivity extends BaseActivity {
	private LinearLayout mainLayout;
	private ScrollView scrollView;
	private TextView addGroup;
	
	@Override
	protected void setView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_group);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub

		mainLayout=(LinearLayout)findViewById(R.id.groupslayout);
		scrollView=(ScrollView)findViewById(R.id.groupsScrollView);
		addGroup=(TextView)findViewById(R.id.addgroup);

		addGroup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(GroupsActivity.this, AddGroupActivity.class);
				startActivity(intent);
			}
			
		});
		
		
	}
	
	private void CleanViews()
	{
		mainLayout.removeAllViews();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ContactGroup[] contactGroups= CommonVariables.getContactDataOperate().LoadContactGroup(CommonVariables.getObjectID(), this);
		if(contactGroups==null || contactGroups.length<1)
		{
			return;
		}
		CleanViews();
		for(int i=0;i<contactGroups.length;i++)
		{
			addGroupIntoView(contactGroups[i]);
		}
	}

	private void addGroupIntoView(final ContactGroup contactGroup)
	{
		ImageView pic=new ImageView(GroupsActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		
		TextView name=new TextView(GroupsActivity.this);
		name.setText(contactGroup.getGroupName());
		
		LinearLayout linearLayout=new LinearLayout(GroupsActivity.this);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.addView(pic);
		linearLayout.addView(name);
		
		linearLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChatModel chatModel = CommonVariables.getChatOperate().GetChat(contactGroup.getGroupObjectID(), 
						contactGroup.getGroupName(), 2, GroupsActivity.this);
				
				Intent intent=new Intent();
				intent.putExtra("ChatModel", chatModel);
				intent.setClass(GroupsActivity.this, ChatActivity.class);
				startActivity(intent);
			}});
		
		mainLayout.addView(linearLayout);
	}
	
}
