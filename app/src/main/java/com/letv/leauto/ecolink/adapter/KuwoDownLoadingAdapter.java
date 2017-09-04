package com.letv.leauto.ecolink.adapter;

import android.content.Context;
import android.nfc.tech.IsoDep;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.manager.MusicDownloadManager;
import com.letv.leauto.ecolink.ui.kuwodownload.CheckInterface;
import com.letv.leauto.ecolink.download.DownloadManager;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by Administrator on 2016/8/11.
 */
public class KuwoDownLoadingAdapter extends BaseAdapter {
    private DownloadManager downloadManager;
    private List<MediaDetail> mNowDownDetails;
    private Context context;
    private int index = -1;
    private List<MediaDetail> downloadingInfos;//未完成下载的列表
    private List<MediaDetail> downloadInfoList;//

    private int mType;

    public KuwoDownLoadingAdapter(Context context, DownloadManager downloadManager, int type) {
        mNowDownDetails = new ArrayList<>();
        this.context = context;
        downloadingInfos = new ArrayList<MediaDetail>();
        this.downloadManager = downloadManager;
        mType = type;
//        initData();
    }

    /**
     * 初始化下载完成/正在下载集合中的数据
     */
    public void initData() {
        downloadingInfos.clear();
//        mIsDelete = false;
        downloadInfoList = downloadManager.getDownloadings();
        for (int i = 0; i < downloadInfoList.size(); i++) {
            MediaDetail downloadInfo = downloadInfoList.get(i);
            if (downloadInfo.DOWNLOAD_FLAG == MediaDetail.State.STATE_FINISH) {
            } else {
                if (mType == 0) {
                    if (downloadInfo.TYPE.equals(SortType.SORT_KUWO_LOCAL)) {
                        downloadingInfos.add(downloadInfo);
                    }
                } else {
                    downloadingInfos.add(downloadInfo);
                }
            }
        }
        if (musicCountInterface != null) {
            musicCountInterface.musicSize(downloadingInfos.size());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return downloadingInfos == null ? 0 : downloadingInfos.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return downloadingInfos.get(position);
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            if (GlobalCfg.IS_POTRAIT) {
                convertView = View.inflate(context, R.layout.kuwo_downloading_item_p, null);
            } else {
                convertView = View.inflate(context, R.layout.kuwo_downloading_item, null);
            }

            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_speed = (TextView) convertView.findViewById(R.id.tv_speed);
            holder.tv_progress = (TextView) convertView.findViewById(R.id.tv_progress);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            holder.iv_deleteBt = (ImageView) convertView.findViewById(R.id.iv_deleteBt);
            holder.tv_download = (TextView) convertView.findViewById(R.id.tv_download);
            holder.iv_select_item = (ImageView) convertView.findViewById(R.id.iv_select_item);
            holder.rl_jindu = (RelativeLayout) convertView.findViewById(R.id.rl_jindu);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.item_local_downloading = (LinearLayout) convertView.findViewById(R.id.item_local_downloading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        if (position < downloadingInfos.size()) {
            //下载中
            final MediaDetail downloadInfo = downloadingInfos.get(position);
//            updateView(holder, downloadInfo);
            setHolderData(holder, downloadInfo, downloadManager);
        } else {
        }
        return convertView;
    }

    public boolean mIsDelete = false;

    public void setDelete(boolean visibily) {
        mIsDelete = visibily;
        notifyDataSetChanged();
    }

    public void setIsAllDownLoad(boolean mIsAll) {
        if (mIsAll) {
            mNowDownDetails.clear();
            mNowDownDetails.addAll(downloadingInfos);
        } else {
            mNowDownDetails.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 为holder设置相关参数
     *
     * @param holder
     * @param downloadInfo
     */
    private void setHolderData(final ViewHolder holder, final MediaDetail downloadInfo, final DownloadManager downloadManager) {

        holder.tv_name.setText(downloadInfo.NAME);
        if (mIsDelete) {
            holder.iv_select_item.setVisibility(View.VISIBLE);
//            holder.tv_progress.setVisibility(View.GONE);
            holder.tv_download.setVisibility(View.GONE);
            holder.iv_deleteBt.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.rl_jindu.setVisibility(View.GONE);
            holder.ll_item.setClickable(false);
            if (mNowDownDetails.contains(downloadInfo)) {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                holder.iv_select_item.setImageResource(R.mipmap.song_selected);
            } else {
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                holder.iv_select_item.setImageResource(R.mipmap.song_not_selected);
            }
            holder.item_local_downloading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNowDownDetails.contains(downloadInfo)) {
                        mNowDownDetails.remove(downloadInfo);
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));
                        holder.iv_select_item.setImageResource(R.mipmap.song_not_selected);
                        Trace.Error("==mnowdowndetails=", "=arrayList.size()=" + mNowDownDetails.size() + "==" + mNowDownDetails.toString() + "mIsDelete=" + mIsDelete);
                    } else {
                        mNowDownDetails.add(downloadInfo);
                        holder.tv_name.setTextColor(context.getResources().getColor(R.color.green_color));
                        holder.iv_select_item.setImageResource(R.mipmap.song_selected);
                        Trace.Error("==mnowdowndetails=", "=arrayList.size()=" + mNowDownDetails.size() + "==" + mNowDownDetails.toString() + "mIsDelete=" + mIsDelete);
                    }
                    if (mNowDownDetails.size() == downloadingInfos.size()) {
                        checkInterface.checkAll();
                    } else {
                        checkInterface.unCheckAll();
                    }
                }
            });
        } else {
            holder.iv_select_item.setVisibility(View.GONE);
//            holder.tv_download.setVisibility(View.VISIBLE);
            holder.iv_deleteBt.setVisibility(View.VISIBLE);
//            holder.tv_progress.setVisibility(View.VISIBLE);
            holder.ll_item.setClickable(true);
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.transparent_60));

            if (downloadInfo.mLength > 0) {
                //根据当前下载大小和APP大小计算出的进度百分比
                int prosress = (int) (((float)downloadInfo.mCurrentlen/ (float) downloadInfo.mLength)*100);
                holder.progressBar.setProgress(prosress);
                holder.tv_progress.setText(TimeUtils.getProgress(downloadInfo.mCurrentlen) + "/" + TimeUtils.getProgress(downloadInfo.mLength));
                holder.tv_speed.setText(downloadInfo.getSpeed() + "/S");

            } else {
                Trace.Error("=========loading=", "ee");
                holder.progressBar.setProgress(0);
                holder.tv_progress.setText("0.0MB" + "/" + "0.0MB");
                holder.tv_speed.setText("0.0B/S");
            }

            final int state = downloadInfo.DOWNLOAD_FLAG;
            switch (state) {
                case MediaDetail.State.STATE_QUEUED:
                    holder.rl_jindu.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.tv_download.setVisibility(View.VISIBLE);
                    holder.tv_download.setText(R.string.wait_download);
                    Trace.Error("==tag=WAITING=", "");
                    break;
                case MediaDetail.State.STATE_DOWNLOADING:
                    holder.rl_jindu.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.tv_download.setVisibility(View.GONE);
                    Trace.Error("==tag=STARTED=", "");
                    break;
                case MediaDetail.State.STATE_PAUSED:
                    holder.rl_jindu.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.tv_download.setVisibility(View.VISIBLE);
                    holder.tv_download.setText(R.string.click_resume_download);
                    Trace.Error("==tag=CANCELLED=", "");
                    break;
                case MediaDetail.State.STATE_FINISH:
                    holder.rl_jindu.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.tv_download.setVisibility(View.GONE);
                    Trace.Error("==tag=SUCCESS=", "");
                    break;
                case MediaDetail.State.STATE_FAILED:
                    holder.rl_jindu.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.tv_download.setVisibility(View.VISIBLE);
                    holder.tv_download.setText(R.string.click_resume_download);
                    Trace.Error("==tag=FAILURE=", "");
                    break;
                default:
                    break;
            }
            //暂停、继续、重试按钮点击事件
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadInfo.DOWNLOAD_FLAG == MediaDetail.State.STATE_QUEUED ||
                            downloadInfo.DOWNLOAD_FLAG == MediaDetail.State.STATE_FAILED ||
                            downloadInfo.DOWNLOAD_FLAG == MediaDetail.State.STATE_PAUSED) {
                        downloadManager.start(downloadInfo);
                        Trace.Error("==tag=state1=", downloadInfo.DOWNLOAD_FLAG + "mIsDelete=" + mIsDelete);
                    } else if (downloadInfo.DOWNLOAD_FLAG == MediaDetail.State.STATE_DOWNLOADING) {
                        downloadManager.stop();
                        Trace.Error("==tag=state2=", downloadInfo.DOWNLOAD_FLAG + "mIsDelete=" + mIsDelete);
                    }
                }
            });
            //删除按钮点击事件
            holder.iv_deleteBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadManager.remove(downloadInfo);
                    Trace.Error("==tag=state3=", downloadInfo.DOWNLOAD_FLAG + "mIsDelete=" + mIsDelete);
                }
            });



        }


    }

    public List<MediaDetail> getNowMedials() {
        return mNowDownDetails;
    }

    private static class ViewHolder {
        public TextView tv_name;//歌曲名称
        public TextView tv_download;//点击继续下载
        public TextView tv_speed;//显示下载速度
        public TextView tv_progress;//显示下进度
        public ProgressBar progressBar;//进度条
        public ImageView iv_deleteBt;//删除按钮
        public ImageView iv_select_item;//选择按钮
        public RelativeLayout rl_jindu;
        public LinearLayout ll_item;
        public LinearLayout item_local_downloading;
    }

    private void updateView(ViewHolder viewHolder, MediaDetail downloadInfo) {
//
//        if (downloadInfo.mLength > 0) {
//            //根据当前下载大小和APP大小计算出的进度百分比
//            int prosress = (int) (downloadInfo.mCurrentlen * 100.00 / downloadInfo.mLength);
//            viewHolder.progressBar.setProgress(prosress);
//            viewHolder.tv_progress.setText(TimeUtils.getProgress(downloadInfo.mCurrentlen) + "/" + TimeUtils.getProgress(downloadInfo.mLength));
//            viewHolder.tv_speed.setText(downloadInfo.getSpeed() + "/S");
//
//        } else {
//            Trace.Error("=========loading=", "ee");
//            viewHolder.progressBar.setProgress(0);
//            viewHolder.tv_speed.setText("0.0B/S");
//        }


    }


    //回调选中的状态
    public CheckInterface checkInterface;

    public void setCheckInterface(CheckInterface argCheckInterface) {
        checkInterface = argCheckInterface;
    }

    //回调选中的状态
    public interface MusicCountInterface {
        public abstract void musicSize(int argSize);
    }

    public MusicCountInterface musicCountInterface;

    public void setMusicCount(MusicCountInterface argMusicCountInterface) {
        musicCountInterface = argMusicCountInterface;
    }

}
