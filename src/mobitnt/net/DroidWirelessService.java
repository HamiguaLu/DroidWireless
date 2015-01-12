package mobitnt.net;

import java.io.IOException;

import mobitnt.android.wrapper.AppApi;
import mobitnt.android.wrapper.CallLogApi;
import mobitnt.android.wrapper.FileApi;
import mobitnt.android.wrapper.SmsApi;
import mobitnt.android.wrapper.SysApi;
import mobitnt.backup.BackupOption;
import mobitnt.util.*;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class DroidWirelessService extends Service {
	SrvSock m_SrvSock;
	static DroidWirelessService myService = null;

	public static final int SRV_OP_INIT_ALL_CACHE = 0;
	public static final int SRV_OP_INIT_SMS_CACHE = 1;
	public static final int SRV_OP_INIT_CALL_CACHE = 2;
	public static final int SRV_OP_INIT_FILE_CACHE = 3;
	public static final int SRV_OP_INIT_CONTACT_CACHE = 4;
	public static final int SRV_OP_INIT_APP_CACHE = 5;

	public DroidWirelessService() {

	}


	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		return false;
	}

	

	public static void ReportBatteryState(String sState) {
		
	}


	private static BroadcastReceiver mBroadcastReceiver = new DroidWirelessReceiver();

	public static final String smsAction = "android.provider.Telephony.SMS_RECEIVED";
	public static final String callAction = "android.intent.action.PHONE_STATE";

	void InstallReceiver() {
		IntentFilter filter = new IntentFilter();

		filter.addAction(smsAction);
		filter.addAction(callAction);

		filter.addAction(Intent.ACTION_UMS_CONNECTED);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

		registerReceiver(mBroadcastReceiver, filter);

	}

	@Override
	public void onCreate() {
		super.onCreate();

		myService = this;
		EAUtil.SetEAContext(this.getBaseContext());

		Log.i("Service", "onCreatePESRV");

		MobiTNTLog.m_bEnableLog = EAUtil.GetLogState();

		MobiTNTLog.write("Wait2Init");

		InstallReceiver();
		
		BackupOption.Load();

		// MobiTNTLog.SendLog();

		try {
			m_SrvSock = new SrvSock();
			m_SrvSock.htmlData = getResources().getAssets();
			MobiTNTLog.write("Waiting4Req");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("Service", "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		super.onStart(intent, startid);
		Log.i("Service", "onStartPESRV");
	}

	void LogOP(int iOP) {
		if (iOP == 0) {
			MobiTNTLog.SendLog();
			return;
		}

		boolean bLogState = EAUtil.GetLogState();
		if (!bLogState) {
			EAUtil.SetLogState(true);
			MobiTNTLog.m_bEnableLog = true;
			return;
		}

		EAUtil.SetLogState(false);
		
		MobiTNTLog.m_bEnableLog = false;

		return;

	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


}
