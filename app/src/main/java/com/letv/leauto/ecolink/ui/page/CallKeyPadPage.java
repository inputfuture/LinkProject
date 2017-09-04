package com.letv.leauto.ecolink.ui.page;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.adapter.CallLogAdapter;
import com.letv.leauto.ecolink.adapter.SearchAdapter;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.manager.ContactManager;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.utils.FirstLetterUtil;
import com.letv.leauto.ecolink.utils.PinYinUtil;
import com.letv.leauto.ecolink.utils.ToastUtil;
import com.letv.leauto.ecolink.utils.Trace;
import com.letv.voicehelp.eventbus.EventBusHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lonng on 15/12/8.
 */
public class CallKeyPadPage extends BasePage implements View.OnClickListener,View.OnLongClickListener {
    private static final int SEARCH = 0;
    @Bind(R.id.num1layout)
    LinearLayout num1layout;
    @Bind(R.id.num2layout)
    LinearLayout num2layout;
    @Bind(R.id.num3layout)
    LinearLayout num3layout;
    @Bind(R.id.num4layout)
    LinearLayout num4layout;
    @Bind(R.id.num5layout)
    LinearLayout num5layout;
    @Bind(R.id.num6layout)
    LinearLayout num6layout;
    @Bind(R.id.num7layout)
    LinearLayout num7layout;
    @Bind(R.id.num8layout)
    LinearLayout num8layout;
    @Bind(R.id.num9layout)
    LinearLayout num9layout;
    @Bind(R.id.num0layout)
    LinearLayout num0layout;
    @Bind(R.id.numrlayout)
    LinearLayout numrlayout;
    @Bind(R.id.numllayout)
    LinearLayout numllayout;

    @Bind(R.id.callnum)
    TextView mCallNumView;
    @Bind(R.id.delnum)
    ImageView mDelNumView;
    @Bind(R.id.searchResult)
    ListView searchResult;
    @Bind(R.id.searchLayout)
    RelativeLayout searchLayout;
    @Bind(R.id.call)
    ImageView call;
    @Bind(R.id.rl_log)
    RelativeLayout rl_log;
    @Bind(R.id.calllog_null)
    LinearLayout mCallNodataLayout;
    @Bind(R.id.recent_list)
    ListView mRencentListview;
    @Bind(R.id.keypad_list)
    LinearLayout keypad_list;
    private volatile ArrayList<Contact> mAllContacts=new ArrayList<>();
    private final static int MAXLENGTH = 48;
    private int mPageCount=30;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private ArrayList<Contact> mCallLogList =new ArrayList<>();
    private CallLogAdapter mCallLogAdapter;
    private int index;



    public CallKeyPadPage(Context context) {
        super(context);
    }
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SEARCH:
                    ArrayList<Contact> contacts= (ArrayList<Contact>) msg.obj;

                    if (contacts.size()>0){
                        mSearchContact.clear();;
                        mSearchContact.addAll(contacts);
                        searchLayout.setVisibility(View.VISIBLE);

                        final SearchAdapter searchAdapter = new SearchAdapter(mChar, ct, mSearchContact);
                        searchResult.setAdapter(searchAdapter);
                        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                callPhone((mSearchContact.get(position)).getNumber().toString());
                            }
                        });
                    } else {
                        searchLayout.setVisibility(View.INVISIBLE);
                        Trace.Debug("#### phone  invisible");
                    }

                    break;
                case MessageTypeCfg.RECENT_CONTACT:
                    ArrayList<Contact> conList= (ArrayList<Contact>) msg.obj;

                    mCallLogList.addAll(conList);
                    mCallLogAdapter.notifyDataSetChanged();
                    if (mCallLogList.size() > 0) {
                        mCallNodataLayout.setVisibility(View.GONE);
                        mRencentListview.setVisibility(View.VISIBLE);
                        // saveLastCallLog();
                    } else {
                        mCallNodataLayout.setVisibility(View.VISIBLE);
                        mRencentListview.setVisibility(View.GONE);
                    }

                    break;
                case  MessageTypeCfg.MSG_SUBITEMS_OBTAINED:
                    ArrayList<Contact> allContacts= (ArrayList<Contact>) msg.obj;
                    mAllContacts.clear();
                    mAllContacts.addAll(allContacts);
                    break;

                default:
                    break;
            }
        }
    };

    public void clearNumEdit() {
        if (mCallNumView != null) {
            mCallNumView.setText("");
            mDelNumView.setVisibility(View.GONE);
            searchLayout.setVisibility(View.INVISIBLE);

        }
    }

    public String getNumText() {
        if (mCallNumView != null) {
            return mCallNumView.getText().toString();

        }
        return null;

    }


    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.page_callkeypad, null);
        } else {
//
            view = inflater.inflate(R.layout.page_callkeypad_l, null);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    ArrayList<Contact> mSearchContact=new ArrayList<>();
    CharSequence mChar;
    int mCount;
    ExecutorService singleThreadExecutor;
    LinearLayout.LayoutParams linearParams;

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.num0layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input("+");
                }
                break;
        }
        return true;
    }

    public class MyRun implements Runnable {
        @Override
        public void run() {

            search(mChar+"");

        }
    }

    ContactManager mContactManager;

    @Override
    public void initData() {
//        以下是获取通话记录
        rl_log.setVisibility(View.VISIBLE);
        keypad_list.setVisibility(View.GONE);
        index = 0;
        mContactManager = ContactManager.getInstance(ct);
        mContactManager.getRecentContactsTwo(mHandler,index,mPageCount);
        mCallLogAdapter = new CallLogAdapter(ct, mCallLogList);
        mRencentListview.setAdapter(mCallLogAdapter);
        mRencentListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callPhone(((Contact) mCallLogAdapter.getItem(position)).getNumber().toString());
            }
        });
        mCallLogList.clear();
        mRencentListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int visibleItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int itemsLastIndex = mCallLogList.size() - 1;    //数据集最后一项的索引
                int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                    //如果是自动加载,可以在这里放置异步加载数据的代码
                    index++;
                    mContactManager.getRecentContactsTwo(mHandler,index,mPageCount);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount;
            }
        });
        final MyRun myRun = new MyRun();
        mContactManager.getLocalContactsTwo(mHandler);
        mCallNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChar = s.toString().trim();
                if(judgeQuickCode(mChar)) {
                    return;
                }
                if (mChar.equals("")) {
                    rl_log.setVisibility(View.VISIBLE);
                    keypad_list.setVisibility(View.GONE);

                } else {
                    rl_log.setVisibility(View.GONE);
                    keypad_list.setVisibility(View.VISIBLE);

                }
                Trace.Error("=mChar=", mChar.toString().trim());
                mCount = count;
                if (count != 0) {
                    mDelNumView.setVisibility(View.VISIBLE);
                } else {
                    mDelNumView.setVisibility(View.GONE);
                }
