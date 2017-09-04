package com.letv.leauto.ecolink.leplayer.mixMedia;

import android.content.Context;
import android.util.Log;

import com.leauto.link.lightcar.pcm.ThincarPlayerPcm;
import com.letv.leauto.ecolink.ui.MainActivity;
import com.letvcloud.cmf.MediaPlayer;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/15.
 */

public class LePlayerPcm {
    public static void lePlayer_ready(MediaPlayer player, Context context){
        Long comm = new Long(0);
        Long comm_arg = new Long(0);
        new ThincarPlayerPcm().thincar_player_ready(comm,comm_arg);
        if(player!=null) {
            player.setComponent(1, comm, comm_arg, 0);
//            //if (MainActivity.isThinCar) {
//                player.setVolume(0,0);
//            //}
        }
    }
}
