package com.example.wifitest;



import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private WifiAdmin mWifiAdmin; 
	
	private ListView lvMessage;
	private ArrayAdapter<String> arrayAdapterMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mWifiAdmin = new WifiAdmin(MainActivity.this);
		arrayAdapterMessage = new ArrayAdapter<String>(this, R.layout.device_name);
		lvMessage = (ListView) findViewById(R.id.lvMessage);
		lvMessage.setAdapter(arrayAdapterMessage);
	}
	
	
	@SuppressWarnings("deprecation")
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            isExit.setTitle("ϵͳ��ʾ");  
            isExit.setMessage("ȷ��Ҫ�˳���");  
            isExit.setButton("ȷ��", listener);  
            isExit.setButton2("ȡ��", listener);  
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
            case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����  
                finish();  
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���  
                break;  
            default:  
                break;  
            }  
        }  
    };   
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	if(resultCode == Activity.RESULT_OK){
        		Bundle b = data.getExtras(); 
				WifiConfiguration wifiInfo  = b.getParcelable("wifi_info");
				
				if(wifiInfo == null)
					Toast.makeText(this, "wifi ��Ч", Toast.LENGTH_SHORT).show();
				else if(mWifiAdmin.addNetWork(wifiInfo)){
					arrayAdapterMessage.add(wifiInfo.SSID);
					Toast.makeText(this, "���ӳɹ�:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "����ʧ��:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
				}
        	}
            break;
        default: break;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.Search:
        	Intent deviceListIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.Setting:
            
            return true;
        }
        return false;
    }

}
