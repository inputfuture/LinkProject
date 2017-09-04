package com.letv.leauto.ecolink.manager;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.AlphabetIndexer;


import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.utils.TimeUtils;
import com.letv.leauto.ecolink.utils.Trace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContactManager {

    private  Context mContext;
    private static ContactManager instance;
    private AlphabetIndexer indexer;




    public static ContactManager getInstance(Context context) {
        if (null == instance) {
            synchronized (ChoosedAppManager.class) {
                if (instance == null) {
                    instance = new ContactManager(context);
                }
            }

        }
        return instance;
    }

    private ContactManager(Context context) {
        mContext=context.getApplicationContext();
        mContext.getContentResolver().registerContentObserver(
                CallLog.Calls.CONTENT_URI, true, mObserver);

    }



    private ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Trace.Debug("####通讯录更新");


        }
    };

    public void destroy(){
        mContext.getContentResolver().unregisterContentObserver(mObserver);
    }


    public int LocalContactsCount;


    public ArrayList<Contact> getLocalContacts() {

        /* 开始获取本地联系人 */
        ArrayList<Contact> contactList = new ArrayList<Contact>();

        // 搜索条件，只取邮件和电话号码数据
        String selection = ContactsContract.Data.MIMETYPE + " in ( \'"
                + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                + "\' ) ";

        Cursor cursor = mContext.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.Data.DATA1,
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.Data.SORT_KEY_PRIMARY,

                }, selection, null, ContactsContract.Data.SORT_KEY_PRIMARY);
        if (cursor != null) {
            LocalContactsCount = cursor.getCount();
            Contact contact;
            if (cursor != null) {
                contactList = new ArrayList<Contact>();
                while (cursor.moveToNext()) {
                    String data = cursor.getString(2);
                    if (data == null || data.length() <= 0)
                        continue;
                    contact = new Contact();
                    contact.setName(cursor.getString(1));
                    contact.setNumber(cursor.getString(2));
                    contact.setSortKey(getSortKey(cursor.getString(4)));
                    contactList.add(contact);

                }
                String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                indexer = new AlphabetIndexer(cursor, 1, alphabet);
                cursor.close();
            }
        }

        return contactList;
    }
    public void getLocalContactsTwo(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Contact> contactList = null;
                    String selection = ContactsContract.Data.MIMETYPE + " in ( \'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "\' ) ";
                    Cursor cursor = mContext.getContentResolver().query(
                            ContactsContract.Data.CONTENT_URI,
                            new String[]{ContactsContract.Data.CONTACT_ID,
                                    ContactsContract.Data.DISPLAY_NAME,
                                    ContactsContract.Data.DATA1,
                                    ContactsContract.Data.MIMETYPE,
                                    ContactsContract.Data.SORT_KEY_PRIMARY,

                            }, selection, null, ContactsContract.Data.SORT_KEY_PRIMARY);
                    if (cursor != null) {
                        LocalContactsCount = cursor.getCount();
                        contactList = new ArrayList<Contact>();
                        while (cursor.moveToNext()) {
                            String data = cursor.getString(2);
                            if (data == null || data.length() <= 0)
                                continue;
                            Contact contact = new Contact();
                            String name = cursor.getString(1);
                            contact.setName(name);
                            if (contactList.contains(contact)){
                                int index=contactList.indexOf(contact);
                                Contact saveContact=contactList.get(index);
                                String number=cursor.getString(2);
                                saveContact.getNumList().add(number);

                            }else {
                                String number=cursor.getString(2);
                                contact.setName(name);
                                ArrayList<String> tels = new ArrayList<String>();
                                contact.setNumber(number);
                                tels.add(number);
                                contact.setNumList(tels);
                                contact.setSortKey(getSortKey(cursor.getString(4)));
                                contactList.add(contact);
                            }


                        }
                        cursor.close();
                        Message message = Message.obtain();
                        message.obj = contactList;
                        message.what = MessageTypeCfg.MSG_SUBITEMS_OBTAINED;
                        handler.sendMessage(message);
                    }else{
                        Trace.Debug("##### 通讯录未同步");
                        Message message = Message.obtain();
                        message.what = MessageTypeCfg.MSG_NO_CONNECTS_PERMISSION;
                        handler.sendMessage(message);
                    }

                }catch (Exception e){
                    Trace.Debug("##### 通讯录未同步");
                    Message message = Message.obtain();
                    message.what = MessageTypeCfg.MSG_NO_CONNECTS_PERMISSION;
                    handler.sendMessage(message);
                }
            }

        }).start();
    }

    public AlphabetIndexer getIndexer() {
        if (indexer != null) {
            return indexer;
        }
        return null;
    }





    public ArrayList<Contact> getRecentContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        ContentResolver cr = mContext.getContentResolver();
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION},
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        Contact contact;
        SimpleDateFormat sfd = null;
        String weekday = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                contact = new Contact();
                Contact last = new Contact();
                //if (cursor.getString(0).equals(last.getNumber()) && (cursor.getInt(2) == last.getType())) {
                contact.setNumber(cursor.getString(0)); //呼叫号码
                if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
                    contact.setName(cursor.getString(1)); //联系人姓名
                } else {
                    contact.setName(cursor.getString(0)); //联系人姓名
                }
                contact.setType(cursor.getInt(2));//来电:1,拨出:2,未接:3 public static final int INCOMING_TYPE = 1;   public static final int OUTGOING_TYPE = 2;   public static final int MISSED_TYPE = 3;
                Date date = new Date(Long.parseLong(cursor.getString(3)));
                Long callTime = date.getTime();
               Trace.Info("calllog", "calltime=" + callTime);
                if ((TimeUtils.getCurrantTime() - callTime) / (1000 * 60) < 1) {
                    contact.setTime("刚刚");
                } else if (TimeUtils.isToday(date)) {
                    sfd = new SimpleDateFormat("HH:mm");
                    contact.setTime(sfd.format(date));
                } else if (TimeUtils.isYesterday(callTime)) {
                    contact.setTime("昨天");
                } else if (TimeUtils.isThisWeek(callTime)) {
                    weekday = TimeUtils.DateToWeek(date);
                    contact.setTime(weekday);
                } else {
                    sfd = new SimpleDateFormat("yy/MM/dd");
                    contact.setTime(sfd.format(date));
                }

                contactList.add(contact);
                // }
                last = contact;
            } while (cursor.moveToNext());
            cursor.close();
        }
        ArrayList<Contact> newConList = new ArrayList<Contact>();
        int num = 1;
