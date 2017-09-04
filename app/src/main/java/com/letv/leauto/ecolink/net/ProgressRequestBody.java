package com.letv.leauto.ecolink.net;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import okio.Timeout;

/**
 * Created by tianwei on 16/3/22.
 */
public class ProgressRequestBody extends RequestBody {
    private static final String TAG = "ProgressRequestBody";
    private final RequestBody mRequestBody;
    private final UploadFileCallBack mUploadFileCallBack;
    private BufferedSink mBufferedSink;
    private boolean mIsCancel = false;
    private boolean mIsFirstTime = true;

    public ProgressRequestBody(RequestBody requestBody, UploadFileCallBack uploadFileCallBack) {
        mRequestBody = requestBody;
        mUploadFileCallBack = uploadFileCallBack;
    }

    public void setIsCancel(boolean isCancel) {
        mIsCancel = isCancel;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    public long contentLength() {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            if (mUploadFileCallBack != null)
                mUploadFileCallBack.onError(e);
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void writeTo(BufferedSink sink) {
        try {
            if (mBufferedSink == null) {
                mBufferedSink = Okio.buffer(sink(sink));
            }
            mRequestBody.writeTo(mBufferedSink);
            mBufferedSink.flush();
        } catch (IllegalStateException | IOException e) {
            if (mUploadFileCallBack != null)
                mUploadFileCallBack.onError(e);
            e.printStackTrace();
        }
    }

    private Sink sink(final Sink sink) {
        return new ForwardingSink(sink) {
            long current = 0l;
            long total = 0l;

            @Override
            public void write(Buffer source, long byteCount) {
                try {
                    super.write(source, byteCount);
                    if (total == 0) {
                        total = contentLength();
                        mUploadFileCallBack.onStart(total);
                    }
                    current += byteCount;

                    if (mIsCancel) {
                        if (mUploadFileCallBack != null) {
                            mUploadFileCallBack.onCancel();
                            return;
                        }
                    }

                    if (mUploadFileCallBack != null) {
                        mUploadFileCallBack.onLoading(total, current);
                    }

                } catch (IllegalStateException | IOException e) {

                    if (mUploadFileCallBack != null && mIsFirstTime) {
                        if (mIsCancel) {
//                            mUploadFileCallBack.onCancel();

                        } else {
//                            mUploadFileCallBack.onError(e);
                        }
                        mIsFirstTime = false;
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public Timeout timeout() {
                if (mUploadFileCallBack != null)
                    mUploadFileCallBack.onTimeOut();
                return super.timeout();
            }

            @Override
            public void close() {
                try {
                    super.close();
                    if (current == total) {
                        if (mUploadFileCallBack != null)
                            mUploadFileCallBack.onFinish();
                    }
                } catch (IOException e) {
                    if (mUploadFileCallBack != null)
                        mUploadFileCallBack.onError(e);
                    e.printStackTrace();
                }
            }
        };
    }
}