//                searchLayout.setVisibility(View.GONE);
                if (singleThreadExecutor!=null){
                    singleThreadExecutor.shutdownNow();
                }
                singleThreadExecutor = Executors.newSingleThreadExecutor();
                singleThreadExecutor.execute(myRun);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        mDelNumView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallNumView.setText("");
                mDelNumView.setVisibility(View.GONE);
                return false;
            }
        });
        num0layout.setOnClickListener(this);
        num0layout.setOnLongClickListener(this);
        num1layout.setOnClickListener(this);
        num2layout.setOnClickListener(this);
        num3layout.setOnClickListener(this);
        num4layout.setOnClickListener(this);
        num5layout.setOnClickListener(this);
        num6layout.setOnClickListener(this);
        num7layout.setOnClickListener(this);
        num8layout.setOnClickListener(this);
        num9layout.setOnClickListener(this);
        numllayout.setOnClickListener(this);
        numrlayout.setOnClickListener(this);
        mDelNumView.setOnClickListener(this);
        call.setOnClickListener(this);
    }


    private volatile boolean mSearchRun=true;
    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    private ArrayList<Contact> search(String str) {

        ArrayList<Contact> filterList = new ArrayList<Contact>();//过滤后的list
        Trace.Debug("##### str="+ str +"  number="+mAllContacts.size());
        for (int i = 0; i < mAllContacts.size(); i++) {
            if (mSearchRun){

                Contact contact=mAllContacts.get(i);
                if (contact!=null){
                    if (contact.getNumber() != null) {
                        Trace.Debug("##### str="+ str +"  number="+contact.getNumber());
                        String midText = contact.getNumber().toString().trim().replaceAll(" ", "");
                        if (midText.contains(str)) {
                            if (!filterList.contains(contact)) {
                                contact.setNumber(midText);
                                filterList.add(contact);
                            }
                        }
                    }
//            }
                    if (contact.getName() != null) {
                        String charName = contact.getName();
                        String strNameView = String.copyValueOf(charName.toCharArray(), 0, charName.length());
                        boolean isChinese = PinYinUtil.isChinese(strNameView);
                        if (contains(contact, str, isChinese)) {
                            contact.setNumber(contact.getNumber().toString().trim().replaceAll(" ", ""));
                            filterList.add(contact);
                        }
                    }
                }
            }else{
                break;
            }
        }
        Message message=mHandler.obtainMessage();
        message.what=SEARCH;
        message.obj=filterList;
        mHandler.sendMessage(message);
        return filterList;
    }

    //石孟添加智能拨号盘功能,20160520,begin
    String getNumberFormChar(char c) {
        if (c >= 'a' && c <= 'c') {
            return "2";
        } else if (c >= 'd' && c <= 'f') {
            return "3";
        } else if (c >= 'g' && c <= 'i') {
            return "4";
        } else if (c >= 'j' && c <= 'l') {
            return "5";
        } else if (c >= 'm' && c <= 'o') {
            return "6";
        } else if (c >= 'p' && c <= 's') {
            return "7";
        } else if (c >= 't' && c <= 'v') {
            return "8";
        } else if (c >= 'w' && c <= 'z') {
            return "9";
        } else if ('0' <= c && c <= '9') {
            return "" + c;
        } else {
            return "";
        }
    }

    String getNameNumber(String name) {
        String number = "";
        String nameLow = name.toLowerCase();
        for (int i = 0; i < nameLow.length(); i++) {
            char c = nameLow.charAt(i);
            number = number + getNumberFormChar(c);
        }
        return number;
    }

    //石孟添加智能拨号盘功能,20160520,end
    private void callPhone(String str) {
        if (ContextCompat.checkSelfPermission(ct, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
// 没有获得授权，申请授权
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ct,Manifest.permission.CALL_PHONE)) {
// 返回值：
//如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
// 弹窗需要解释为何需要该权限，再次请求授权
                Toast.makeText(ct, "请授权！", Toast.LENGTH_LONG).show();
// 帮跳转到该应用的设置界面，让用户手动授权
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", ct.getPackageName(), null);
                intent.setData(uri);
                ct.startActivity(intent);
            }else{
// 不需要解释为何需要该权限，直接请求授权
                ActivityCompat.requestPermissions((Activity) ct,new String[]{Manifest.permission.CALL_PHONE},1);
            }
        }else {
// 已经获得授权，可以打电话
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str));
            ct.startActivity(intent);
        }




        Trace.Error("==PhoneName==", android.os.Build.MODEL);
