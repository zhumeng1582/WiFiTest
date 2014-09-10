package com.example.wifitest;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WiFiTest extends Activity {

	private EditText eTSSID;
	private EditText eTPassword;
	private EditText eTMinValue;
	private CheckBox checkBox;
	private WifiAdmin mWifiAdmin; 
	private WifiConfiguration wifiConfi;

	private AudioManager mAudioManager;
	
	private ProgressBar progressBar;
	private TextView tVConnectState;
	private TextView tVPingState;
	private ImageView iVPass;
	private Vibrator vibrator;
	private long [] pattern1 = {100,400,100,400};   // 停止 开启 停止 开启 
	private long [] pattern2 = {100,200,100,200};   // 停止 开启 停止 开启 
	
	private enum SearchState {close,search,badly,well};//断开，搜索，信号糟，信号良好
	private SearchState isSearchState= SearchState.close;
	//private final ReentrantLock lock = new ReentrantLock();
	
	private Button btnScan;
	private Button btnStop;
	private Timer  getWiFiRssiTimer;
	private Timer  searchWiFiTimer;
	private Intent service;
	private Uri notification;
	private Ringtone r;
	private int times = 0; 
	//private boolean isMeetCondition = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.wifitest);
		
		service = new Intent(WiFiTest.this,MediaPlayerService.class);
		notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);  
		
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		mWifiAdmin = new WifiAdmin(WiFiTest.this);
		
		progressBar = (ProgressBar)findViewById(R.id.progressBar3);
		progressBar.setVisibility(View.GONE);
		
		SharedPreferences sharedata = getSharedPreferences("data", 0); 
		String data;
		
		eTSSID = (EditText)findViewById(R.id.eTSSID);
		data = sharedata.getString("ssid", null);
		eTSSID.setText(data);
		
		eTPassword = (EditText)findViewById(R.id.eTPassword);
		data = sharedata.getString("wifiPassword", null);
		eTPassword.setText(data);
		
		eTMinValue = (EditText)findViewById(R.id.eTMinValue);
		data = sharedata.getString("minValueStr", null);
		eTMinValue.setText(data);
		
		tVConnectState = (TextView)findViewById(R.id.tVConnectState);
		tVPingState = (TextView)findViewById(R.id.tVPingState);
		
		iVPass = (ImageView)findViewById(R.id.iVPass);
		
		btnScan = (Button)findViewById(R.id.btnScan);
		
		checkBox = (CheckBox)findViewById(R.id.cBRemberPara);
		
		btnScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(isSearchState != SearchState.close){
					Toast.makeText(WiFiTest.this, "WiFi已经打开", Toast.LENGTH_SHORT).show();
					return;
				}

				String ssid = eTSSID.getText().toString();
				if(ssid.isEmpty()){
					Toast.makeText(WiFiTest.this, "SSID 不能为空!", Toast.LENGTH_SHORT).show();
					progressBar.setVisibility(View.GONE);
					return;
				}
		        String password = eTPassword.getText().toString();
		        if(password.isEmpty()){
					Toast.makeText(WiFiTest.this, "密码 不能为空!", Toast.LENGTH_SHORT).show();
					progressBar.setVisibility(View.GONE);
					return;
		        }
		        
		        String minValueStr = eTMinValue.getText().toString(); 
		        
		        int minValue = 0;
		        try{
		        	minValue = Integer.parseInt(minValueStr);
		        }catch( NumberFormatException e){
		        	Toast.makeText(WiFiTest.this, e.toString(), Toast.LENGTH_SHORT).show();
		        	progressBar.setVisibility(View.GONE);
					return;
		        }
		       if( mWifiAdmin.checkState()!=3){
		    	   Toast.makeText(WiFiTest.this, "WiFi状态不可用,请打开WiFi", Toast.LENGTH_SHORT).show();
		    	   progressBar.setVisibility(View.GONE);
	        		return;
		       }
		       
		       
		       progressBar.setVisibility(View.VISIBLE);
		       ScanOpened();
		       
		       wifiConfi = mWifiAdmin.CreateWifiInfo(ssid, password, 3);
		       
		       searchWiFiTimer = new Timer();
		       searchWiFiTimer.schedule(new searchWiFi(ssid,password,minValue), 0, 4000);
		    		  
		       getWiFiRssiTimer = new Timer();
		       getWiFiRssiTimer.schedule(new getWiFiRssi(ssid, minValue), 0, 2000);
		        
	        }
		});
		
		btnStop = (Button)findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				ScanClosed();
				if(searchWiFiTimer != null)
					searchWiFiTimer.cancel();
				if(getWiFiRssiTimer != null)
					getWiFiRssiTimer.cancel();
				
				isSearchState = SearchState.close;
			}
		});
	}
	private void ScanOpened(){
		eTSSID.setEnabled(false);
        eTPassword.setEnabled(false);
        eTMinValue.setEnabled(false);
        btnScan.setEnabled(false);
        btnStop.setEnabled(true);
	}
	private void ScanClosed(){
		progressBar.setVisibility(View.GONE);
		eTSSID.setEnabled(true);
        eTPassword.setEnabled(true);
        eTMinValue.setEnabled(true);
        btnScan.setEnabled(true);
        btnStop.setEnabled(false);
        tVConnectState.setText("");
        tVPingState.setText("");
        iVPass.setImageBitmap(null);
	}
	private MessageHandler mHandler = new MessageHandler();
	private static final int CLOSE =0;
	private static final int CONNECT =1;
	private static final int BADLY =2;
	private static final int WELL =3;
	private static final int OVERTIME =4;
	//SearchState {close,search,badly,well}

	class MessageHandler extends Handler {
		
		public MessageHandler() {      
        }  
		public MessageHandler(Looper looper) {   
			super(looper);   
        }   
  
        @Override   
        public void handleMessage(Message msg) {
        	switch(msg.what){
        	case CLOSE:
        	{
        		iVPass.setImageBitmap(null);
        		tVPingState.setText("");
        		progressBar.setVisibility(View.VISIBLE);
        		tVConnectState.setText((String)msg.obj);
        		break;
        	}
        	case CONNECT:
        	{
        		iVPass.setImageResource(R.drawable.pass);
        		tVConnectState.setText((String)msg.obj);
        		progressBar.setVisibility(View.GONE);
        		
        		Runtime run = Runtime.getRuntime();
        		Process proc = null; 
        		String str = "ping -c 1 -i 0.2 -W 1 "+ "www.baidu.com"; 
        		try {
					proc = run.exec(str);
					int result = proc.waitFor(); 
	        		if(result == 0) {
	        			times++;
	        			WiFiTest.this.setTitle("WiFi连接成功"+times+"次");
	        			tVPingState.setText("Ping www.baidu.com 成功");
	        		}
	        		else{
	        			tVPingState.setText("Ping www.baidu.com 失败");
	        		}
				} catch (IOException | InterruptedException e) {
					tVPingState.setText("Ping www.baidu.com 失败");
				} finally{
        			proc.destroy(); 
        		}
        		
        		break;
        	}
        	case WELL:
        	{
        		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        		r.stop();
    			if(current==0)
        			vibrator.vibrate(pattern2,-1); 
        		else
                    r.play();
		        break;
        	}

        	case BADLY:   
        	{
        		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

    			WiFiTest.this.stopService(service);    //关闭
        		if(current==0)
        			vibrator.vibrate(pattern1,-1); 
        		else{	
        			WiFiTest.this.startService(service);    //开启服务
        		}
		        break;
        	}
        	case OVERTIME:
        	{
        		tVConnectState.setText("连接超时");
        		tVPingState.setText("");
        		break;
        	}
        	
        	default:break;
        	} 
        }
	}   
	class searchWiFi extends TimerTask{

		String ssid,password;
		int minValue;
		searchWiFi(String ssid,String password,int minValue){
			this.ssid = ssid;
			this.password = password;
			this.minValue = minValue;
		}
		
		@Override
		public void run() {
			
			if(isSearchState != SearchState.close)
				return;
			if(isExits(ssid) ==null)
				return;

	        if(mWifiAdmin.addNetWork(wifiConfi)){
	        	int strength = mWifiAdmin.getRssi();
	        	int repeate=0;
	    		while((strength<-100)){//正在搜索
	    			strength = mWifiAdmin.getRssi();
	    			try {
						Thread.sleep(500);
						if(repeate++>6){
							Message msg = new Message();
							msg.what = OVERTIME;
							mHandler.sendMessage(msg);

							this.cancel();
							if(getWiFiRssiTimer != null)
								getWiFiRssiTimer.cancel();
							
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
				isSearchState = SearchState.search;
	        	
	        }else{
        		Toast.makeText(WiFiTest.this, "连接wifi信号失败:"+ssid, Toast.LENGTH_SHORT).show();
        	}
		}
	}
	class getWiFiRssi extends TimerTask {
		int minValue;
		String ssid;
		getWiFiRssi(String ssid,int minValue){
			this.ssid = ssid;
			this.minValue = minValue;
		}
        public void run() {
        	if(isSearchState == SearchState.close)
        		return;

    		int strength = mWifiAdmin.getRssi();
    		Message msg = new Message();
    		if((strength<-100)){//正在搜索
    			
    			isSearchState = SearchState.close;
    			msg.what = CLOSE;
    			msg.obj="已经断开";
				mHandler.sendMessage(msg); 
    			return;
    		}
    		msg = new Message();
    		msg.what = CONNECT;
    		msg.obj = "信号强度:"+strength;
			mHandler.sendMessage(msg);
			
    		

        	if(strength > minValue){		//每次满足条件都报警
        		isSearchState = SearchState.well;
        		msg = new Message();
				msg.what = WELL; 
				mHandler.sendMessage(msg);
        	}else{
        		if(isSearchState != SearchState.badly){//第一次不满足条件时报警
        			msg = new Message();
    				msg.what = BADLY; 
    				mHandler.sendMessage(msg);
        		}
        		isSearchState = SearchState.badly;
        		
        	}
        	
			
        }
    }

	public ScanResult isExits(String ssid){
		
		mWifiAdmin.startScan();  
		List<ScanResult> list=mWifiAdmin.getWifiList(); 
		if(list!=null){  
            for(int i=0;i<list.size();i++){  
                ScanResult mScanResult = list.get(i);  
                if(ssid.equals(mScanResult.SSID)){
                	return mScanResult;
                }
            }   
        }
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            isExit.setTitle("系统提示");  
            isExit.setMessage("确定要退出吗");  
            isExit.setButton("确定", listener);  
            isExit.setButton2("取消", listener);  
            isExit.show();  
        }  
        return false;  
    }  
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
                finish();  
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
                break;  
            default:  
                break;  
            }  
        }  
    };   
    
    public void onStop(){  
        super.onStop();  
        SharedPreferences sharedata = getSharedPreferences("data", 0); 
		SharedPreferences.Editor shareDataEdit = sharedata.edit();  
		
		shareDataEdit.putString("times",String.valueOf(times+1));
		String ssid,wifiPassword,minValueStr;
		ssid = eTSSID.getText().toString();
		wifiPassword = eTPassword.getText().toString();
		minValueStr = eTMinValue.getText().toString();
		if(checkBox.isChecked()==true){
			shareDataEdit.putString("ssid",ssid);
			shareDataEdit.putString("wifiPassword",wifiPassword);
			shareDataEdit.putString("minValueStr",minValueStr);
			
		}else{
			shareDataEdit.putString("ssid",null);
			shareDataEdit.putString("wifiPassword",null);
			shareDataEdit.putString("minValueStr",null);
		}
		
		shareDataEdit.commit();
        vibrator.cancel();  
    } 

}
