package com.letv.leauto.ecolink.easystop;

import android.util.Log;

import com.letv.leauto.ecolink.utils.Trace;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Crypto {
    private static String TAG = "MD5Util";

    protected static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Trace.Debug(TAG, e.getMessage());
            messagedigest = null;
        }
    }

    static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2.getMessage());
        }
    }

    public static String getFileMD5String(File file) {
        String ret = null;
        if (messagedigest != null) {
            FileInputStream in = null;
            FileChannel ch = null;
            try {
                in = new FileInputStream(file);
                ch = in.getChannel();
                MappedByteBuffer byteBuffer =
                        ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                messagedigest.update(byteBuffer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        Trace.Debug(TAG, e.getMessage());
                    }
                }
                if (ch != null) {
                    try {
                        ch.close();
                    } catch (Exception e) {
                        Trace.Debug(TAG, e.getMessage());
                    }
                }
            }
            ret = bufferToHex(messagedigest.digest());
        }

        return ret;
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        String bufferToHex = null;
        if (messagedigest != null) {
            messagedigest.update(bytes);
            bufferToHex = bufferToHex(messagedigest.digest());
        }

        return bufferToHex;
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static String Md5(String plainText) {
        String afterStr = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            afterStr = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return afterStr;
    }

    private static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int j = 0; i < l; ++i) {
            out[j++] = toDigits[(240 & data[i]) >>> 4];
            out[j++] = toDigits[15 & data[i]];
        }

        return out;
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static byte[] md5(byte[] data) {

        return getMd5Digest().digest(data);
    }

    public static String encodeHexString(byte[] data) {

        return new String(encodeHex(data));
    }

    public static String md5Hex(byte[] data) {
        return encodeHexString(md5(data));
    }

    public static String MD5Encode(String origin, String charset) {
        try {
            return md5Hex(origin.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
