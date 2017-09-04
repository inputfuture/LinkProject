package com.letv.leauto.ecolink.leplayer.xiami;

import com.google.gson.annotations.SerializedName;
import com.xiami.sdk.utils.Encryptor;

import java.io.Serializable;

/**
 * Created by shizhao.czc on 2015/5/6.
 */
public class OnlineSong implements Serializable {

    @SerializedName("song_id")
    private long songId;

    @SerializedName("album_id")
    private long albumId;

    @SerializedName("album_name")
    private String albumName;

    @SerializedName("artist_id")
    private long artistId;

    @SerializedName("artist_name")
    private String artistName;

    @SerializedName("album_logo")
    private String albumLogo;

    @SerializedName("artist_logo")
    private String artistLogo;
    private int encodeRate;

    @SerializedName("listen_file")
    private String listenFile = null;

    @SerializedName("lyric")
    private String lyric;

    @SerializedName("song_name")
    private String songName;

    @SerializedName("singers")
    private String singers;

    //����ʱ��
    @SerializedName("length")
    private int length;

    @SerializedName("cd_serial")
    private int cdSerial;

    @SerializedName("track")
    private int track;

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumLogo() {
        return albumLogo;
    }

    public void setAlbumLogo(String albumLogo) {
        this.albumLogo = albumLogo;
    }

    public String getArtistLogo() {
        return artistLogo;
    }

    public void setArtistLogo(String artistLogo) {
        this.artistLogo = artistLogo;
    }

    public int getEncodeRate() {
        return encodeRate;
    }

    public void setEncodeRate(int encodeRate) {
        this.encodeRate = encodeRate;
    }

    public String getListenFile() {
        return Encryptor.decryptUrl(listenFile);
    }

    public void setListenFile(String listenFile) {
        this.listenFile = listenFile;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingers() {
        return singers;
    }

    public void setSingers(String singers) {
        this.singers = singers;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCdSerial() {
        return cdSerial;
    }

    public void setCdSerial(int cdSerial) {
        this.cdSerial = cdSerial;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }
}
