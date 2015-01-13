package mobitnt.android.wrapper;

import java.util.Date;

import mobitnt.android.data.*;
import mobitnt.util.EADefine;
import mobitnt.util.EAUtil;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CallLog;

public class CallLogApi {
	static public int CALL_FIELD_ID = 0;
	static public int CALL_FIELD_NAME = 1;
	static public int CALL_FIELD_NUMBER = 2;
	static public int CALL_FIELD_DATE = 3;
	static public int CALL_FIELD_DURATION = 4;
	static public int CALL_FIELD_TYPE = 5;
	static public int CALL_FIELD_TIMESTAMP = 6;
	static public int CALL_FIELD_COUNT = 7;

	static public String CALL_TYPE_INCOMING = "InComing";
	static public String CALL_TYPE_MISSED = "Missed";
	static public String CALL_TYPE_OUTGOING = "OutGoing";

	static int m_iCallCount = 0;

	static final int MAX_CALL_LIST_SIZE = 10000;

	static public int GetCallLogCount() {
		if (m_iCallCount > 0) {
			return m_iCallCount;
		}

		ContentResolver cr = EAUtil.GetContentResolver();
		Cursor cur = cr
				.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
		m_iCallCount = cur.getCount();
		cur.close();
		return m_iCallCount;
	}

	static public CallLogItem GetCallLogByIndex(int iIndex) {

		ContentResolver cr = EAUtil.GetContentResolver();
		Cursor cur = cr.query(CallLog.Calls.CONTENT_URI, null, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		if (cur == null || !cur.moveToPosition(iIndex)) {
			return null;
		}

		int iIdIndex = cur.getColumnIndexOrThrow(CallLog.Calls._ID);
		String sID = cur.getString(iIdIndex);
		if (sID == null) {
			sID = "0";
		}

		int iNameIndex = cur.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME);
		String sName = cur.getString(iNameIndex);
		if (sName == null) {
			sName = "";
		}

		int iNumberIndex = cur.getColumnIndexOrThrow(CallLog.Calls.NUMBER);
		String sNumber = cur.getString(iNumberIndex);

		int iDateIndex = cur.getColumnIndexOrThrow(CallLog.Calls.DATE);
		Date dt = new Date();
		long lTimeStamp = cur.getLong(iDateIndex);
		dt.setTime(lTimeStamp);
		String sDate = dt.toLocaleString();
		String sTimeStamp = Long.toString(lTimeStamp);

		int iDurationIndex = cur.getColumnIndexOrThrow(CallLog.Calls.DURATION);
		int iDuration = cur.getInt(iDurationIndex);
		String sDuration = String.valueOf(iDuration);// FormatDuration(iDuration);

		int iTypeIndex = cur.getColumnIndexOrThrow(CallLog.Calls.TYPE);
		int iCallType = cur.getInt(iTypeIndex);

		cur.close();

		String sType = "InComing";
		switch (iCallType) {
		case CallLog.Calls.INCOMING_TYPE: {
			sType = CALL_TYPE_INCOMING;
			break;
		}
		case CallLog.Calls.MISSED_TYPE: {
			sType = CALL_TYPE_MISSED;
			break;
		}
		case CallLog.Calls.OUTGOING_TYPE: {
			sType = CALL_TYPE_OUTGOING;
			break;
		}
		}

		CallLogItem call = new CallLogItem();
		call.sID = sID;
		call.sName = sName;
		call.sNumber = sNumber;
		call.sDate = sDate;
		call.sDuration = sDuration;
		call.sType = sType;
		call.sTimeStamp = sTimeStamp;
		return call;

	}

	static public int DeleteCall(String id) {
		int iCount = EAUtil.GetContentResolver().delete(
				CallLog.Calls.CONTENT_URI, "_id=?", new String[] { id + "" });

		if (iCount < 1) {
			return EADefine.EA_RET_FAILED;
		}

		return EADefine.EA_RET_OK;

	}


}
