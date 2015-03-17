package mobitnt.net;


import java.util.Properties;

import mobitnt.android.wrapper.AppApi;
import mobitnt.util.*;


public class AuthManager extends PageGen {

	public String ProcessRequest(String request, Properties parms) {

		
		
		String sAction = parms.getProperty(EADefine.EA_ACTION_TAG,
				EADefine.EA_ACT_REQ_AUTH);
		if (sAction.contains(EADefine.EA_ACT_REQ_AUTH)) {
			return GenRetCode(EADefine.EA_RET_OK);
		}

		return GenRetCode(EADefine.EA_RET_UNKONW_REQ);

	}

	

}
