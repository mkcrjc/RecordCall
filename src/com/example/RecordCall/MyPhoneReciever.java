
package com.example.RecordCall;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;


public class MyPhoneReciever extends BroadcastReceiver {
	
	public static final String LISTEN_ENABLED = "ListenEnabled";
	public static final String FILE_DIRECTORY = "recordedCalls";
	private String phoneNumber;
	public static final int STATE_INCOMING_NUMBER = 0;
	public static final int STATE_CALL_START = 1;
	public static final int STATE_CALL_END = 2;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		SharedPreferences settings = context.getSharedPreferences(LISTEN_ENABLED, 0);
		boolean silent = settings.getBoolean("silentMode", true);
		phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		
		
		if (silent && MainActivity.updateExternalStorageState() == MainActivity.MEDIA_MOUNTED)
		{
			if (phoneNumber == null)
			{
				if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
				{
					Intent myIntent = new Intent(context, RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_START);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					context.startService(myIntent);
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) 
				{
					Intent myIntent = new Intent(context, RecordService.class);
					myIntent.putExtra("commandType", STATE_CALL_END);
					context.startService(myIntent);
					
					
				}
				else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
				{
					if (phoneNumber == null)
						phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					Intent myIntent = new Intent(context, RecordService.class);
					myIntent.putExtra("commandType", STATE_INCOMING_NUMBER);
					myIntent.putExtra("phoneNumber",  phoneNumber);
					context.startService(myIntent);
					
				}
			}
			else
			{
				Intent myIntent = new Intent(context, RecordService.class);
				myIntent.putExtra("commandType", TelephonyManager.EXTRA_INCOMING_NUMBER);
				myIntent.putExtra("phoneNumber",  phoneNumber);
				context.startService(myIntent);
			}
			
		}
		 
	}
	
	


}