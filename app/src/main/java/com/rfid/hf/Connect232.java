package com.rfid.hf;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
public class Connect232 extends AppCompatActivity {

	private static final String TAG = "COONECTRS232";
	private static final boolean DEBUG = true;
	private ImageButton mConectButton;

    private String strIP;
    private String strPort;
    
    Spinner spType;
	Spinner spCom;
	Spinner spBaud;

	String[]devname = new String[1];
	String[]BaudRate = new String[1];
	String devicePath="/dev/ttyHSL0";
	int speed =19200;
	int mType=0;
	Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        initSound();
		mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		this.registerReceiver(mVirtualKeyListenerBroadcastReceiver, intentFilter);
		//intent = new Intent(this,MyService.class);
		//startService(intent);
		setContentView(R.layout.activity_connect232);

		// Tombol back
		ImageButton btnBack = findViewById(R.id.btn_back);
		btnBack.setOnClickListener(v -> {
			Intent intent = new Intent(Connect232.this, MenuActivity.class);
			startActivity(intent);
			finish();
		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		
		spType = (Spinner)findViewById(R.id.comtype_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this,
				R.array.comtype_spinner,
				R.layout.spinner_item
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(adapter); 
		spType.setSelection(0, true);
		spType.setOnItemSelectedListener(new ComOnItemSelectedListener());
		
		devname[0]="/dev/ttyHSL0";
		spCom = (Spinner)findViewById(R.id.rs232_spinner);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.spinner_item, devname);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCom.setAdapter(adapter1); 
		spCom.setSelection(0, true);
		spCom.setOnItemSelectedListener(new ComOnItemSelectedListener());
		
		
		BaudRate[0]="19200";
		spBaud = (Spinner)findViewById(R.id.baud_spinner);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spinner_item, BaudRate);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spBaud.setAdapter(adapter2); 
		spBaud.setSelection(0, true);
		spBaud.setOnItemSelectedListener(new ComOnItemSelectedListener());
		
		mConectButton = (ImageButton) findViewById(R.id.textview_connect);
		mConectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int result=0x30;
					result = HfData.reader.OpenReader(speed, devicePath, mType, 1, null);
					if(result==0){
						Intent intent;
						intent = new Intent().setClass(Connect232.this, MainHFActivity.class);
						startActivity(intent);
					}
					else
					{
						Toast.makeText(
								getApplicationContext(),
								"串口连接失败",
								Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e) 
				{
					Toast.makeText(
							getApplicationContext(),
							"串口连接失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		}
		

		public class ComOnItemSelectedListener implements OnItemSelectedListener{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch(arg0.getId())
				{
				case R.id.comtype_spinner:
					mType = arg2;

					break;
				case R.id.rs232_spinner:
					devicePath = devname[arg2];
					break;
				case R.id.baud_spinner:
					if(arg2==0)
						speed = 19200;
					else if(arg2==1)
						speed = 38400;
					else if(arg2==2)
						speed = 57600;
					else if(arg2==3)
						speed = 115200;
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}
		
		
		
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				
				finish();

				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			this.unregisterReceiver(mVirtualKeyListenerBroadcastReceiver);
			//stopService(intent);
		}

    static HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private static SoundPool soundPool;
    private static float volumnRatio;
    private static AudioManager am;
    private  void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.scan_new, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
        ScanMode.setsoundid(soundMap.get(1),soundPool);
    }


	private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;
	public  static boolean mSwitchFlag = false;
	private class VirtualKeyListenerBroadcastReceiver extends BroadcastReceiver {
		private final String SYSTEM_REASON = "reason";
		private final String SYSTEM_HOME_KEY = "homekey";
		private final String SYSTEM_RECENT_APPS = "recentapps";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String systemReason = intent.getStringExtra(SYSTEM_REASON);
				if (systemReason != null) {
					mSwitchFlag = true;
					if (systemReason.equals(SYSTEM_HOME_KEY)) {
						System.out.println("Press HOME key");
					} else if (systemReason.equals(SYSTEM_RECENT_APPS)) {
						System.out.println("Press RECENT_APPS key");
						//mSwitchFlag = true;
					}

				}
			}
		}
	}

}
