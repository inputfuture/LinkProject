package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Xml;

import com.letv.leauto.ecolink.database.model.Contact;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhoneNumberUtil {
	private static String[] array = new String[] {"电信","联通","移动"};

	public static String getNumberLocation(String mobile) {
		try {
			String format = mobile.replaceAll(" ","");
			String soap = readSoap();
			soap = soap.replaceAll("\\$mobile", format);
			byte[] entity = soap.getBytes();
			String path = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
			conn.getOutputStream().write(entity);

			if (conn.getResponseCode() == 200) {
				return parseSoap(conn.getInputStream());
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private static String parseSoap(InputStream xml) throws Exception {
		// TODO Auto-generated method stub
		XmlPullParser pullParser= Xml.newPullParser();
		pullParser.setInput(xml,"utf-8");
		int event=pullParser.getEventType();
		while(event!=XmlPullParser.END_DOCUMENT)
		{
			switch(event)
			{
				case XmlPullParser.START_TAG:
					if("getMobileCodeInfoResult".equals(pullParser.getName()))
					{
						String result = pullParser.nextText();
						return result;
					}
					break;
			}
			event=pullParser.next();
		}
		return null;
	}

	private static String readSoap() throws Exception {
		InputStream inStream = PhoneNumberUtil.class.getClassLoader().getResourceAsStream("assets/send.xml");
		byte[] data = inputToByteArray(inStream);
		return new String(data);
	}

	public static byte[] inputToByteArray(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int len=0;
		while((len=inStream.read(buffer)) !=-1)
		{
			outStream.write(buffer,0,len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	public static int getNumberType(Context context,String mobile) {
		String number = mobile.replaceAll(" ","");
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Cursor cur =context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.TYPE},null,null,null);
		int type = 0;
		while (cur.moveToNext()) {
			type = cur.getInt(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.TYPE));
		}
		cur.close();
		return type;
	}

	public static String filteNumber(String number) {
		for (String str: array) {
			if (number.contains(str)) {
				int index = number.lastIndexOf(str);
				return number.substring(0,index);
			}
		}

		return number;
	}
}
