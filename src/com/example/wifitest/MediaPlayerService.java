package com.example.wifitest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MediaPlayerService extends Service {
	private MediaPlayer player;
	private boolean isRunning = true;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		player = MediaPlayer.create(getApplicationContext(), R.raw.ring2);
		player.setLooping(false);
		
	}
	@Override
	public void onDestroy() {	//ֹͣ����
		super.onDestroy();
		isRunning = false; 
		if(player!=null){
			player.stop();
			player.release();
			player = null;
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRunning = true;  
		
	    new Thread(new Runnable() {  
	  
	        @Override  
	        public void run() {  
	            if(isRunning) {  
	            	try {
	        			//��ΪMediaPlayer��create�Ѿ�������prepare��������˴˴�ֱ��start��������
	        			player.start();  
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}  
	            }  
	        }  
	       }).start();  
	    
		return super.onStartCommand(intent, flags, startId);
	}
}
