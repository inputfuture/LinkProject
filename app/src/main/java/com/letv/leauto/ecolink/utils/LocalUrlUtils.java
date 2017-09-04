package com.letv.leauto.ecolink.utils;

import java.net.URLEncoder;

import android.util.Base64;

import com.letvcloud.cmf.MediaSource;
import com.letvcloud.cmf.utils.StringUtils;

public class LocalUrlUtils {
    public static final String HTTP_LOCAL_SERVER_DOMAIN = "http://127.0.0.1:";
    public static final String RTSP_LOCAL_SERVER_DOMAIN = "rtsp://127.0.0.1:";
    public static final String RT_STREAM = "rt";
    public static final String RTMP_STREAM = "rtmp";
    public static final String RTSP_STREAM = "rtsp";
    public static final String STATE_PARAM_PLAY_OTHER = "cde=1&simple=1&maxDuration=1000";
    public static final String EXT_TYPE_M3U8 = "ext=m3u8";

    private static final String ENC_UTF8 = "UTF-8";
    private static final String ENC_BASE64 = "base64";
    private static final String ENC_RAW = "raw";
    private static final String M3U8 = "m3u8";
    private static final String MEDIA_TYPE_M3U8 = "&mediatype=m3u8";

    public static final String REQUEST_STREAM_1 = "stream/1";
    public static final String REQUEST_STREAM_SILENT = "stream/silent";
    private static final String REQUEST_STREAM_1FLV = "stream/1.flv";
    private static final String REQUEST_PLAY = "play";
    private static final String REQUEST_PLAY_STOP = "play/stop";
    private static final String REQUEST_PLAY_PAUSE = "play/pause";
    private static final String REQUEST_PLAY_RESUMEE = "play/resume";
    private static final String REQUEST_STATE_PLAY = "state/play";
    private static final String REQUEST_REPORT_TRAFFIC = "report/traffic";
    private static final String REQUEST_REPORT_COMMON = "report/common";
    private static final String REQUEST_CONTROL = "/control/";
    private static final String REQUEST_SUPPORT = "/support/open?contact=";
    private static final String REQUEST_LOG = "/log/text?limit=";
    private static final String REPORT_PARAM_OTHER = "&cde=1&styled=1&needCurrentProcess=1&needNetworkList=1&random=";

    private static String sFormatUrl = "%s://127.0.0.1:%d/%s?enc=%s&url=%s%s";
    private static String sFormatCacheUrl = HTTP_LOCAL_SERVER_DOMAIN + "%d/play/caches/%s.%s?key=%s%s%s%s%s";

    private static volatile long sCdePort = 0;

    public static String getRtspPlayUrl(String url, String other) {
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_RTSP, sCdePort, REQUEST_STREAM_1, ENC_RAW, getEncodedUrl(url, true), other);
    }

    public static String getPlayUrl(String url, String other) {
        boolean rtStream = url.startsWith(RT_STREAM);
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        if (!rtStream && url.indexOf(M3U8) > 0) {
            other += MEDIA_TYPE_M3U8;
        }
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, rtStream ? REQUEST_STREAM_1FLV : REQUEST_PLAY, rtStream ? ENC_RAW
                : ENC_BASE64, getEncodedUrl(url, rtStream), other);
    }

    public static String getPlayStopUrl(String url, String other) {
        boolean rtStream = url.startsWith(RT_STREAM);
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, REQUEST_PLAY_STOP, rtStream ? ENC_RAW : ENC_BASE64,
                getEncodedUrl(url, rtStream), other);
    }

    public static String getPlayPauseUrl(String url, String other) {
        boolean rtStream = url.startsWith(RT_STREAM);
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, REQUEST_PLAY_PAUSE, rtStream ? ENC_RAW : ENC_BASE64,
                getEncodedUrl(url, rtStream), other);
    }

    public static String getPlayResumeUrl(String url, String other) {
        boolean rtStream = url.startsWith(RT_STREAM);
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, REQUEST_PLAY_RESUMEE, rtStream ? ENC_RAW : ENC_BASE64,
                getEncodedUrl(url, rtStream), other);
    }

    public static String getPlayCacheUrl(long cdePort, String key, String ext, String params, String other) {
        return String.format(sFormatCacheUrl, cdePort, key, !StringUtils.isEmpty(ext) ? ext : M3U8, key,
                !StringUtils.isEmpty(params) || !StringUtils.isEmpty(other) ? "&" : "", !StringUtils.isEmpty(params) ? params : "",
                !StringUtils.isEmpty(params) && !StringUtils.isEmpty(other) ? "&" : "", !StringUtils.isEmpty(other) ? other : "");
    }

    public static String getPlayStateUrl(String url, String other) {
        boolean rtStream = url.startsWith(RT_STREAM);
        other = !StringUtils.isEmpty(other) ? "&" + other : "";
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, REQUEST_STATE_PLAY, rtStream ? ENC_RAW : ENC_BASE64,
                getEncodedUrl(url, rtStream), other);
    }

    // http://127.0.0.1:6991/report/traffic?cde=1&random=0.3196933643988627&styled=1
    // http://127.0.0.1:6991/report/common?cde=1&random=0.6933643931988627&styled=1
    public static String getReportUrl(String url, boolean traffic) {
        boolean rtStream = url.startsWith(RT_STREAM);
        return String.format(sFormatUrl, MediaSource.PROTOCOL_TYPE_HTTP, sCdePort, traffic ? REQUEST_REPORT_TRAFFIC : REQUEST_REPORT_COMMON, rtStream ? ENC_RAW
                : ENC_BASE64, getEncodedUrl(url, rtStream), REPORT_PARAM_OTHER + StringUtils.getRandomNumber());
    }

    public static String getControlUrl(long cdePort, String params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HTTP_LOCAL_SERVER_DOMAIN).append(cdePort);
        stringBuilder.append(REQUEST_CONTROL).append(params);
        return stringBuilder.toString();
    }

    @SuppressWarnings("deprecation")
    public static String getSupportUrl(String phoneNumber) {
        String numberEncode = "";
        if (!StringUtils.isEmpty(phoneNumber)) {
            try {
                numberEncode = URLEncoder.encode(phoneNumber, ENC_UTF8);
            } catch (Exception e) {
                numberEncode = URLEncoder.encode(phoneNumber);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HTTP_LOCAL_SERVER_DOMAIN).append(sCdePort);
        stringBuilder.append(REQUEST_SUPPORT).append(numberEncode);
        return stringBuilder.toString();
    }

    public static String getLogUrl(int limit) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HTTP_LOCAL_SERVER_DOMAIN).append(sCdePort);
        stringBuilder.append(REQUEST_LOG).append(limit);
        return stringBuilder.toString();
    }

    public static void setCdePort(long cdePort) {
        sCdePort = cdePort;
    }

    @SuppressWarnings("deprecation")
    private static String getEncodedUrl(String url, boolean rtStream) {
        if (!rtStream) {
            return Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
        }

        try {
            return URLEncoder.encode(url, ENC_UTF8);
        } catch (Exception e) {
        }
        return URLEncoder.encode(url);
    }
}