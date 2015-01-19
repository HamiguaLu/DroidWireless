package mobitnt.android.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mobitnt.android.data.SysEvt;
import mobitnt.util.EADefine;
import mobitnt.util.EAUtil;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;



public class SysApi {
	static List<String> m_BatteryInfo = new ArrayList<String>();
	static boolean m_iBatteryInfoAvailable = false;
	public static int m_iBatteryLevel;

	static public List<String> getSysInfo() {
		String[] sMemInfo = getMemoryInfo();
		String[] sStorageInfo = getStorageInfo();
		String[] sModelInfo = getModelInfo();
		String[] sCpuInfo = getCpuInfo();
		
		List<String> sysInfo = new ArrayList<String>();
			
		sysInfo.add("BatteryLevel:" + String.valueOf(m_iBatteryLevel));
		
		if (sMemInfo != null) {
			for (int i = 0; i < sMemInfo.length; ++i) {
				sysInfo.add(sMemInfo[i]);
			}
		}

		if (sStorageInfo != null) {
			for (int i = 0; i < sStorageInfo.length; ++i) {
				sysInfo.add(sStorageInfo[i]);
			}
		}

		if (sModelInfo != null) {
			for (int i = 0; i < sModelInfo.length; ++i) {
				sysInfo.add(sModelInfo[i]);
			}
		}

		if (sCpuInfo != null) {
			for (int i = 0; i < sCpuInfo.length; ++i) {
				sysInfo.add(sCpuInfo[i]);
			}
		}
		
		return sysInfo;
	}

	
	static public String getImei() {
		return EAUtil.GetPhoneID();
	}
	static String[] getModelInfo() {
		String[] modelInfo = { EADefine.EA_ACT_SYS_PRODUCT_MODEL_TAG + ":"
				+ android.os.Build.MODEL/*
										 * , String.format("SDK version:%s",
										 * android.os.Build.VERSION.SDK),
										 * String.format("Release Version:%s",
										 * android.os.Build.VERSION.RELEASE),
										 */
		};
		// String.format("SDCard total space:%s", sTotalSize) };
		return modelInfo;
	}

	static String[] getStorageInfo() {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		File file = new File(sdcard);
		StatFs statFs = new StatFs(file.getPath());
		
		long bs = statFs.getBlockSize();
		long ab = statFs.getAvailableBlocks();
		long bc = statFs.getBlockCount();
		
		long lAvailableSpace = bs * (ab - 4);
		long lTotalSpace = bc * bs;

		// String sASize = formatSize(lAvailableSpace);
		// String sTotalSize = formatSize(lTotalSpace);

		String[] storageInfo = {
				EADefine.EA_SYS_EXT_AVAILABLE_SPACE + ":"
						+ String.valueOf(lAvailableSpace),
				EADefine.EA_SYS_EXT_TOTAL_SPACE + ":"
						+ String.valueOf(lTotalSpace) };

		return storageInfo;
	}

	static String[] getMemoryInfo() {
		ActivityManager activityManager = (ActivityManager) EAUtil.GetEAContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(outInfo);

		String sAvailableMem = String.valueOf(outInfo.availMem/1024);//formatSize(outInfo.availMem);
		String sTotalRAM = getTotalRAM();
		
		sTotalRAM = sTotalRAM.toUpperCase();
		sTotalRAM = sTotalRAM.replace("KB", "");

		String[] sysInfo = { EADefine.EA_ACT_SYS_AVAIL_RAM + ":" + sAvailableMem ,
				EADefine.EA_ACT_SYS_TOTAL_RAM + ":" + sTotalRAM 	
		};
		return sysInfo;
	}

/*	static public boolean UserLogin(String sUsr, String sPwd) {
		if (sUsr.equals("admin") && sPwd.equals("123")) {
			return true;
		}
		return false;
	}*/

	// android的总内存(ram)大小信息存放在系统的/proc/meminfo文件里面，可以通过读取这个文件来获取这些信息：
	static String getTotalRAM() {
		String sPath = "/proc/meminfo";
		String sItem = "";
		try {
			FileReader fr = new FileReader(sPath);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			while ((sItem = localBufferedReader.readLine()) != null) {
				if (sItem.contains("MemTotal")){
					sItem = sItem.replace("MemTotal:", "");
					localBufferedReader.close();
					return sItem.trim();
				}
			}
			localBufferedReader.close();
		} catch (IOException e) {
		}
		
		return "UNKONW";
	}


	// proc/cpuinfo文件中第一行是CPU的型号，第二行是CPU的频率，可以通过读文件，读取这些数据！
	static String[] getCpuInfo() {
		String sPath = "/proc/cpuinfo";
		String sItem = "";
		String[] cpuInfo = { "", "" };
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(sPath);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			sItem = localBufferedReader.readLine();
			arrayOfString = sItem.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			sItem = localBufferedReader.readLine();
			arrayOfString = sItem.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		
		String[] info = {EADefine.EA_ACT_SYS_CPU_MODEL_TAG + ":"+cpuInfo[0],EADefine.EA_ACT_SYS_CPU_FREQ_TAG + ":"+cpuInfo[1]};
		return info;
	}


	static  List<SysEvt> m_SysEvtlist = new Vector<SysEvt>();
	public static int m_iAuthState;

	static public synchronized void ClearEvtList() {
		m_SysEvtlist.clear();
		PushSysEvt(EADefine.SYS_EVT_BATTERY_LEVEL_CHANGED, String.valueOf(m_iBatteryLevel));
	}

	static public synchronized SysEvt PopSysEvt() {
		if (m_SysEvtlist.size() < 1) {
			SysEvt evt = new SysEvt();
			evt.iEventCode = EADefine.SYS_EVT_NONE;
			return evt;
		}

		SysEvt evt = m_SysEvtlist.get(0);
		m_SysEvtlist.remove(0);

		evt.iHasMore = 0;
		if (m_SysEvtlist.size() > 0) {
			evt.iHasMore = 1;
		}

		return evt;
	}

	static public synchronized void PushSysEvt(int iEvt, String sParameter) {
		SysEvt evt = new SysEvt();
		evt.iEventCode = iEvt;
		evt.sParameter = sParameter;
		if (m_SysEvtlist.size() < 1) {
			m_SysEvtlist.add(evt);
			return;
		}

		if (m_SysEvtlist.size() > 100) {
			ClearEvtList();
			return;
		}

		for (int i = 0; i < m_SysEvtlist.size(); ++i) {
			if (m_SysEvtlist.get(i).iEventCode != iEvt) {
				continue;
			}
			if (m_SysEvtlist.get(i).sParameter.length() < 1) {
				if (sParameter.length() < 1) {
					// already here
					return;
				}
			}

		}

		m_SysEvtlist.add(evt);
	}


}
