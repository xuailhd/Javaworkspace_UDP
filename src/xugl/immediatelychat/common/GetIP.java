package xugl.immediatelychat.common;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class GetIP extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public String GetHostIP()
	{
		String ip="1";
		//获取wifi服务  
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        //判断wifi是否开启  
        if (wifiManager.isWifiEnabled()) {  
        	WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
            int ipAddress = wifiInfo.getIpAddress();   
            ip = intToIp(ipAddress);   
        }  
         
        return ip;
	}
	
	private String intToIp(int i) {       
        
        return (i & 0xFF ) + "." +       
      ((i >> 8 ) & 0xFF) + "." +       
      ((i >> 16 ) & 0xFF) + "." +       
      ( i >> 24 & 0xFF) ;  
   } 
}