//        boolean noCall = false;
        for (int i = 0; i < contactList.size(); i++) {
            contact = new Contact();
            if (i + 1 == contactList.size()) {
                contact.setName(contactList.get(i).getName());
                contact.setNum(num);
                contact.setType(contactList.get(i).getType());
                contact.setNumber(contactList.get(i).getNumber());
                contact.setTime(contactList.get(i).getTime());
                newConList.add(contact);
                break;

            }
//                if(null != contactList.get(i).getName() && null != contactList.get(i+1).getName()){
            if (contactList.get(i).getName().equals(contactList.get(i + 1).getName()) && contactList.get(i).getType() == contactList.get(i + 1).getType()) {
                num++;
                continue;
            } else {

                contact.setName(contactList.get(i).getName());
                contact.setNum(num);
                contact.setType(contactList.get(i).getType());
                contact.setNumber(contactList.get(i).getNumber());
                contact.setTime(contactList.get(i).getTime());
                newConList.add(contact);
//                        noCall = false;
                num = 1;
            }


        }
        return newConList;
    }

    public void getRecentContactsTwo(final Handler handler, final int index, final int count) {
        new Thread(){
            @Override
            public void run() {
                ArrayList<Contact> contactList = new ArrayList<Contact>();
                ContentResolver cr = mContext.getContentResolver();
                final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                        new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION},
                        null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                Contact contact;
                SimpleDateFormat sfd = null;
                String weekday = null;
                if (cursor != null) {
                    if (cursor.getCount() > index * count) {
                        int firstZize = index * count;
                        int endSize = (index + 1) * count;
                        int cursorCount = cursor.getCount();
                        for (int i = firstZize; i < endSize; i++) {
                            if (cursorCount <= i) {
                                break;
                            } else {
                                cursor.moveToPosition(i);
                                contact = new Contact();
                                Contact last = new Contact();
                                //if (cursor.getString(0).equals(last.getNumber()) && (cursor.getInt(2) == last.getType())) {
                                contact.setNumber(cursor.getString(0)); //呼叫号码
                                if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
                                    contact.setName(cursor.getString(1)); //联系人姓名
                                } else {
                                    contact.setName(cursor.getString(0)); //联系人姓名
                                }
                                contact.setType(cursor.getInt(2));//来电:1,拨出:2,未接:3 public static final int INCOMING_TYPE = 1;   public static final int OUTGOING_TYPE = 2;   public static final int MISSED_TYPE = 3;
                                Trace.Error("==contact.setType==", cursor.getInt(2) + "");
                                Date date = new Date(Long.parseLong(cursor.getString(3)));
                                Long callTime = date.getTime();
                               Trace.Info("calllog", "calltime=" + callTime);
                                contact.setToday(date);
                                if (TimeUtils.getCurrantTime() > callTime && (TimeUtils.getCurrantTime() - callTime) / (1000 * 60) < 1) {
                                    contact.setTime("刚刚");
                                } else if (TimeUtils.isToday(date)) {
                                    sfd = new SimpleDateFormat("HH:mm");
                                    contact.setTime(sfd.format(date));
                                } else if (TimeUtils.isYesterday(callTime)) {
                                    contact.setTime("昨天");
                                } else if (TimeUtils.isInOneWeek(callTime)) {
                                    weekday = TimeUtils.DateToWeek(date);
                                    contact.setTime(weekday);
                                } else {
                                    sfd = new SimpleDateFormat("yy/MM/dd");
                                    contact.setTime(sfd.format(date));
                                }

                                contactList.add(contact);
                            }

                        }
                    }
                    cursor.close();
                }

                ArrayList<Contact> newConList = new ArrayList<Contact>();
                int num = 1;
//        boolean noCall = false;
                for (int i = 0; i < contactList.size(); i++) {
                    contact = new Contact();
                    if (i + 1 == contactList.size()) {
                        contact.setName(contactList.get(i).getName());
                        contact.setNum(num);
                        contact.setType(contactList.get(i).getType());
                        contact.setNumber(contactList.get(i).getNumber());
                        contact.setTime(contactList.get(i).getTime());
                        newConList.add(contact);
                        break;

                    }
//                if(null != contactList.get(i).getName() && null != contactList.get(i+1).getName()){
                    if (contactList.get(i).getName().equals(contactList.get(i + 1).getName()) && contactList.get(i).getType() == contactList.get(i + 1).getType() && TimeUtils.isSameDay(contactList.get(i).getToday(), contactList.get(i + 1).getToday())) {
                        num++;
                        continue;
                    } else {

                        contact.setName(contactList.get(i).getName());
                        contact.setNum(num);
                        contact.setType(contactList.get(i + 1 - num).getType());
                        contact.setNumber(contactList.get(i).getNumber());
                        contact.setTime(contactList.get(i + 1 - num).getTime());
                        newConList.add(contact);
//                        noCall = false;
                        num = 1;
                    }
                }
                Message message=handler.obtainMessage();
                message.what=MessageTypeCfg.RECENT_CONTACT;
                message.obj=newConList;
                handler.sendMessage(message);
//                return newConList;
            }
        }.start();

    }


    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }


}
