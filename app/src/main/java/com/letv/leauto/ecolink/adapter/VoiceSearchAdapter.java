package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.VoiceCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.lemap.entity.SearchPoi;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;

/**
 * Created by why on 2016/8/16.
 */
public class VoiceSearchAdapter extends BaseAdapter {
    private ArrayList<SearchPoi> mPoiSearches;
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<Contact> mContacts;
    private ArrayList<MediaDetail> mMusicList;
    private String mType= VoiceCfg.DOMAIN_MUSIC;
    private String mKeyWords;

    public VoiceSearchAdapter(Context context, ArrayList<SearchPoi> poiSearches, String keyWords) {
        mType=VoiceCfg.DOMAIN_MAP;
        mPoiSearches = poiSearches;
        mKeyWords=keyWords;
        mContext=context;
        mKeyWords = keyWords;
        mInflater=LayoutInflater.from(mContext);
    }

    public VoiceSearchAdapter(Context context, ArrayList<Contact> conList, String keyWords, String type) {
        mType=VoiceCfg.DOMAIN_CONTACT;
        mContext=context;
        mContacts =conList;
        mKeyWords = keyWords;
        mInflater=LayoutInflater.from(mContext);
    }
    public VoiceSearchAdapter(Context context, ArrayList<MediaDetail> mediaDetails, String keyWords,boolean type) {
        mType=VoiceCfg.DOMAIN_MUSIC;
        mMusicList=mediaDetails;
        mContext =context;
        mKeyWords = keyWords;
        mInflater=LayoutInflater.from(mContext);
    }
    private int mIndex=-1;
    public void setIndex(int index){
        mIndex=index;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        switch (mType){
            case VoiceCfg.DOMAIN_MUSIC:
                return mMusicList ==null?0: mMusicList.size();
            case VoiceCfg.DOMAIN_CONTACT:
                return mContacts ==null?0: mContacts.size();
            case VoiceCfg.DOMAIN_MAP:
                return mPoiSearches==null?0:mPoiSearches.size();
        }
     return 0;
    }

    @Override
    public Object getItem(int position) {
        switch (mType){
            case VoiceCfg.DOMAIN_MUSIC:
                return mMusicList ==null?null: mMusicList.get(position);
            case VoiceCfg.DOMAIN_CONTACT:
                return mContacts ==null?null: mContacts.get(position);
            case VoiceCfg.DOMAIN_MAP:
                return mPoiSearches==null?null:mPoiSearches.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.voice_item_voice_map,null);
            viewHolder.name= (TextView) convertView.findViewById(R.id.name);
            viewHolder.describe= (TextView) convertView.findViewById(R.id.describe);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }


        switch (mType){
            case VoiceCfg.DOMAIN_MAP:
                if (position<9){
                    viewHolder.name.setText("0"+(position+1)+"."+mPoiSearches.get(position).getAddrname());
                }else{
                    viewHolder.name.setText((position+1)+"."+mPoiSearches.get(position).getAddrname());
                }
                break;
            case VoiceCfg.DOMAIN_MUSIC:
                if (position<9){
                    viewHolder.name.setText("0"+(position+1)+"."+mMusicList.get(position).NAME);
                }else{
                    viewHolder.name.setText((position+1)+"."+mMusicList.get(position).NAME);
                }
                viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.white));

                break;
            case VoiceCfg.DOMAIN_CONTACT:
                if (position<9){
                    viewHolder.name.setText("0"+(position+1)+"."+mContacts.get(position).getName());
                }else{
                    viewHolder.name.setText((position+1)+"."+mContacts.get(position).getName());
                }

                break;
        }

        String text=viewHolder.name.getText().toString();
        int index = -1;
        Trace.Debug("XXX", "--->keyword=" + mKeyWords);
        if (mKeyWords != null) {
            index = text.indexOf(mKeyWords);
            Trace.Debug("XXX", "--->index:" + index + " len:" + mKeyWords.length());
        }
        viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.transparent_60));
        SpannableStringBuilder spannableString=new SpannableStringBuilder(text);
        if (index >= 0) {
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green)), index, index + mKeyWords.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.transparent_60)),0,text.indexOf(".")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

//        if (text.contains("(")&&text.contains(")")){
//            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.transparent_60)),text.indexOf("("),text.indexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        viewHolder.name.setText(spannableString);



//        viewHolder.describe.setText(mPoiSearches.get(position).getDistrict());

        return convertView;
    }


    class ViewHolder{
        TextView name;
        TextView describe;
    }
}