//            if (android.os.Build.MODEL.contains("vivo") || android.os.Build.MODEL.contains("coolpad")) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
//                ct.startActivity(intent);
//
//            } else {
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + str));
//        ct.startActivity(intent);
//            }


    }

    private void input(String str) {
        String num = mCallNumView.getText().toString();
        mCallNumView.setText(num + str);
    }

    private void delete() {
        String num = mCallNumView.getText().toString();
        mCallNumView.setText(num.substring(0, num.length() - 1));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num0layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(0 + "");
                }
                break;
            case R.id.num1layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(1 + "");
                }
                break;
            case R.id.num2layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(2 + "");
                }
                break;
            case R.id.num3layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(3 + "");
                }
                break;
            case R.id.num4layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(4 + "");
                }
                break;
            case R.id.num5layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(5 + "");
                }
                break;
            case R.id.num6layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(6 + "");
                }
                break;
            case R.id.num7layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(7 + "");
                }
                break;
            case R.id.num8layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(8 + "");
                }
                break;
            case R.id.num9layout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input(9 + "");
                }
                break;
            case R.id.numllayout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input("*");
                }
                break;
            case R.id.numrlayout:
                if (mCallNumView.getText().length() < MAXLENGTH) {
                    input("#");
                }
                break;
            case R.id.delnum:
                if (mCallNumView.getText().length() > 0) {
                    delete();
                }
                break;
            case R.id.call:
                if (mCallNumView.getText().toString().length() > 0) {
                    callPhone(mCallNumView.getText().toString());
                    mCallNumView.setText("");
                } else {
                    ToastUtil.showShort(ct, ct.getString(R.string.str_input_number_toast));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据拼音搜索
     *
     * @param contact   当前电话本
     * @param search    输入的数字字符
     * @param isChinese 是否为中文
     * @return
     */
    public boolean contains(Contact contact, String search,
                            boolean isChinese) {
        if (TextUtils.isEmpty(contact.getName())) {
            return false;
        }
        String pinYinName = null;
        String charName = contact.getName();
        String strNameView = String.copyValueOf(charName.toCharArray(), 0, charName.length());
        if (isChinese) {
            pinYinName = PinYinUtil.getPingYin(strNameView);
        } else {
            pinYinName = strNameView;
        }
        String nameNum = getNameNumber(pinYinName);
        boolean flag = false;
        if (nameNum != null && search != null && nameNum.startsWith(search, 0)) {
            return true;
        }
        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            String firstLetters = FirstLetterUtil.getFirstLetter(contact
                    .getName());
            String firstLettersNum = getNameNumber(firstLetters);
            if (firstLettersNum != null && search != null && firstLettersNum.startsWith(search, 0)) {
                return true;
            }
        }

        return flag;
    }

    @Override
    public void destory() {
        super.destory();
        mAllContacts.clear();
        mSearchContact.clear();
        mCallLogList.clear();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
    }

    /**
     * 判断是不是有后台暗码
     * "*#4507*#" 打开语音pcm调试
     * "*#1207*#" 打开应用内存CPU调试
     * @param mChar
     * @return
     */
    private boolean judgeQuickCode(CharSequence mChar) {
        boolean value = false;
        if (mChar.equals("*#4507*#")) {
            value = true;
            showVoiceDebugDialog();
        }

        if (mChar.equals("*#1207*#")) {
            value = true;
            showMemoryDebugDialog();
        }
        return value;
    }

    private void showMemoryDebugDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setMessage("要打开內存调试吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBusHelper.post(Constant.SHOW_DEBUG_VIEW);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EventBusHelper.post(Constant.HIDE_DEBUG_VIEW);
            }
        });

        builder.show();
    }

    private void showVoiceDebugDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setMessage("要打开瘦车机语音调试吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlobalCfg.isVoiceDebugOpen = true;
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GlobalCfg.isVoiceDebugOpen = false;
            }
        });

        builder.show();
    }
}
