package com.example.wifitest;
import java.util.List;  

import android.content.Context;  
import android.net.wifi.ScanResult;  
import android.net.wifi.WifiConfiguration;  
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager;  
import android.net.wifi.WifiManager.WifiLock;  
  
public class WifiAdmin {  
    //定义一个WifiManager对象  
    private WifiManager mWifiManager;  
    //定义一个WifiInfo对象  
   // private WifiInfo mWifiInfo;  
    //扫描出的网络连接列表  
    private List<ScanResult> mWifiList;  
    //网络连接列表  
    private List<WifiConfiguration> mWifiConfigurations;  
    WifiLock mWifiLock;  
    public WifiAdmin(Context context){  
        //取得WifiManager对象  
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
    }  
    //打开wifi  
    public void openWifi(){  
        if(!mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(true);  
        }  
    }  
    //关闭wifi  
    public void closeWifi(){  
        if(mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(false);  
        }  
    }  
     // 检查当前wifi状态    
    public int checkState() {    
        return mWifiManager.getWifiState();    
    }    
    //锁定wifiLock  
    public void acquireWifiLock(){  
        mWifiLock.acquire();  
    }  
    //解锁wifiLock  
    public void releaseWifiLock(){  
        //判断是否锁定  
        if(mWifiLock.isHeld()){  
            mWifiLock.acquire();  
        }  
    }  
    //创建一个wifiLock  
    public void createWifiLock(){  
        mWifiLock=mWifiManager.createWifiLock("test");  
    }  
    //得到配置好的网络  
    public List<WifiConfiguration> getConfiguration(){  
        return mWifiConfigurations;  
    }  
    //指定配置好的网络进行连接  
    public void connetionConfiguration(int index){  
        if(index>mWifiConfigurations.size()){  
            return ;  
        }  
        //连接配置好指定ID的网络  
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);  
    }  
    public void startScan(){  
        mWifiManager.startScan();  
        //得到扫描结果  
        mWifiList=mWifiManager.getScanResults();  
        //得到配置好的网络连接  
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();  
    }  
    //得到网络列表  
    public List<ScanResult> getWifiList(){  
        return mWifiList;  
    }  
    //查看扫描结果  
    public StringBuffer lookUpScan(){  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<mWifiList.size();i++){  
            sb.append("Index_" + new Integer(i + 1).toString() + ":");  
             // 将ScanResult信息转换成一个字符串包    
            // 其中把包括：BSSID、SSID、capabilities、frequency、level    
            sb.append((mWifiList.get(i)).toString()).append("\n");  
        }  
        return sb;    
    }  
    public String getMacAddress(){  
        return mWifiManager.getConnectionInfo().getMacAddress();  
    }  
    public String getBSSID(){  
        return mWifiManager.getConnectionInfo().getBSSID();  
    }  
    public int getIpAddress(){  
        return mWifiManager.getConnectionInfo().getIpAddress();  
    }  
    //得到连接的ID  
    public int getNetWordId(){  
        return mWifiManager.getConnectionInfo().getNetworkId();  
    }  
    //得到wifiInfo的所有信息  
    public String getWifiInfo(){  
        return mWifiManager.getConnectionInfo().toString();  
    }  
    //得到连接速度
    public int getLinkSpeed(){  
        return mWifiManager.getConnectionInfo().getLinkSpeed();  
    }  
    public int getRssi() {
    	return mWifiManager.getConnectionInfo().getRssi();
	} 
    //添加一个网络并连接  
    public boolean addNetWork(WifiConfiguration configuration){  
        int wcgId=mWifiManager.addNetwork(configuration);  
        return mWifiManager.enableNetwork(wcgId, true);  
    }  
    //断开指定ID的网络  
    public void disConnectionWifi(int netId){  
        mWifiManager.disableNetwork(netId);  
        mWifiManager.disconnect();  
    }  
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)  
    {  
    	WifiConfiguration config = new WifiConfiguration();    
    	config.allowedAuthAlgorithms.clear();  
    	config.allowedGroupCiphers.clear();  
    	config.allowedKeyManagement.clear();  
    	config.allowedPairwiseCiphers.clear();  
    	config.allowedProtocols.clear();  
    	config.SSID = "\"" + SSID + "\"";    
           
    	WifiConfiguration tempConfig = this.IsExsits(SSID);            
    	if(tempConfig != null) {   
    		mWifiManager.removeNetwork(tempConfig.networkId);   
    	} 
           
    	if(Type == 1){ //WIFICIPHER_NOPASS 
    		config.wepKeys[0] = "";  
    		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
    		config.wepTxKeyIndex = 0;  
		}else if(Type == 2){ //WIFICIPHER_WEP 
    		config.hiddenSSID = true; 
            config.wepKeys[0]= "\""+Password+"\"";  
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
            config.wepTxKeyIndex = 0; 
        }else if(Type == 3) { //WIFICIPHER_WPA 
        	config.preSharedKey = "\""+Password+"\"";  
        	config.hiddenSSID = true;    
        	config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
        	config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
        	config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
        	config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
        	//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
        	config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
        	config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
        	config.status = WifiConfiguration.Status.ENABLED;    
        } 
           return config;  
    }  
     
    public WifiConfiguration IsExsits(String SSID)   
    {   
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();   
           for (WifiConfiguration existingConfig : existingConfigs)    
           {   
             if (existingConfig.SSID.equals("\""+SSID+"\""))   
             {   
                 return existingConfig;   
             }   
           }   
        return null;    
    }
	
}  
