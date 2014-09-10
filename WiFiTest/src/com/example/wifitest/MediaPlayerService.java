package com.example.wifitest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MediaPlayerService extends Service {
	private MediaPlayer player;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		player = MediaPlayer.create(getApplicationContext(), R.raw.ring2);
		player.setLooping(false);
		try {
			//��ΪMediaPlayer��create�Ѿ�������prepare��������˴˴�ֱ��start��������
			player.start();  
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	@Override
	public void onDestroy() {	//ֹͣ����
		super.onDestroy();
		if(player!=null){
			player.stop();
			player.release();
			player = null;
		}
	}
}
