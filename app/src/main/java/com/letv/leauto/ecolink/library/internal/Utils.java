package com.letv.leauto.ecolink.library.internal;

import com.letv.leauto.ecolink.utils.Trace;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		Trace.Debug(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

}
