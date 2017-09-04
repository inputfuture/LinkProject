package com.letv.leauto.ecolink.thincar.protocol;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leauto.link.lightcar.LogUtils;
import com.leauto.link.lightcar.ThinCarDefine;
import com.leauto.link.lightcar.protocol.DataSendManager;
import com.letv.leauto.ecolink.cfg.MessageTypeCfg;
import com.letv.leauto.ecolink.database.model.Contact;
import com.letv.leauto.ecolink.manager.ContactManager;
import com.letv.leauto.ecolink.thincar.module.BlueToothContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/4.
 */
public class BlueToothHelp {
    private static BlueToothHelp ourInstance = new BlueToothHelp();

    /**
     * 一次发送的联系人数量
     */
    private int ONE_SEND_PHONE_BOOK_COUNT = 50;

    /**
     * 无法获取联系人信息或者通话记录
     */
    private int CAN_NOT_GET_PHONE_BOOK = -1;

    private static final String PHONE_BOOK = "PhoneBook";

    private static final String CALL_HISTROY = "CallHistory";

    private static final int CONTACT_CELL_TYPE = 0;
    private static final int CONTACT_HOME_TYPE = 1;
    private static final int CONTACT_WORK_TYPE = 2;
    private static final int CONTACT_OTHER_TYPE = 3;

    List<BlueToothContact> contactList = new ArrayList<>();

    public static BlueToothHelp getInstance() {
        return ourInstance;
    }

    private BlueToothHelp() {
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessageTypeCfg.MSG_NO_CONNECTS_PERMISSION:
                    sendNoneContacts(PHONE_BOOK);
                    break;
                case MessageTypeCfg.RECENT_CONTACT:
                    ArrayList<Contact> recentCallContacts= (ArrayList<Contact>) msg.obj;
                    if (recentCallContacts != null && recentCallContacts.size() > 0) {
                        sendAllRecentCalls(recentCallContacts);
                    } else {
                        sendNoneContacts(CALL_HISTROY);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 发送最近通话记录
     * @param recentCallContacts
     */
    private void sendAllRecentCalls(ArrayList<Contact> recentCallContacts) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Contact contact : recentCallContacts) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", contact.getName());
            item.put("num", contact.getNumber());
            if (contact.getToday() != null) {
                item.put("time", contact.getToday().getTime() + "");
            } else {
                item.put("time", "");
            }

            list.add(item);
            if (list.size() == ONE_SEND_PHONE_BOOK_COUNT) {
                sentLimitContacts(list,recentCallContacts.size(),CALL_HISTROY);

                list.clear();
            }
        }

        if (list.size() > 0) {
            sentLimitContacts(list,recentCallContacts.size(),CALL_HISTROY);
        }
    }

    /**
     * 没有获取到联系人信息
     */
    private void sendNoneContacts(String method) {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", method);

        Map<String, Object> content = new HashMap<>();
        content.put("sum",CAN_NOT_GET_PHONE_BOOK);

        map.put("Parameter", content);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.BLUETOOTH_APPID,jsonObject);
    }

    private void sentAllContacts(List<BlueToothContact> contacts) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BlueToothContact contact : contacts) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", contact.name);
            item.put("num", contact.number);
            item.put("type", contact.type);
            list.add(item);

            if (list.size() == ONE_SEND_PHONE_BOOK_COUNT) {
                sentLimitContacts(list,contacts.size(),PHONE_BOOK);

                list.clear();
            }
        }

        if (list.size() > 0) {
            sentLimitContacts(list,contacts.size(),PHONE_BOOK);
        }
    }

    /**
     * 每次发送 @ONE_SEND_PHONE_BOOK_COUNT 联系人给车机
     * @param list
     */
    private void sentLimitContacts(List<Map<String, Object>> list, int totalNum, String method) {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", method);

        Map<String, Object> content = new HashMap<>();
        content.put("sum",totalNum);
        content.put("items",list);

        map.put("Parameter", content);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.BLUETOOTH_APPID,jsonObject);
    }

    /**
     * 请求通讯录
     */
    public void requestPhoneBook(Context context) {
        getLocalContact(context);
    }

    /**
     * 请求通话记录
     */
    public void requestCallHistory(String upLimit, Context context) {
        int limit = 0;
        try {
            limit = Integer.parseInt(upLimit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContactManager contactManager = ContactManager.getInstance(context);
        contactManager.getRecentContactsTwo(mHandler,0,limit);
    }

    private void getLocalContact(final Context context) {
        new Thread() {

            @Override
            public void run() {
                try {
                    contactList.clear();
                    ContentResolver contentResolver = context.getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                            new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
                    while (cursor.moveToNext()) {
                        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        queryAllNumbers(contentResolver,name,contactId);
                    }

                    if (contactList.size() > 0) {
                        sentAllContacts(contactList);
                    } else {
                        sendNoneContacts(PHONE_BOOK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 根据contactId查询这个联系人的所有号码
     * @param contactId
     */
    private void queryAllNumbers(ContentResolver contentResolver,String name,String contactId) {
        Cursor phone = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                        + contactId , null, null);
        while (phone.moveToNext()) {
            String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = phone.getInt(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            BlueToothContact contact = new BlueToothContact();
            contact.name = name;
            contact.number = number;
            contact.type = transferToThincarType(phoneNumberType);
            contactList.add(contact);
        }
    }

    private int transferToThincarType(int phoneNumberType) {
        int type = CONTACT_OTHER_TYPE;
        switch (phoneNumberType ){
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                type = CONTACT_CELL_TYPE;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                type = CONTACT_HOME_TYPE;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                type = CONTACT_WORK_TYPE;
                break;
            default:
                break;
        }

        return type;
    }

    public void requestBlueToothInfo() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "PhoneBTNameAndAddr");

        Map<String, Object> content = new HashMap<>();
        String macAddr = adapter.getAddress();
        content.put("bt_name", adapter.getName());
        content.put("bt_addr[0]", getMacValue(macAddr, 0));
        content.put("bt_addr[1]", getMacValue(macAddr, 1));
        content.put("bt_addr[2]", getMacValue(macAddr, 2));
        content.put("bt_addr[3]", getMacValue(macAddr, 3));
        content.put("bt_addr[4]", getMacValue(macAddr, 4));
        content.put("bt_addr[5]", getMacValue(macAddr, 5));

        map.put("Parameter", content);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.BLUETOOTH_APPID,jsonObject);
    }

    public void requestAutoConnect(Context context,String btName,String btAddr) {
        responseAutoConnect();
    }

    public void responseAutoConnect() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "Interface_Response");
        map.put("Method", "AutoConnect");

        JSONObject jsonObject = (JSONObject) JSON.toJSON(map);

        DataSendManager.getInstance().sendJsonDataToCar(ThinCarDefine.ProtocolAppId.BLUETOOTH_APPID,jsonObject);
    }

    private int getMacValue(String macAddr, int i) {
        int start = i * 3;
        int end = i * 3 + 2;
        String sub = macAddr.substring(start, end);
        LogUtils.i("BlueTooth", "getMacValue sub:" + sub);
        int value = Integer.parseInt(sub, 16);
        LogUtils.i("BlueTooth", "getMacValue value:" + value);
        return value;
    }
}
