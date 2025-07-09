package com.rfid.hf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScanMode extends Activity implements OnClickListener, OnItemClickListener{
	
	private String mode;
	private Map<String,Integer> data;
	ImageButton btclear;
	ImageButton btStart;
	ImageButton btStop;
	ListView listView;
	TextView txNum;
	TextView txTime;
	TextView txCount;
	TextView txinfo;
	private Timer timer;
	private Handler mHandler;
	private boolean isCanceled = true;
	private boolean isReading = false;
	private boolean isStopping = false;

	private static final int SCAN_INTERVAL = 10;
	private static final int MSG_UPDATE_LISTVIEW = 0;
	private static final int MSG_UPDATE_BUTTON = 2;
	private static final int MSG_UPDATE_INFO=1;
	private static final int MSG_UPDATE_CLEAR=3;
	private static final int MSG_UPDATE_ADD=4;
	private static boolean Scanflag=false;
	
	public static ArrayList<HashMap<String, String>> mCurIvtClist;
	public static ArrayList<HashMap<String, String>> mnewIvtClist;
	SimpleAdapter adapterTaglist;
	
	Thread mThread;
	volatile boolean mWorking=false;
	long Count=0;
	long Number=0;
	long ScanTime =0;
	private static SoundPool soundPool=null;
	private static  Integer soundid=null;
	public static void setsoundid(int id, SoundPool soundPoola)
	{
		soundid =id;
		soundPool = soundPoola;
	}
	public  void playSound() {
		if((soundid==null)||(soundPool==null))return;
		try {
			soundPool.play(soundid, 1, // 左声道音量
					1, // 右声道音量
					1, // 优先级，0为最低
					0, // 循环次数，0无不循环，-1无永远循环
					1  // 回放速度 ，该值在0.5-2.0之间，1为正常速度
			);
			//SystemClock.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try
		{
			setContentView(R.layout.query);	
			mCurIvtClist = new ArrayList<HashMap<String, String>>();
			mnewIvtClist = new ArrayList<HashMap<String, String>>();
			adapterTaglist = new SimpleAdapter(
					this,
					mCurIvtClist,
					R.layout.listtag_items,
					new String[] { "tagid", "tagUid", "tagDecode" },
					new int[] { R.id.tv_id, R.id.tv_Uid, R.id.tv_decode }
			);

			txNum = (TextView)findViewById(R.id.textNumber);
			txTime = (TextView)findViewById(R.id.textTime);
			txCount = (TextView)findViewById(R.id.textCount);

			btStart = (ImageButton)findViewById(R.id.btn_startInventory);
			btStart.setOnClickListener(this);
			
			btStop = (ImageButton)findViewById(R.id.btn_stopInventory);
			btStop.setOnClickListener(this);

			btclear = (ImageButton)findViewById(R.id.btn_clearList);
			btclear.setOnClickListener(this);

			listView = (ListView)findViewById(R.id.list_inventory_record);
			//listView.setOnItemClickListener(this);
			
			
			mHandler = new Handler(){

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					//
					switch (msg.what) {
						case MSG_UPDATE_LISTVIEW:
							txNum.setText("0");
							txTime.setText("0");
							txCount.setText("0");
							mCurIvtClist.clear();
							listView.setAdapter(adapterTaglist);
							adapterTaglist.notifyDataSetChanged();
						break;
						case MSG_UPDATE_BUTTON:
							btStart.setEnabled(true);
							btStop.setEnabled(false);
							break;
						case MSG_UPDATE_CLEAR:
							mCurIvtClist.clear();;
							break;
						case MSG_UPDATE_ADD:
							if (isCanceled) return;

							String temp = msg.obj + "";
							String[] uidtemp = temp.split(",");
							Log.d("RFID_UID", "Scanned UID: " + uidtemp[1]);

							HashMap<String, String> temps = new HashMap<String, String>();
							temps.put("tagUid", uidtemp[1]);
							temps.put("tagDecode", "Loading...");

							int index = checkIsExist(uidtemp[1], mCurIvtClist);
							if (index == -1) {
								temps.put("tagid", (mCurIvtClist.size() + 1) + "");
								mCurIvtClist.add(temps);
								Number = mCurIvtClist.size();

								// 🔗 Call API setelah ditambahkan
								LibraryApiHelper.getBookByUID(Util.privateKey, uidtemp[1], new LibraryApiHelper.BookCallback() {
									@Override
									public void onSuccess(final String title, final boolean rentable) {
										if (!ScanMode.this.isFinishing()) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													temps.put("tagDecode", title);
													adapterTaglist.notifyDataSetChanged();
												}
											});
										}
									}

									@Override
									public void onFailure(final String message) {
										if (!ScanMode.this.isFinishing()) {
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													temps.put("tagDecode", "Tidak ditemukan");
													adapterTaglist.notifyDataSetChanged();
												}
											});
										}
									}
								});

							}

							listView.setAdapter(adapterTaglist);
							adapterTaglist.notifyDataSetChanged();
							txCount.setText(String.valueOf(Count));

							if (ScanTime != 0) {
								txTime.setText(String.valueOf(ScanTime));
								txNum.setText(String.valueOf(Number));
							}
							break;
						default:
						break;
					}
					super.handleMessage(msg);
				}
				
			};
		}
		catch(Exception e)
		{
			
		}
	}

	private String decodeUID(String uid) {
		Map<String, String> map = new HashMap<>();
		map.put("E2 04 93 7B", "Barang A");
		map.put("45 22 AA 1F", "Barang B");
		map.put("11 22 33 44", "Barang C");

		String value = map.get(uid.toUpperCase());
		return (value != null) ? value : "Tidak dikenal";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isReading = false;
		isStopping = false;
		btStart.setEnabled(true);
		btStop.setEnabled(false);
		btStart.setImageResource(R.drawable.btn_before_start_inventory);
		btStop.setImageResource(R.drawable.btn_before_stop_inventory);
		btclear.setImageResource(R.drawable.btn_before_clear);
	}


	private void StartRead()
	{
		if(mThread==null)
		{
			isCanceled =false;
			btStart.setEnabled(false);
			btStop.setEnabled(true);
			mWorking=true;

			mThread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try{
						while(mWorking)
						{
							long beginTime = System.currentTimeMillis();
							mnewIvtClist.clear();
							InventoryTag();
							ScanTime=0;
							Count++;
							if(mnewIvtClist.size()>0)
							{
								playSound();
								for (int i = 0; i < mnewIvtClist.size(); i++) {
									HashMap<String, String> temp = new HashMap<String, String>();
									temp = mnewIvtClist.get(i);
									String data = temp.get("tagid")+","+temp.get("tagUid");
									AddUID(data);

								}
							}
							ScanTime = System.currentTimeMillis() - beginTime;

						}
						mThread=null;
						mHandler.removeMessages(MSG_UPDATE_BUTTON);
						mHandler.sendEmptyMessage(MSG_UPDATE_BUTTON);
					}catch(Exception ex)
					{
						mThread=null;
						mWorking=false;
					}
					SystemClock.sleep(100);
				}
			});
			mThread.start();
		}
	}
	private void StopRead()
	{
		isCanceled = true;
		mWorking =false;
		btStart.setImageResource(R.drawable.btn_before_start_inventory);
	}

	@Override
	public void onClick(View arg0) {
		if(arg0==btStart) {
			StartRead();
			isReading = true;
			isStopping = false;
			btStart.setImageResource(R.drawable.btn_after_start_inventory);
			btStop.setImageResource(R.drawable.btn_before_stop_inventory);
		} else if(arg0==btStop) {
			StopRead();
			isReading = false;
			isStopping = true;
			btStart.setImageResource(R.drawable.btn_before_start_inventory);
			btStop.setImageResource(R.drawable.btn_after_stop_inventory);
		} else if(arg0==btclear) {
			Count=0;
			Number=0;
			ScanTime=0;
			mHandler.removeMessages(MSG_UPDATE_LISTVIEW);
			mHandler.sendEmptyMessage(MSG_UPDATE_LISTVIEW);

			btclear.setImageResource(R.drawable.btn_after_clear);
			btStart.setImageResource(R.drawable.btn_before_start_inventory);
			btStop.setImageResource(R.drawable.btn_before_stop_inventory);

			btclear.postDelayed(() -> btclear.setImageResource(R.drawable.btn_before_clear), 1000);

			isReading = false;
			isStopping = false;
		}
	}

	private void AddUID(String UIDINfo)
	{
		if((UIDINfo==null)||(UIDINfo.length()==0))return;
		Message message = mHandler.obtainMessage();
		message.what = MSG_UPDATE_ADD;
		message.obj = UIDINfo;
		mHandler.sendMessage(message);
	}
	
	private void InventoryTag()
	{
		byte state = (byte)0x06;
		int fCmdRet=0;
		do{
			byte[]UID= new byte[25600];
			int[]CardNum = new int[1];
			CardNum[0]=0;
			fCmdRet = HfData.reader.Inventory(state, UID, CardNum);
			if(CardNum[0]>0)
			{
				for(int m=0;m<CardNum[0];m++)
				{
					byte[]daw = new byte[9];
					System.arraycopy(UID, m*9, daw, 0, 9);
					String uidStr = Util.bytesToHexString(daw, 1, 9);

					HashMap<String, String> temp = new HashMap<String, String>();
					temp.put("tagid", mnewIvtClist.size()+1+"");
					temp.put("tagUid", uidStr);


					int index = checkIsExist(uidStr,mnewIvtClist);
					if(index==-1)//不存在
					{
						mnewIvtClist.add(temp);
					}
				}
			}
			state = (byte)0x02;
		}while(fCmdRet!=0x0E);
	}
	
	public boolean isEmpty(String strEPC)
	{
		return strEPC==null || strEPC.length()==0;
	}
	public int checkIsExist(String strUID,ArrayList<HashMap<String, String>> mList){
        int existFlag = -1;
        if (isEmpty(strUID)) {
            return existFlag;
        }
        if(mList==null)
        {
        	return existFlag;
        }
        String tempStr = "";
        for (int i = 0; i < mList.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = mList.get(i);
            tempStr = temp.get("tagUid");
            if (strUID.equals(tempStr)) {
                existFlag = i;
                break;
            }
        }
        return existFlag;
    }
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mWorking =false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == 523)
		{
			if( event.getAction() == KeyEvent.ACTION_DOWN && !keyPress)
			{
				keyPress = true;
				if(mThread==null)
				{
					StartRead();
				}
				else
				{
					StopRead();
				}
			}
			else if(event.getAction() == KeyEvent.ACTION_UP)
			{
				keyPress = false;
			}
		}
		else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.dispatchKeyEvent(event);
	}
	public boolean keyPress =false;
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==523 && !keyPress)
		{
			keyPress = true;
			if(mThread==null)
			{
				onClick(btStart);
			}
			else
			{
				onClick(btStop);
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==523)
		{
			keyPress =false;
		}
		return super.onKeyUp(keyCode, event);
	}*/
}
