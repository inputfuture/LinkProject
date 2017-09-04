package com.letv.leauto.ecolink.IOStream;

import com.leauto.link.lightcar.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zhaotongkai on 2016/11/28.
 *
 * VoiceInputStream is used for buffering the PCM voice data coming from Thin-Car
 * Voice recognization SDK invoke this inputstream for recognization.
 *
 * Notice: This class must cant confused as Baidu Voice SDK wished.
 *
 */
public class VoiceInputStream extends InputStream {

    private final static String TAG = VoiceInputStream.class.getSimpleName();

    private static Queue<byte[]> sVoicDataQueue = new ConcurrentLinkedQueue<byte[]>();
    private static VoiceInputStream mInputStream;

    private boolean isListening = false;

    private int mTotal = 0;
    public static VoiceInputStream getStream() {
        if (mInputStream == null) {
            mInputStream = new VoiceInputStream();
        }
        return mInputStream;
    }

    private VoiceInputStream() {

    }

    /**
     * 本次识别结束后就不再保存后续的语音数据，防止占用内存
     * @param listening
     */
    public void setListening(boolean listening) {
        isListening = listening;
        clear();
    }

    /**
     * clear the PCM voice data
     */
    public void clear() {
        sVoicDataQueue.clear();
        mTotal = 0;
    }

    /**
     * write PCM voice data to Queue
     * @param data
     */
    public void writeVoice(byte[] data) {
        // LogUtils.i("XXX", "--->writeVoice: " + sVoicDataQueue.size());
        if (!isListening) {
            return;
        }
        sVoicDataQueue.offer(data);
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        int tryTimes = 3;
        LogUtils.i("XXX", "--->read ");
        for (;;) {
            if (sVoicDataQueue.size() <= 0 && tryTimes > 0) {
                try {
                    Thread.sleep(1000);
                    tryTimes--;
                } catch (Exception e) {

                }
            } else {
                break;
            }
        }

        buffer = sVoicDataQueue.poll();
        return buffer.length;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int tryTimes = 3;
        //LogUtils.i("XXX", "--->read " + byteCount);
        for (;;) {
            if (sVoicDataQueue.size() <= byteCount/512 && tryTimes > 0) {
                try {
                    Thread.sleep(50);
                    tryTimes--;
                } catch (Exception e) {

                }
            } else {
                break;
            }
        }
        byte[] src;
        for (int i = 0; i < byteCount/512; i++) {
            if (sVoicDataQueue.size() <= 0) {
                break;
            }
            src = sVoicDataQueue.remove();
            System.arraycopy(src, 0, buffer, i*src.length, src.length);
        }
        // writePcmToFile(buffer, 0, buffer.length);
        // buffer = sVoicDataQueue.poll();
        // LogUtils.i("XXX", "--->read: " + this + "  size: " + sVoicDataQueue.size());
        return buffer.length;
    }

    @Override
    public int read() throws IOException {
        LogUtils.i("XXX", "--->read 102 ");
        return 0;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    public void writePcmToFile(byte[] buff, int src, int re_lenght) {
        File file = new File("/mnt/sdcard/lec-back.pcm");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file, "rw");
            accessFile.seek(file.length());
            accessFile.write(buff, src, re_lenght);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
