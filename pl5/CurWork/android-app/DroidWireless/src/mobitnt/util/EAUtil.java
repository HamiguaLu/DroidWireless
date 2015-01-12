package  mobitnt.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Random;

import mobitnt.android.wrapper.FileApi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

public class EAUtil {
	static Context eaContext;
	static InetAddress m_ClientIP = null;
	
	public static final int SRV_STATE_WAIT = 0;
	public static final int SRV_STATE_BACKUP_SMS = 1;
	public static final int SRV_STATE_BACKUP_CALL = 2;
	public static final int SRV_STATE_RESOTORE_SMS = 3;
	public static final int SRV_STATE_RESOTORE_CALL = 4;
	static int m_iSrvState = SRV_STATE_WAIT;
	
	public static Context GetEAContext(){
		return eaContext;
	}
	
	public static String GetResString(int id){
		if (eaContext == null){
			return null;
		}
		
		return eaContext.getString(id);
	}
	
	public static void SetEAContext(Context c){
		eaContext = c;
	}

	public static String CHECK_STRING(String s,String DefValue){
		if (s == null || s.length() < 1){
			return DefValue;
		}
		
		return s;
	}

	static public String GetPEFolderOnSDCard() {
		String sPath = FileApi.getExternalStoragePath();
		if (sPath == null || sPath.length() < 2) {
			return "";
		}

		sPath += "/YaSync";
		FileApi.CreateFolder(sPath);

		return sPath;
	}

	static public ContentResolver GetContentResolver() {
		if (eaContext != null) {
			return eaContext.getContentResolver();
		}
		return null;
	}

	static String GenRandomNumber(int iLen) {
		String s = "";
		for (int i = 0; i < iLen; ++i) {
			int r = new Random().nextInt(10);
			s += String.valueOf(r);
		}

		return s;
	}
	
	public static String GetLogReplyMail() {
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			return "";
		}

		return sp.getString("LogReplyMail", "");
	}
	
	public static void SetLogReplyMail(String sMail) {
		Editor sharedata = EAUtil.GetEAContext().getSharedPreferences(EADefine.EA_PREF_NAME, 0)
				.edit();
		sharedata.putString("LogReplyMail", sMail);
		sharedata.commit();
	}
	
	public static void SetLogState(boolean bEnable) {
		Editor sharedata = EAUtil.GetEAContext().getSharedPreferences(EADefine.EA_PREF_NAME, 0)
				.edit();
		sharedata.putBoolean("EnableLog", bEnable);
		sharedata.commit();
	}
	
	static final String m_sBackupHistoryTag = "BKHISTORY:";
	static public void SaveBkHistory(String sInfo){
		String sHistory = sInfo + ";";
		String sOldHistory = GetBkHistory();
		if (sOldHistory.length() > 1){
			String[] sTmp = sOldHistory.split(";");
			if (sTmp.length > 5){
				sOldHistory = "";
				for (int i = 0; i < 5; ++i){
					sOldHistory += sTmp[i];	
				}
			}	
		}
		
		sHistory += sOldHistory;
		
		Editor sharedata = EAUtil.GetEAContext().getSharedPreferences(EADefine.EA_PREF_NAME, 0)
				.edit();
		sharedata.putString(m_sBackupHistoryTag, sHistory);
		sharedata.commit();
		
	}
	static public String GetBkHistory(){
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			return "";
		}

		return sp.getString(m_sBackupHistoryTag, "");
	}
	
	public static void SetBkSyncTime(long lTime){
		Editor sharedata = EAUtil.GetEAContext().getSharedPreferences(EADefine.EA_PREF_NAME, 0)
				.edit();
		sharedata.putLong("BkSyncTime", lTime);
		
		sharedata.commit();
	}
	
	public static long GetBkSyncTime(){
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			return 0;
		}

		return sp.getLong("BkSyncTime", 0);
	}
	
	public static void SetBkOption(String sAccount, String sPwd,String sBkFreq,String sBkTime){
		Editor sharedata = EAUtil.GetEAContext().getSharedPreferences(EADefine.EA_PREF_NAME, 0)
				.edit();
		sharedata.putString("BkMailAccount", sAccount);
		sharedata.putString("BkMailPwd", sPwd);
		sharedata.putString("BkFreq", sBkFreq);
		sharedata.putString("BkTime", sBkTime);
		sharedata.commit();
	}
	
	public static String GetBkOption(){
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			return "";
		}

		String sOptions = sp.getString("BkMailAccount", "");
		sOptions += "," + URLEncoder.encode(sp.getString("BkMailPwd", ""));
		sOptions += "," + sp.getString("BkFreq", "");
		sOptions += "," + sp.getString("BkTime", "");
		
		return sOptions;
	}
		
	public static boolean GetLogState() {
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			return false;
		}

		return sp.getBoolean("EnableLog", false);
	}
	
	
	public static void SetServiceState(int iState){
		m_iSrvState = iState;
	}
	
	public static int GetServiceState(){
		return m_iSrvState;
	}
	
	
	static public String GetPhoneID() {
		SharedPreferences sp = EAUtil.GetEAContext().getSharedPreferences(
				EADefine.EA_PREF_NAME, 0);
		if (sp == null) {
			MobiTNTLog.write("GetPhoneID: can not open pref");
			return "unknowid";
		}

		String s = sp.getString("PhoneID", "");
		if (s.length() > 0) {
			return s;
		}
		
		String sCode = GenRandomNumber(10);

		Editor sharedata = sp.edit();
		sharedata.putString("PhoneID", sCode);
		sharedata.commit();

		return sCode;
	}


	static public String EncodeItem(String s) {
		String sVal = EAUtil.CHECK_STRING(s, " ");

		try {
			sVal = URLEncoder.encode(sVal, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sVal;
	}

	static public String Cursor2Xml(Cursor cur) {
		if (cur == null) {
			return "";
		}

		StringBuilder sXml = new StringBuilder("");
		String[] colNames = cur.getColumnNames();
		for (int i = 0; i < colNames.length; ++i) {
			String sCol = colNames[i];
			sCol = sCol.trim();
			if (sCol.length() < 1){
				continue;
			}
			
			if (sCol.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "")
					.length() != 0) {
				continue;
			}
			
			if (sCol.contains("sync_") || sCol.contains("_sync")) {
				continue;
			}
			
			String sVal = "";
			try{
				sVal = cur.getString(i);
			}catch (Exception e){
				sVal = "";
			}
			
			if (sVal == null || sVal.length() < 1) {
				continue;
			}

			sVal = sVal.trim();
			if (sVal.length() > 0) {
				sXml.append("<" + sCol + ">");
				sXml.append(EAUtil.EncodeItem(cur.getString(i)));
				sXml.append("</" + sCol + ">");
			}
		}

		return sXml.toString();
	}
	
	// 判断是否已经连接到Internet
	public static boolean IsInternetReachable() {
		try {
			InetAddress.getByName("www.google.com").isReachable(3);
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}
