package com.example.wifitest;
import java.util.List;  

import android.content.Context;  
import android.net.wifi.ScanResult;  
import android.net.wifi.WifiConfiguration;  
import android.net.wifi.WifiInfo;  
import android.net.wifi.WifiManager;  
import android.net.wifi.WifiManager.WifiLock;  
  
public class WifiAdmin {  
    //����һ��WifiManager����  
    private WifiManager mWifiManager;  
    //����һ��WifiInfo����  
   // private WifiInfo mWifiInfo;  
    //ɨ��������������б�  
    private List<ScanResult> mWifiList;  
    //���������б�  
    private List<WifiConfiguration> mWifiConfigurations;  
    WifiLock mWifiLock;  
    public WifiAdmin(Context context){  
        //ȡ��WifiManager����  
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
    }  
    //��wifi  
    public void openWifi(){  
        if(!mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(true);  
        }  
    }  
    //�ر�wifi  
    public void closeWifi(){  
        if(mWifiManager.isWifiEnabled()){  
            mWifiManager.setWifiEnabled(false);  
        }  
    }  
     // ��鵱ǰwifi״̬    
    public int checkState() {    
        return mWifiManager.getWifiState();    
    }    
    //����wifiLock  
    public void acquireWifiLock(){  
        mWifiLock.acquire();  
    }  
    //����wifiLock  
    public void releaseWifiLock(){  
        //�ж��Ƿ�����  
        if(mWifiLock.isHeld()){  
            mWifiLock.acquire();  
        }  
    }  
    //����һ��wifiLock  
    public void createWifiLock(){  
        mWifiLock=mWifiManager.createWifiLock("test");  
    }  
    //�õ����úõ�����  
    public List<WifiConfiguration> getConfiguration(){  
        return mWifiConfigurations;  
    }  
    //ָ�����úõ������������  
    public void connetionConfiguration(int index){  
        if(index>mWifiConfigurations.size()){  
            return ;  
        }  
        //�������ú�ָ��ID������  
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);  
    }  
    public void startScan(){  
        mWifiManager.startScan();  
        //�õ�ɨ����  
        mWifiList=mWifiManager.getScanResults();  
        //�õ����úõ���������  
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();  
    }  
    //�õ������б�  
    public List<ScanResult> getWifiList(){  
        return mWifiList;  
    }  
    //�鿴ɨ����  
    public StringBuffer lookUpScan(){  
        StringBuffer sb=new StringBuffer();  
        for(int i=0;i<mWifiList.size();i++){  
            sb.append("Index_" + new Integer(i + 1).toString() + ":");  
             // ��ScanResult��Ϣת����һ���ַ�����    
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level    
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
    //�õ����ӵ�ID  
    public int getNetWordId(){  
        return mWifiManager.getConnectionInfo().getNetworkId();  
    }  
    //�õ�wifiInfo��������Ϣ  
    public String getWifiInfo(){  
        return mWifiManager.getConnectionInfo().toString();  
    }  
    //�õ������ٶ�
    public int getLinkSpeed(){  
        return mWifiManager.getConnectionInfo().getLinkSpeed();  
    }  
    public int getRssi() {
    	return mWifiManager.getConnectionInfo().getRssi();
	} 
    //���һ�����粢����  
    public boolean addNetWork(WifiConfiguration configuration){  
        int wcgId=mWifiManager.addNetwork(configuration);  
        return mWifiManager.enableNetwork(wcgId, true);  
    }  
    //�Ͽ�ָ��ID������  
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
