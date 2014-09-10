package com.example.wifitest;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private Button btnLogin;
	private Button btnGetPassword;
	private EditText eTMacAddress;
	private EditText eTPassword;
	private CheckBox cBRemberPassword;
	final byte[] keyBytes = { 0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11
            ,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11,0x11};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.login);
		WifiAdmin mWifiAdmin = new WifiAdmin(this);
		
		eTPassword = (EditText)findViewById(R.id.eTPassword);
		SharedPreferences sharedata = getSharedPreferences("data", 0); 
		String data = sharedata.getString("password", null);
		if(data != null)
			eTPassword.setText(data);
		
		
		eTMacAddress = (EditText)findViewById(R.id.eTMacAddress);
		//eTMacAddress.setEnabled(false);
		if(mWifiAdmin.checkState()!=3){
			Toast.makeText(Login.this, "���ȴ�WiFi", Toast.LENGTH_SHORT).show();
			finish();
		}
	
		eTMacAddress.setText(mWifiAdmin.getMacAddress().toString());
		
		final Des des = new Des();
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String macaddr = eTMacAddress.getText().subSequence(0,4).toString();
				byte[] macAddressByte;
				try {
					macAddressByte = macaddr.getBytes("UTF8");
					byte[] valPassword = des.encryptMode(keyBytes, macAddressByte);
					String password1 = des.byte2hex(valPassword);
					String password2 = eTPassword.getText().toString();
					if(password1.equals(password2)){
						Intent deviceListIntent = new Intent(Login.this, WiFiTest.class);
						startActivity(deviceListIntent);
						finish();
					}else{
						Toast.makeText(Login.this, "�����������������", Toast.LENGTH_SHORT).show();
					}
 					
				} catch (UnsupportedEncodingException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
				
			}
		});
		
		btnGetPassword = (Button)findViewById(R.id.btnGetPassword);
		//btnGetPassword.setVisibility(View.INVISIBLE);
		btnGetPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO �Զ����ɵķ������
				String macaddr = eTMacAddress.getText().subSequence(0,4).toString();
				byte[] macAddressByte;
				try {
					macAddressByte = macaddr.getBytes("UTF8");
					byte[] valPassword = des.encryptMode(keyBytes, macAddressByte);
					
					eTPassword.setText(des.byte2hex(valPassword));
				} catch (UnsupportedEncodingException e) {
					// TODO �Զ����ɵ� catch ��
					e.printStackTrace();
				}
				
			}
		});
		cBRemberPassword = (CheckBox)findViewById(R.id.cBRemberPassword);
	}
	@Override
	protected void onStop() {
		String password2 = eTPassword.getText().toString();
		SharedPreferences sharedata = getSharedPreferences("data", 0); 
		SharedPreferences.Editor shareDataEdit = sharedata.edit(); 
		
		if(cBRemberPassword.isChecked()==true){
			shareDataEdit.putString("password",password2);  
		}else{ 
			shareDataEdit.putString("password",null);  
		}
		
		shareDataEdit.commit();
		
		super.onStop();
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

}
