package com.example.wifitest;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class Ring {
	private Context mContext;
	public Ring(Context mContext){
		this.mContext = mContext;
	}
	public Ringtone getDefaultRingtone(int type){ 
		return RingtoneManager.getRingtone(mContext, RingtoneManager.getActualDefaultRingtoneUri(mContext, type)); 
	} 

	 

	public Uri getDefaultRingtoneUri(int type){ 
	    return RingtoneManager.getActualDefaultRingtoneUri(mContext, type); 
	} 

	public List<Ringtone> getRingtoneList(int type){ 

	    List<Ringtone> resArr = new ArrayList<Ringtone>(); 
	    RingtoneManager manager = new RingtoneManager(mContext); 
	    manager.setType(type); 
	    Cursor cursor = manager.getCursor(); 
	    int count = cursor.getCount(); 
	    for(int i = 0 ; i < count ; i ++){ 
	        resArr.add(manager.getRingtone(i)); 
	    } 
	    return resArr; 

	} 

	public Ringtone getRingtone(int type,int pos){ 

	    RingtoneManager manager = new RingtoneManager(mContext); 
	    manager.setType(type); 
	    return manager.getRingtone(pos); 

	} 


	public List<String> getRingtoneTitleList(int type){ 

	    List<String> resArr = new ArrayList<String>(); 
	    RingtoneManager manager = new RingtoneManager(mContext); 
	    manager.setType(type); 
	    Cursor cursor = manager.getCursor(); 
	    if(cursor.moveToFirst()){ 
	        do{ 
	            resArr.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)); 
	        }while(cursor.moveToNext()); 
	    } 
	    return resArr; 
	}  

	public String getRingtoneUriPath(int type,int pos,String def){ 

	    RingtoneManager manager = new RingtoneManager(mContext); 
	    manager.setType(type); 
	    Uri uri = manager.getRingtoneUri(pos); 
	    return uri==null?def:uri.toString(); 
	} 

	 

	public Ringtone getRingtoneByUriPath(int type ,String uriPath){ 

	    RingtoneManager manager = new RingtoneManager(mContext); 
	    manager.setType(type); 
	    Uri uri = Uri.parse(uriPath); 
	    return manager.getRingtone(mContext, uri); 
	}   

}
