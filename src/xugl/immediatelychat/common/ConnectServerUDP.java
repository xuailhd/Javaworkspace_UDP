package xugl.immediatelychat.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectServerUDP implements IConnectServer {

	private int ReTryPSCount = 1;
	private int ReTryMMSCount = 3;
	private int ReTryMCSCount = 3;

	@Override
	public void postAccount(final String account, final String password,
			final Context packageContext) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent intent = null;
				int psReturn = -1;
				try {
					while (ReTryPSCount > 0) {
						psReturn = ConnectPS(account, password, packageContext);
						if (psReturn == 0) {
							ReTryPSCount = 0;
							while (ReTryMMSCount > 0) {
								// connect MMS
								if (ConnectMMS(packageContext)) {
									ReTryMMSCount = 0;
									// connect MCS
									while (ReTryMCSCount > 0) {
										
										if (ConnectMCS(packageContext)) {
											return;
										} else {
											ReTryMCSCount--;
										}
									}

									intent = new Intent(); // Itent就是我们要发送的内容
									intent.putExtra("MSG",
											"Can not connect MCS");
									intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
									packageContext.sendBroadcast(intent); // 发送广播
									return;
								} else {
									ReTryMMSCount--;
								}
							}
							intent = new Intent(); // Itent就是我们要发送的内容
							intent.putExtra("MSG", "Can not connect MMS");
							intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
							packageContext.sendBroadcast(intent); // 发送广播
							return;
						} else {
							ReTryPSCount--;
							Thread.sleep(500);
						}
					}

					if (psReturn != 1 && psReturn != 2 && psReturn != 0) {
						intent = new Intent(); // Itent就是我们要发送的内容
						intent.putExtra("MSG", "Can not connect PS");
						intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
						packageContext.sendBroadcast(intent); // 发送广播
						return;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();

	}

	private int ConnectPS(String account, String password,
			Context packageContext) {
		Intent intent;
		HttpGet httpRequest = new HttpGet("http://" + CommonVariables.getPSIP()
				+ ":" + CommonVariables.getPSPort()
				+ "/ContactPerson/LoginForAPI?" + CommonFlag.getF_Account()
				+ "=" + account + "&" + CommonFlag.getF_Password() + "="
				+ password);
		try {
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读 */
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				JSONObject psresultjsonObject = new JSONObject(strResult);

				int status = psresultjsonObject.getInt("Status");
				if (status == 1) {
					intent = new Intent(); // Itent就是我们要发送的内容
					intent.putExtra("MSG", "Password incorrect");
					intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
					packageContext.sendBroadcast(intent); // 发送广播
					return 1;
				}

				if (status == 2) {
					intent = new Intent(); // Itent就是我们要发送的内容
					intent.putExtra("MSG", "MMS server have not start");
					intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
					packageContext.sendBroadcast(intent); // 发送广播
					return 2;
				}

				if (status == 3) {
					if (sendRegister(account, password, packageContext)) {
						httpResponse = new DefaultHttpClient()
								.execute(httpRequest);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							strResult = EntityUtils.toString(httpResponse
									.getEntity());
							psresultjsonObject = new JSONObject(strResult);
						}
					} else {
						return 3;
					}
				}

				CommonVariables.setMMSIP(psresultjsonObject.getString("IP"));
				CommonVariables.setMMSPort(psresultjsonObject.getInt("Port"));
				CommonVariables.setObjectID(psresultjsonObject
						.getString("ObjectID"));
				CommonVariables.setAccount(account);
				return 0;

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Test", e.getMessage());
		}
		return 3;
	}

	private boolean sendRegister(String account, String password,
			Context packageContext) {
		HttpGet httpRequest = new HttpGet("http://" + CommonVariables.getPSIP()
				+ ":" + CommonVariables.getPSPort()
				+ "/ContactPerson/Register?" + CommonFlag.getF_Account() + "="
				+ account + "&" + CommonFlag.getF_Password() + "=" + password);
		try {
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读 */
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				if (strResult.equals("register failed")) {
					return false;
				}

				return true;

			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return false;
	}

	private boolean ConnectMMS(Context packageContext) {
		try {
//			CommonVariables.getContactDataOperate().CleanPersonInfo(CommonVariables.getObjectID(),
//			 packageContext);
			CommonVariables.getContactDataOperate().InitContactPersonInfo(
					CommonVariables.getObjectID(), packageContext);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put(CommonFlag.getF_ObjectID(),
					CommonVariables.getObjectID());
			jsonObject.put(CommonFlag.getF_LatestTime(),
					CommonVariables.getLatestTime());
			jsonObject.put(CommonFlag.getF_UpdateTime(),
					CommonVariables.getUpdateTime());
			String msg = CommonFlag.getF_MMSVerifyUA() + jsonObject.toString();

			msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMMSIP(), 
					CommonVariables.getMMSPort(), msg);

			Log.e("Test", "ConnectMMS :"+ msg);
			
			jsonObject = new JSONObject(msg);
			CommonVariables.setMCSIP(jsonObject.getString("MCS_IP"));
			CommonVariables.setMCSPort(jsonObject.getInt("MCS_Port"));

			String mmsUpdateTime = jsonObject.getString("UpdateTime");

			if (mmsUpdateTime.compareTo(CommonVariables.getUpdateTime()) > 0) {
				jsonObject = new JSONObject();
				jsonObject.put(CommonFlag.getF_ObjectID(),
						CommonVariables.getObjectID());
				jsonObject.put(CommonFlag.getF_UpdateTime(),
						CommonVariables.getUpdateTime());
				msg = CommonFlag.getF_MMSVerifyUAGetUAInfo()
						+ jsonObject.toString();
				
				msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMMSIP(), 
					CommonVariables.getMMSPort(), msg);

				while (msg!=null) {
					jsonObject = new JSONObject(msg);
					msg = CommonFlag.getF_MMSVerifyFBUAGetUAInfo()
							+ CommonVariables.getContactDataOperate()
									.SaveContactData(
											CommonVariables.getObjectID(),
											jsonObject, packageContext);
					msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMMSIP(), 
							CommonVariables.getMMSPort(), msg);
				}
			}
			jsonObject = null;
			return true;
		} catch (Exception ex) {
			Log.e("Test", "ConnectMMS error:"+ ex.getMessage());
		}
		return false;
	}

	private boolean ConnectMCS(Context packageContext) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(CommonFlag.getF_ObjectID(),
					CommonVariables.getObjectID());
			jsonObject.put(CommonFlag.getF_UpdateTime(),
					CommonVariables.getUpdateTime());
			jsonObject.put(CommonFlag.getF_LatestTime(), CommonVariables.getLatestTime());
			String msg = CommonFlag.getF_MCSVerifyUA() + jsonObject.toString();
			
			msg = CommonVariables.getSocketManage().sendMsgWithReceive(CommonVariables.getMCSIP(),
					CommonVariables.getMCSPort(), msg);
			
			if (msg.equalsIgnoreCase("ok")) {
				Intent intent = new Intent(); // Itent就是我们要发送的内容
				intent.putExtra("MSG", "Success");
				intent.setAction("LoginActivity"); // 设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
				packageContext.sendBroadcast(intent); // 发送广播
				return true;
			} 
		} catch (Exception ex) {
		}

		return false;
	}

}
