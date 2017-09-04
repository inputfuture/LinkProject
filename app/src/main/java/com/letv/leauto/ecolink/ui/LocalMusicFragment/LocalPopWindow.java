package com.letv.leauto.ecolink.ui.LocalMusicFragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.letv.leauto.ecolink.R;

/**
 * Created by Administrator on 2016/8/8.
 */
public class LocalPopWindow extends PopupWindow{

    private final LayoutInflater inflate;
    private  Context context;
    private int width;
    private ListView listview;

    public LocalPopWindow (Context context,int width){
        super();
        this.context=context;
        this.width=width;
        inflate=LayoutInflater.from(context);
        initViews();

    }

    private void initViews() {
        View view=inflate.inflate(R.layout.local_select_listview,null);
       listview=(ListView) view.findViewById(R.id.listview_pop);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(view);
        setWidth(width);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
        this.setBackgroundDrawable(cd);
        setFocusable(true);
        setOutsideTouchable(true);
    }


    public ListView getListView(){
        return  listview;
    }


}
