package xugl.immediatelychat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import xugl.immediatelychat.common.CommonFlag;
import xugl.immediatelychat.common.CommonVariables;
import xugl.immediatelychat.models.ChatModel;
import xugl.immediatelychat.models.MsgRecord;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ReciveMsgService extends Service {

	private ReciveMsgThread reciveMsgThread;
	private SendMsgThread sendMsgThread;

	// private static final String TAG = "ReciveMsgService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new LocalBinder();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);

		reciveMsgThread = new ReciveMsgThread();
		reciveMsgThread.start();

		sendMsgThread = new SendMsgThread();
		sendMsgThread.start();
		return 1;

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		reciveMsgThread.setGoonRunning(false);
		sendMsgThread.setGoonRunning(false);
	}

	public class LocalBinder extends Binder {
		// 返回本地服务
		public ReciveMsgService getService() {
			return ReciveMsgService.this;
		}
	}

	public class ReciveMsgThread extends Thread {

		private boolean isGoonRunning;

		public void setGoonRunning(boolean isGoonRunning) {
			this.isGoonRunning = isGoonRunning;
		}

		public ReciveMsgThread() {
			isGoonRunning = true;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			char[] charbuffer = new char[1024];
			int charcount = 0;
			Socket sockettoServer = null;
			OutputStream ou = null;
			InputStream in = null;
			JSONObject jsonObject = null;
			String message = null;
			ChatModel chatModel = null;
			MsgRecord msgRecord = null;
			Intent intent = null;
			try {
				while (isGoonRunning) {
					try {
						sockettoServer = new Socket();
						sockettoServer.connect(
								new InetSocketAddress(CommonVariables
										.getMCSIP(), CommonVariables
										.getMCSPort()), 5000);

						ou = sockettoServer.getOutputStream();
						in = sockettoServer.getInputStream();

						jsonObject = new JSONObject();
						jsonObject.put(CommonFlag.getF_ObjectID(),
								CommonVariables.getObjectID());
						jsonObject.put(CommonFlag.getF_LatestTime(),
								CommonVariables.getLatestTime());
						String msg = CommonFlag.getF_MCSVerifyUAGetMSG()
								+ jsonObject.toString();

						ou.write(msg.getBytes("UTF-8"));
						ou.flush();

						BufferedReader bff = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						charcount = bff.read(charbuffer);

						while (charcount > 0) {
							msgRecord = null;
							message = String.valueOf(charbuffer, 0, charcount);
							jsonObject = new JSONObject(message);

							chatModel = CommonVariables.getChatOperate()
									.GetChat(jsonObject, ReciveMsgService.this);

							msgRecord = CommonVariables.getMsgRecordOperate()
									.SaveMsgRecord(jsonObject,
											chatModel.getChatID(),
											ReciveMsgService.this);

							if (msgRecord == null) {
								break;
							}

							if (jsonObject
									.getString(CommonFlag.getF_SendTime())
									.compareTo(CommonVariables.getLatestTime()) > 0) {
								CommonVariables.setLatestTime(jsonObject
										.getString(CommonFlag.getF_SendTime()));
								CommonVariables.getContactDataOperate()
										.UpdateLatestTime(
												CommonVariables.getObjectID(),
												jsonObject.getString(CommonFlag
														.getF_SendTime()),
												ReciveMsgService.this);
							}

							// 通知 chatActivity
							intent = new Intent();
							intent.putExtra("MsgRecord", msgRecord);
							intent.setAction(chatModel.getChatID()); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
							sendBroadcast(intent); // 发送广播

							msg = CommonFlag.getF_MCSReciveUAMSGFB()
									+ msgRecord.getMsgID();
							ou.write(msg.getBytes("UTF-8"));
							ou.flush();

							charcount = bff.read(charbuffer);
						}
						if (msgRecord != null) {
							// 通知HomeActivity
							intent = new Intent();
							intent.putExtra("Msg", "NewMsg");
							intent.setAction(CommonVariables.getObjectID()
									+ "Home"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
							sendBroadcast(intent); // 发送广播
							msgRecord = null;
						}
						ou.close();
						in.close();
						sockettoServer.close();
						sockettoServer = null;
						Thread.sleep(500);
					} catch (IOException ex) {
					}
				}
			} catch (Exception ex) {
			}
		}
		
//		public void run() {
//			JSONObject jsonObject = null;
//			ChatModel chatModel = null;
//			MsgRecord msgRecord = null;
//			Intent intent = null;
//			try {
//				while (isGoonRunning) {
//					jsonObject = new JSONObject();
//					jsonObject.put(CommonFlag.getF_ObjectID(),
//							CommonVariables.getObjectID());
//					jsonObject.put(CommonFlag.getF_LatestTime(),
//							CommonVariables.getLatestTime());
//					String msg = CommonFlag.getF_MCSVerifyUAGetMSG()
//							+ jsonObject.toString();
//
//					msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMCSIP(), CommonVariables.getMCSPort(), 
//							msg);
//					while (msg!=null && msg.length()>0) {
//						msgRecord = null;
//						jsonObject = new JSONObject(msg);
//
//						chatModel = CommonVariables.getChatOperate()
//								.GetChat(jsonObject, ReciveMsgService.this);
//
//						msgRecord = CommonVariables.getMsgRecordOperate()
//								.SaveMsgRecord(jsonObject,
//										chatModel.getChatID(),
//										ReciveMsgService.this);
//
//						if (msgRecord == null) {
//							break;
//						}
//
//						if (jsonObject
//								.getString(CommonFlag.getF_SendTime())
//								.compareTo(CommonVariables.getLatestTime()) > 0) {
//							CommonVariables.setLatestTime(jsonObject
//									.getString(CommonFlag.getF_SendTime()));
//							CommonVariables.getContactDataOperate()
//									.UpdateLatestTime(
//											CommonVariables.getObjectID(),
//											jsonObject.getString(CommonFlag
//													.getF_SendTime()),
//											ReciveMsgService.this);
//						}
//
//						// 通知 chatActivity
//						intent = new Intent();
//						intent.putExtra("MsgRecord", msgRecord);
//						intent.setAction(chatModel.getChatID()); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//						sendBroadcast(intent); // 发送广播
//
//						msg = CommonFlag.getF_MCSReciveUAMSGFB()
//								+ msgRecord.getMsgID();
//						msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMCSIP(), CommonVariables.getMCSPort(), 
//								msg);
//					}
//					if (msgRecord != null) {
//						// 通知HomeActivity
//						intent = new Intent();
//						intent.putExtra("Msg", "NewMsg");
//						intent.setAction(CommonVariables.getObjectID()
//								+ "Home"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
//						sendBroadcast(intent); // 发送广播
//						msgRecord = null;
//					}
//					Thread.sleep(500);
//				}
//			} catch (Exception ex) {
//			}
//		}
	}

	public class SendMsgThread extends Thread {

		private boolean isGoonRunning;

		public void setGoonRunning(boolean isGoonRunning) {
			this.isGoonRunning = isGoonRunning;
		}

		public SendMsgThread() {
			isGoonRunning = true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			char[] charbuffer = new char[1024];
			int charcount = 0;
			Socket sockettoServer = null;
			OutputStream ou = null;
			InputStream in = null;
			MsgRecord msgRecord = null;
			Intent intent = null;
			int i = 0;
			try {
				while (isGoonRunning) {
					try {

						if (CommonVariables.getMsgRecordBuffer().size() > 0) {
							sockettoServer = new Socket();
							sockettoServer.connect(
									new InetSocketAddress(CommonVariables
											.getMCSIP(), CommonVariables
											.getMCSPort()), 5000);

							ou = sockettoServer.getOutputStream();
							in = sockettoServer.getInputStream();
							while (CommonVariables.getMsgRecordBuffer().size() > 0) {
								msgRecord = CommonVariables
										.getMsgRecordBuffer().get(i);

								CommonVariables.getMsgRecordOperate()
										.SaveMsgRecord(msgRecord,
												ReciveMsgService.this);

								JSONObject msgobject = SetMSGBox(msgRecord);
								ou.write((CommonFlag.getF_MCSVerifyUAMSG() + msgobject
										.toString()).getBytes("UTF-8"));

								ou.flush();

								BufferedReader bff = new BufferedReader(
										new InputStreamReader(in, "UTF-8"));
								charcount = bff.read(charbuffer);
								if (charcount > 0) {
									if (msgRecord.getMsgID().equalsIgnoreCase(
											String.valueOf(charbuffer, 0,
													charcount))) {
										CommonVariables.getMsgRecordOperate()
												.UpdateIsSend(
														msgRecord.getMsgID(),
														msgRecord.getChatID(),
														ReciveMsgService.this);

										CommonVariables.getMsgRecordBuffer()
												.remove(0);
										intent = new Intent();
										intent.putExtra("FinishSend",
												msgRecord.getMsgID());
										intent.setAction(msgRecord.getChatID());
										sendBroadcast(intent); // 发送广播
									}
								}
							}
							ou.close();
							in.close();
							sockettoServer.close();
						}

					} catch (IOException ex) {
					}
					Thread.sleep(500);

				}
			} catch (Exception ex) {
			}
		}
		
//		public void run() {
//			// TODO Auto-generated method stub
//			MsgRecord msgRecord = null;
//			Intent intent = null;
//			int i = 0;
//			try {
//				while (isGoonRunning) {
//					if (CommonVariables.getMsgRecordBuffer().size() > 0) {
//						while (CommonVariables.getMsgRecordBuffer().size() > 0) {
//							msgRecord = CommonVariables
//									.getMsgRecordBuffer().get(i);
//
//							JSONObject msgobject = SetMSGBox(msgRecord);
//							
//							String msg = CommonFlag.getF_MCSVerifyUAMSG() + msgobject
//									.toString();
//							msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMCSIP(),
//									CommonVariables.getMCSPort(), msg);
//
//							if (msg!=null &&  msg.length()>0) {
//								if (msgRecord.getMsgID().equalsIgnoreCase(msg)) {
//									CommonVariables.getMsgRecordOperate()
//											.UpdateIsSend(
//													msgRecord.getMsgID(),
//													msgRecord.getChatID(),
//													ReciveMsgService.this);
//
//									CommonVariables.getMsgRecordBuffer()
//											.remove(i);
//									intent = new Intent();
//									intent.putExtra("FinishSend",
//											msgRecord.getMsgID());
//									intent.setAction(msgRecord.getChatID());
//									sendBroadcast(intent); // 发送广播
//								}
//							}
//						}
//					}
//					Thread.sleep(100);
//				}
//			} catch (Exception ex) {
//			}
//		}

		private JSONObject SetMSGBox(MsgRecord msgRecord) {
			JSONObject msgObject = new JSONObject();
			try {
				msgObject.put(CommonFlag.getF_MsgSenderObjectID(),
						msgRecord.getMsgSenderObjectID());
				msgObject.put(CommonFlag.getF_MsgSenderName(),
						msgRecord.getMsgSenderName());
				msgObject.put(CommonFlag.getF_MsgContent(),
						msgRecord.getMsgContent());
				msgObject.put(CommonFlag.getF_MsgRecipientObjectID(),
						msgRecord.getMsgRecipientObjectID());
				msgObject.put(CommonFlag.getF_MsgRecipientGroupID(),
						msgRecord.getMsgRecipientGroupID());
				msgObject
						.put(CommonFlag.getF_MsgType(), msgRecord.getMsgType());
				msgObject.put(CommonFlag.getF_SendTime(),
						msgRecord.getSendTime());
				msgObject.put(CommonFlag.getF_MsgID(), msgRecord.getMsgID());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return msgObject;// CommonFlag.getF_MCSVerifyUAMSG() +
								// msgObject.toString();
		}
	}
}
