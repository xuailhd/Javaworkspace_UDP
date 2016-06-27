package xugl.immediatelychat.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xugl.immediatelychat.contactdata.IContactDataOperate;
import xugl.immediatelychat.models.MsgRecord;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendMsg implements ISendMsg {
	public void doSend(final MsgRecord msgRecord, final Context packageContext) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		CommonVariables.getMsgRecordBuffer().add(msgRecord);
		CommonVariables.getMsgRecordOperate().SaveMsgRecord(msgRecord,
				packageContext);
		CommonVariables.getChatOperate().UpdateLatestMsg(msgRecord,
				packageContext);
		// }
		// }).start();
	}

	// private JSONObject SetMSGBox(MsgRecord msgRecord) {
	// JSONObject msgObject = new JSONObject();
	// try {
	// msgObject.put(CommonFlag.getF_MsgSenderObjectID(),
	// msgRecord.getMsgSenderObjectID());
	// msgObject.put(CommonFlag.getF_MsgSenderName(),
	// msgRecord.getMsgSenderName());
	// msgObject.put(CommonFlag.getF_MsgContent(), msgRecord.getMsgContent());
	// msgObject.put(CommonFlag.getF_MsgRecipientObjectID(),
	// msgRecord.getMsgRecipientObjectID());
	// msgObject.put(CommonFlag.getF_MsgRecipientGroupID(),
	// msgRecord.getMsgRecipientGroupID());
	// msgObject.put(CommonFlag.getF_MsgType(), msgRecord.getMsgType());
	// msgObject.put(CommonFlag.getF_SendTime(), msgRecord.getSendTime());
	// msgObject.put(CommonFlag.getF_MsgID(),msgRecord.getMsgID());
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return msgObject;//CommonFlag.getF_MCSVerifyUAMSG() +
	// msgObject.toString();
	// }

	@Override
	public void sendSearchRequest(final String key, final int type,
			final Context packageContext) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				int RetryCount = 3;
				while(RetryCount>0)
				{
					try {
						
						int charcount;
						JSONArray jsonArray = null;
						String retrunStr = null;
						char[] charbuffer = new char[1024];
						Socket sockettoServer = new Socket();
						sockettoServer.connect(
								new InetSocketAddress(CommonVariables.getMMSIP(),
										CommonVariables.getMMSPort()), 5000);

						OutputStream ou = sockettoServer.getOutputStream();
						InputStream in = sockettoServer.getInputStream();

						JSONObject jsonObject = new JSONObject();
						jsonObject.put(CommonFlag.getF_ObjectID(),
								CommonVariables.getObjectID());
						jsonObject.put(CommonFlag.getF_Type(), type);
						jsonObject.put(CommonFlag.getF_SearchKey(), key);
						String msg = CommonFlag.getF_MMSVerifyUASearch()
								+ jsonObject.toString();
						ou.write(msg.getBytes("UTF-8"));
						ou.flush();

						BufferedReader bff = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						charcount = bff.read(charbuffer);

						if (charcount > 0) {
							jsonArray = new JSONArray();
						}

						while (charcount > 0) {
							retrunStr = String.valueOf(charbuffer, 0, charcount);
							jsonObject = new JSONObject(retrunStr);
							jsonArray.put(jsonObject);
							msg = CommonFlag.getF_MMSVerifyUAFBSearch()
									+ jsonObject.getString("ContactDataID");
							ou.write(msg.getBytes("UTF-8"));
							ou.flush();
							charcount = bff.read(charbuffer);
						}

						if (jsonArray != null && jsonArray.length() > 0) {
							intent.putExtra("SearchResult", jsonArray.toString());
						} else {
							intent.putExtra("SearchResult", "No Result");
						}
						ou.close();
						in.close();
						sockettoServer.close();
						break;
					} catch (JSONException e) {
						e.printStackTrace();
						intent.putExtra("SearchResult", "No Result");
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						RetryCount--;
					}
				}
				
				intent.putExtra("BroadCastType", "Search");

				if (type == 1) {
					intent.setAction("SearchPerson");
					packageContext.sendBroadcast(intent);
				} else if (type == 2) {
					intent.setAction("SearchGroup");
					packageContext.sendBroadcast(intent);
				}
			}
		}).start();
	}

	@Override
	public void sendAddPersonRequest(final String objectID,
			final Context packageContext) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int RetryCount = 3;
				Intent intent = new Intent();
				intent.setAction("SearchPerson");
				while (RetryCount > 0) {
					try {
						int charcount;
						String retrunStr = null;
						char[] charbuffer = new char[1024];
						Socket sockettoServer = new Socket();
						sockettoServer.connect(
								new InetSocketAddress(CommonVariables
										.getMMSIP(), CommonVariables
										.getMMSPort()), 5000);

						OutputStream ou = sockettoServer.getOutputStream();
						InputStream in = sockettoServer.getInputStream();

						JSONObject jsonObject = new JSONObject();
						jsonObject.put(CommonFlag.getF_ObjectID(),
								CommonVariables.getObjectID());
						jsonObject.put(CommonFlag.getF_DestinationObjectID(),
								objectID);
						jsonObject.put(CommonFlag.getF_MCS_IP(),
								CommonVariables.getMCSIP());
						jsonObject.put(CommonFlag.getF_MCS_Port(),
								CommonVariables.getMCSPort());

						String msg = CommonFlag.getF_MMSVerifyUAAddPerson()
								+ jsonObject.toString();
						ou.write(msg.getBytes("UTF-8"));
						ou.flush();

						BufferedReader bff = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						charcount = bff.read(charbuffer);
						ou.close();
						in.close();
						sockettoServer.close();
						if (charcount > 0) {
							retrunStr = String
									.valueOf(charbuffer, 0, charcount);
							jsonObject = new JSONObject(retrunStr);
							CommonVariables.getContactDataOperate()
									.SaveContactData(
											CommonVariables.getObjectID(),
											jsonObject, packageContext);
//							CommonVariables.getChatOperate().SaveChat(objectID,
//									jsonObject.getString("ContactPersonName"),
//									1, packageContext);
							intent.putExtra("Status", 0);
						} else {
							intent.putExtra("Status", 2);
						}
						break;
					} catch (JSONException e) {
						e.printStackTrace();
						intent.putExtra("Status", 2);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						RetryCount--;
					}
				}
				if (RetryCount == 0) {
					intent.putExtra("Status", 2);
				}
				intent.putExtra("BroadCastType", "Add");
				packageContext.sendBroadcast(intent);
			}
		}).start();
	}

	@Override
	public void sendAddGroupRequest(final String groupID,
			final Context packageContext) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int RetryCount = 3;
				Intent intent = new Intent();
				intent.setAction("SearchGroup");
				while (RetryCount > 0) {
					try {
						int charcount;
						String retrunStr = null;
						char[] charbuffer = new char[1024];
						Socket sockettoServer = new Socket();
						sockettoServer.connect(
								new InetSocketAddress(CommonVariables
										.getMMSIP(), CommonVariables
										.getMMSPort()), 5000);

						OutputStream ou = sockettoServer.getOutputStream();
						InputStream in = sockettoServer.getInputStream();

						JSONObject jsonObject = new JSONObject();
						jsonObject.put(CommonFlag.getF_ObjectID(),
								CommonVariables.getObjectID());
						jsonObject.put(CommonFlag.getF_GroupObjectID(), groupID);
						jsonObject.put(CommonFlag.getF_MCS_IP(),
								CommonVariables.getMCSIP());
						jsonObject.put(CommonFlag.getF_MCS_Port(),
								CommonVariables.getMCSPort());

						String msg = CommonFlag.getF_MMSVerifyUAAddGroup()
								+ jsonObject.toString();
						ou.write(msg.getBytes("UTF-8"));
						ou.flush();

						BufferedReader bff = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						charcount = bff.read(charbuffer);
						ou.close();
						in.close();
						sockettoServer.close();
						if (charcount > 0) {
							retrunStr = String
									.valueOf(charbuffer, 0, charcount);
							jsonObject = new JSONObject(retrunStr);
							CommonVariables.getContactDataOperate()
									.SaveContactData(
											CommonVariables.getObjectID(),
											jsonObject, packageContext);
//							CommonVariables.getChatOperate().SaveChat(groupID,
//									jsonObject.getString("GroupName"), 2,
//									packageContext);
							intent.putExtra("Status", 0);
						} else {
							intent.putExtra("Status", 2);
						}
						break;
					} catch (JSONException e) {
						e.printStackTrace();
						intent.putExtra("Status", 2);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						RetryCount--;
					}
				}
				if (RetryCount == 0) {
					intent.putExtra("Status", 2);
				}
				intent.putExtra("BroadCastType", "Add");
				packageContext.sendBroadcast(intent);
			}
		}).start();
	}
}
