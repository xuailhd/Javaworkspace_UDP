package xugl.immediatelychat.activitys;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import xugl.immediatelychat.R;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.ContactPersonList;

public class PersonsActivity extends BaseActivity {
	private LinearLayout mainLayout;
	private ScrollView scrollView;
	private TextView addperson;
	@Override
	protected void setView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_person);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		mainLayout=(LinearLayout)findViewById(R.id.personslayout);
		scrollView=(ScrollView)findViewById(R.id.personsScrollView);
		addperson=(TextView)findViewById(R.id.addperson);

		addperson.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(PersonsActivity.this, AddPersonActivity.class);
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
		ContactPersonList[] contactPersonLists= CommonVariables.getContactDataOperate().LoadContactPersonList(CommonVariables.getObjectID(), this);
		if(contactPersonLists==null || contactPersonLists.length<1)
		{
			return;
		}
		CleanViews();
		for(int i=0;i<contactPersonLists.length;i++)
		{
			addPersonIntoView(contactPersonLists[i]);
		}
	}

	private void addPersonIntoView(final ContactPersonList contactPersonList)
	{
		ImageView pic=new ImageView(PersonsActivity.this);
		pic.setImageResource(R.drawable.ic_launcher);
		
		TextView name=new TextView(PersonsActivity.this);
		name.setText(contactPersonList.getContactPersonName());
		
		LinearLayout linearLayout=new LinearLayout(PersonsActivity.this);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.addView(pic);
		linearLayout.addView(name);
		
		linearLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChatModel chatModel = CommonVariables.getChatOperate().GetChat(contactPersonList.getDestinationObjectID(), 
						contactPersonList.getContactPersonName(), 1, PersonsActivity.this);
				
				Intent intent=new Intent();
				intent.putExtra("ChatModel", chatModel);
				intent.setClass(PersonsActivity.this, ChatActivity.class);
				startActivity(intent);
			}});

		mainLayout.addView(linearLayout);
	}
}
