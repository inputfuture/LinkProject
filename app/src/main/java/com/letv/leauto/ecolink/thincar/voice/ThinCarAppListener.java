package com.letv.leauto.ecolink.thincar.voice;

import android.content.Context;

import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.voicehelp.manger.app.LeVoiceAppManager;

/**
 * Created by Administrator on 2017/3/3.
 */

public class ThinCarAppListener extends LeVoiceAppManager.AppListener {
    private Context mContext;

    public ThinCarAppListener(Context context) {
        mContext = context;
    }

    @Override
    public void startApp(String packageName) {
        GlobalCfg.IS_THIRD_APP_STATE = true;
        GlobalCfg.isCarResumed = false;
        DataSendManager.getInstance().notifyCarNaviEvent(ThinCarDefine.ProtocolToCarParam.PHONE_NOTIFY_APP_CURRENT_PAGE,
                ThinCarDefine.PageIndexDefine.THIRAD_APP_PAGE,0);
    }
}
