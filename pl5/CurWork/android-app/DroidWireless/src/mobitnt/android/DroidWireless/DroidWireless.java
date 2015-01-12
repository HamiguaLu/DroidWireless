package mobitnt.android.DroidWireless;

import mobitnt.android.wrapper.NetApi;
import mobitnt.net.SrvSock;
import mobitnt.util.EAUtil;
import android.app.Activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DroidWireless extends Activity implements
		android.view.View.OnClickListener {
	static public DroidWireless m_mainApp = null;
	public static final int HELP = Menu.FIRST + 2;
	public static final int RENEW_SECURITY_CODE = Menu.FIRST;
	public static final int ENABLE_SECURITY_CODE = Menu.FIRST + 1;

	static final int EA_UPDATE_MSG = 0;
	public static final String SHOW_SRV_INFO = "mobitnt.android.action.SHOW_SRV_INFO_WD";

	Intent serviceintent = new Intent("mobitnt.net.DROIDWIRELESSSERVICE");/* 用于启动后台服务 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*menu.add(0, HELP, 0, "Help");
		if (EAUtil.IsSecurityEnabled()) {
			menu.add(0, ENABLE_SECURITY_CODE, 0, "Disable Security Code");
			menu.add(0, RENEW_SECURITY_CODE, 0, "Refresh Security Code");
		} else {
			menu.add(0, ENABLE_SECURITY_CODE, 0, "Enable Security Code");
		}*/
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TextView t1 = (TextView) findViewById(R.id.txtCode);
		switch (item.getItemId()) {
		case HELP:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(android.net.Uri
					.parse("http://www.mobitnt.com/sync-outlook-with-android-guide.htm"));
			startActivity(intent);
			break;
		case RENEW_SECURITY_CODE:

	
	
			break;
	
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		EAUtil.SetEAContext(this.getBaseContext());

		TextView tTitle = (TextView) findViewById(R.id.txtTitle);
		TextView tAd = (TextView) findViewById(R.id.txtAd);
		TextView tCode = (TextView) findViewById(R.id.txtCode);
		TextView tDevIP = (TextView) findViewById(R.id.txtDevIP);
		TextView tips = (TextView) findViewById(R.id.txtTips);

		tCode.setText("Visit the URL from browser:");
		String sIP = NetApi.getLocalIpAddress();
		String sUrl = String.format("http://%s:%d", sIP,SrvSock.SERVER_PORT);
		tDevIP.setText(sUrl);

		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(0xffEFEFEF);

		View v = findViewById(R.id.MainScreen);
		v.setOnClickListener(this);

		tTitle.setTextColor(0xff2FBFF4);
		tAd.setTextColor(Color.YELLOW);
		tCode.setTextColor(Color.WHITE);
		tDevIP.setTextColor(0xff2FBFF4);
		tips.setTextColor(Color.WHITE);

		m_mainApp = this;

		IntentFilter filter = new IntentFilter();
		filter.addAction(SHOW_SRV_INFO);
		registerReceiver(m_broadcastReceiver, filter);
	}

	public void onStart() {
		super.onStart();
		startService(serviceintent);

		NetApi.StartReportTimer();
	}

	private BroadcastReceiver m_broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(SHOW_SRV_INFO)) {

				Message msg = new Message();
				msg.what = EA_UPDATE_MSG;
				msg.obj = intent.getExtras().getString("INFO");

				mMsgHandler.sendMessage(msg);
				return;
			}

		}
	};

	public static Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (m_mainApp == null) {
				return;
			}

			switch (msg.what) {
			case EA_UPDATE_MSG: {
				String sInfo = (String) msg.obj;
				TextView t2 = (TextView) m_mainApp.findViewById(R.id.txtDevIP);
				t2.setText(sInfo);
				break;
			}

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {

		NetApi.StartReportTimer();
	}

}
