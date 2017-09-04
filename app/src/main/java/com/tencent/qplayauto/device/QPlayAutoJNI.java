package com.tencent.qplayauto.device;

import android.media.AudioFormat;
import android.os.Handler;
import android.util.Log;

import com.letv.leauto.ecolink.utils.Trace;
import com.tencent.qplayauto.device.QPlayAutoArguments.CommandError;
import com.tencent.qplayauto.device.QPlayAutoArguments.ResponseMediaInfos;
import com.tencent.qplayauto.device.QPlayAutoArguments.ResponsePlayList;
import com.tencent.qplayauto.device.QPlayAutoArguments.ResponseSearch;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class QPlayAutoJNI 
{

    
    public final static int MESSAGE_RECEIVE_COMM = 1;//接收到命令
    public final static int MESSAGE_RECEIVE_DATA = 2;//接收到二进制数据
    public final static int MESSAGE_RECEIVE_SONG_ITEMS = 3;//接收到的歌曲列表 
    public final static int MESSAGE_RECEIVE_CONNECT = 4;//接收到的连接信息
    public final static int MESSAGE_RECEIVE_INFOS = 5;//接收到打印信息
    public final static int MESSAGE_RECEIVE_ERROR = 6;//返回错误信息

    
    public final static int MESSAGE_RECEIVE_PLAY_FINISH = 11;//播放完成
	public final static int MESSAGE_RECEIVE_PLAY_BUFF = 12;//缓冲数据
	public final static int MESSAGE_RECEIVE_PLAY_STATE_CHANGE = 13;//播放状态
	
    
	public final static int MESSAGE_INFOS_TYPE_NORMAL = 1;//MESSAGE_RECEIVE_INFOS的类型，普通显示信息
	public final static int MESSAGE_INFOS_TYPE_PLAY_BUFF_SIZE = 2;//MESSAGE_RECEIVE_INFOS的类型，播放缓冲信息
    
    public final static int SONG_ITEM_TYPE_SONG = 1;//Item类型为歌曲
    public final static int SONG_ITEM_TYPE_LIST = 2;//Item类型为目录
    public final static int SONG_ITEM_TYPE_RADIO = 3;//Item类型为电台
	public final static int SONG_TIME_TYPE_SEARCH = 4;//Item类型为搜索歌曲
    
    public final static String SONG_LIST_ROOT_ID = "-1";//歌曲根目录
	public final static String SONG_LIST_SEARCH_ID = "-2";//搜索目录ID
    
    public final static int DEVICE_TYPE_AUTO = 1;//设备类型车机
    
    public final static int BIN_DATA_TYPE_PCM = 1;//二进制数据类型 歌曲PCM
    public final static int BIN_DATA_TYPE_PIC = 2;//二进制数据类型 歌曲专辑图
    public final static int BIN_DATA_TYPE_LRC = 3;//二进制数据类型 歌曲歌词
    
    public final static int CONNECT_TYPE_WIFI = 1;//连接方式为Wifi
	public final static int CONNECT_TYPE_BLUETOOTH = 2;//连接方式为蓝牙
	public final static int CONNECT_TYPE_LINE = 3;//连接方式为数据线
	public final static int CONNECT_TYPE_LOCAL = 4;//连接方式为本地Socket
	
	public final static int CONNECT_STATE_SUCCESS = 0;//连接成功
	public final static int CONNECT_STATE_FAIL = 1;//连接失败
	public final static int CONNECT_STATE_INTERRUPT = 2;//连接断开
	
	public final static int SONG_LYRIC_TYPE_QRC = 0;//歌曲歌词类型为QRC
	public final static int SONG_LYRIC_TYPE_LRC = 1;//歌曲歌词类型为LRC
	public final static int SONG_LYRIC_TYPE_TXT = 2;//歌曲歌词类型为TXT

	public final static int ERROR_NO_ERROR = 0;//调用成功
	public final static int ERROR_ARGUMENTS = -1;//参数错误
	public final static int ERROR_SYSTEM_CALL = -2;//系统调用错误
	public final static int ERROR_WAIT_RESULT_TIMEOUT = -3;//发送查询命令后，等待结果超时
	public final static int ERROR_SEND_COMMAND_OR_RESULT = -4;//发送命令或者结果错误
	public final static int ERROR_SEND_BIN = -5;//发送二进制数据错误
	public final static int ERROR_ALREADY_CONNECT = -6;//已经连接上
	public final static int ERROR_DATA = -7;//数据错误
	public final static int ERROR_NO_INIT = -8;//未初始化

	public final static int ERROR_PROTOCOL_NOT_LOGIN = 110;//用户没有登录


	public final static int JSON_ERROR_PCM_REPEAT = 111;//PCM重复请求

	public static final int COMMAND_GET_PCM	= 101;//获取PCM数据请求
	//public static final int COMMAND_GET_MOBILE_DEVICE_INFOS = 102;//获取移动设备信息
	
	private static Handler mUiMessageHandler = null;//发送信息给UI处理

//	private static Handler mPlayHandler = null;//命令或者结果需要发送到播放线程处理
	
	private static volatile String CurrentPCMSongID = "";//记录当前的播放歌曲ID
	private static String CurrentPICSongID = "";//记录当前歌曲专辑图的ID
	private static String CurrentLyricSongID = "";//记录当前歌曲歌词的ID

	private static int PCMTotalLen = 0;//记录歌曲PCM的总长度
	private static int PCMPackageLen = 0;//本次传输包的长度
	private static volatile int PCMReceiveTotalLen = 0;//记录歌曲PCM接收的总长度
	private static int PCMReceivePackageLen = 0;//本次传输包接收的长度
	public static volatile int PCMPackageIndex = -1;//本次传输包的索引
	
	private static int PICTotalLen = 0;//记录图片还要输送的总长度
	private static int PICPackageLen = 0;//本次传输包的长度
	private static int PICPackageIndex = -1;//本次传输包的索引
	
	private static int LyricTotalLen = 0;//记录图片还要输送的总长度
	private static int LyricPackageLen = 0;//本次传输包的长度
	private static int LyricPackageIndex = 0;//本次传输包的索引
	private static int LyricType = 0;//本次传输歌词的格式 0-QRC  1-LRC  2-TXT
	
	private final static int PCMPlayDataLength = 10*1024;//播放PCM数据包的大小
	public final static int PCM_BUFFER_LENGTH = 1024*1024;//每次QQ音乐发送PCM数据包的长度(不包括Json头长度)
	private static int PCMPlayDataCount = 0;//当前拷贝数据包的大小
	private static byte[] PCMData = null;
	private static boolean InvalidPCMData = false;

	public static final int PLAY_LIST_REQUEST_PRE_COUNT = 10;
	
	public static ConcurrentLinkedQueue<byte[]> PcmQueue = new ConcurrentLinkedQueue<byte[]>();//存储

	private static byte[] PICBitData;
	private static byte[] LyricBitData;
	
		
	static
	{
		try
		{
			System.loadLibrary("QPlayAutoDevice");
		}
		catch (UnsatisfiedLinkError t)
		{
			Trace.Debug("######"+ t.getMessage());
		}
	}
	
//	//发送消息给UI处理
//	public static void SetHandler(Handler uiMessageHandler, Handler playHandler)
//	{
//		mUiMessageHandler = uiMessageHandler;
//		mPlayHandler = playHandler;
//	}
	//
	public static void SetHandler(Handler uiMessageHandler)
	{
		mUiMessageHandler = uiMessageHandler;
	}

	/**
	 * 开始启动连接流程，包括发现设备，连接设备，连接成功后会调用 OnConnectMessage
	 * @param DeviceType 设备类型 1：车机
	 * @param ConnectType 连接类型 CONNECT_TYPE_XXXX常量
	 * @param DeviceBrand 车机品牌
	 * @param DeviceName 车机名称(这个是手机发现车机后显示的名称)
     * @return  < 0 连接失败,失败后，请调用Stop 否则成功
     */
	public native static int Start(int DeviceType, int ConnectType, String DeviceBrand, String DeviceName);

	/**
	 * 停止所有的通讯线程，退出连接,在Start失败，收到连接失败消息后或者自动退出都要调用此函数
	 */
	public native static void Stop();

	/**
	 * 回复手机请求车机的设备信息
	 * @param RequestID
	 * @param DeviceInfos
     * @return
     */
	public native static int ResponseDeviceInfos(int RequestID,QPlayAutoDeviceInfos DeviceInfos);

	/**
	 * 请求手机设备信息
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestMobileDeviceInfos();

	/**
	 * 请求歌曲列表，结果通过 OnResponsePlayList返回
	 * @param ParentID 要请求的歌曲列表的父目录ID ，根目录为:-1
	 * @param PageIndex 歌曲列表分页请求的索引
	 * @param PagePerCount 歌曲列表分页请求中每页多少个歌曲
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestPlayList(String ParentID, int PageIndex, int PagePerCount);

	/**
	 * 请求歌曲PCM 手机通过回调OnReceivePCMData 发送PCM给车机
	 * @param SongID 歌曲ID
	 * @param PackageIndex 歌曲PCM分包的索引号
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestPCMData(String SongID, int PackageIndex);

	/**
	 * 请求歌曲专辑图 手机通过回调OnReceiveAlbumData 发送专辑图给车机
	 * @param SongID 歌曲ID
	 * @param PackageIndex 歌曲专辑图分包的索引号
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestAlbumData(String SongID, int PackageIndex);

	/**
	 * 请求歌曲歌词 手机通过回调OnReceiveLyricData 发送歌词给车机
	 * @param SongID 歌曲ID
	 * @param PackageIndex 歌曲歌词分包的索引号
	 * @param LyricType 歌曲歌词类型 参考SONG_LYRIC_TYPE_XXX定义
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
	 */
	public native static int RequestLyricData(String SongID, int PackageIndex, int LyricType);

	/**
	 * 请求查找歌曲,通过 OnResponseSearch返回结果
	 * @param Key 要查找的关键字(歌曲名，歌手名，专辑图名)
	 * @param PageFlag 要返回分页标识 0:返回第一页 1:返回下一页
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestSearch(String Key, int PageFlag);

	/**
	 * 断开连接
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestDisconnect();

	/**
	 * 请求当前歌曲的播放信息(采样率，声道，采样位数),通过 OnResponseMediaInfo 返回结果
	 * @param SongID 歌曲ID
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestMediaInfo(String SongID);

	/**
	 * 停止发送二进制数据(PCM,专辑图，歌词)
	 * @param SongID 歌曲ID
	 * @param DataType 数据类型 1:PCM 2:专辑图 3:歌词
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestStopSendData(String SongID, int DataType);

	/**
	 * 注册手机播放时返回播放状态
	 * @param AutoTimes 自动发播放状态消息到移动端的时间间隔(秒) 0：移动设备播放状态及播放时间发生变化时通知车机
	 *                     (切歌，快进，快退，暂停，播放，停止) (协议1.2支持)
	 *                     大于0:移动设备会每隔这个值的秒数自动发播放状态给车机
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestRegisterPlayState(int AutoTimes);

	/**
	 * 注销手机播放时返回播放状态
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestUnRegisterPlayState();

	/**
	 * (手表命令)播放歌曲
	 * @param SongID 歌曲ID
	 * @param ParentID 歌曲所在目录的ID
	 * @param Name 歌曲名称
	 * @param Artist 歌手名称
	 * @param Album 专辑名
     * @param Type 歌曲类型 1-普通歌曲   2-电台歌曲(由手表发出没有此项)
     * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestPlaySong(String SongID, String ParentID, String Name, String Artist, String Album, int Type);

	/**
	 * (手表命令)读取手机目前播放状态,通过OnResponsePlayState返回结果
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestPlayState();

	/**
	 * 读取手机网络状态,通过OnResponseNetworkState返回结果
	 * @return <0:调用错误，参考协议  >0:请求的唯一ID
     */
	public native static int RequestNetworkState();



	///////////////////////////////////////////连接消息回调////////////////////////////////////////////////////////////

	/**
	 * 与车机连接状态的回调
	 * @param ConnectStateType 与车机连接的状态,请参考CONNECT_STATE_XXXX  0:连接成功  1:连接失败  2:连接断开
     */
	public static void OnConnectMessage(int ConnectStateType)
	{
		Trace.Debug("######"+"Connect message type:" + ConnectStateType);
		if(mUiMessageHandler == null)
		{
			Trace.Debug("######"+"UI Message handler is null!");
			return ;
		}
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_CONNECT,ConnectStateType,0).sendToTarget();
	}



	/////////////////////////////////////////以下是收到的手机的命令或者结果///////////////////////////////////////////////

	/**
	 * 解析车机返回结果出错回调函数
	 * @param RequestID 请求的ID
	 * @param Command 请求的命令
	 * @param ErrorNo 错误号
	 */
	public static void OnResponseError(int RequestID, String Command, int ErrorNo)
	{
		Trace.Debug("######"+"Response command error, Request ID:" + RequestID + " Command:" + Command + " ErrorNo:" + ErrorNo);
		CommandError error = new CommandError();
		error.Command = Command;
		error.ErrorNo = ErrorNo;
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.RESPONSE_ERROR,RequestID,error).sendToTarget();
	}

	/**
	 * 解析车机命令出错
	 * @param RequestID 请求的ID
	 * @param Command 请求的命令
	 * @param ErrorNo 错误号
	 */
	public static void OnRequestError(int RequestID, String Command, int ErrorNo)
	{
	Trace.Debug("######"+"Request command error, Request ID:" + RequestID + " Command:" + Command + " ErrorNo:" + ErrorNo);
		CommandError error = new CommandError();
		error.Command = Command;
		error.ErrorNo = ErrorNo;
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.REQUEST_ERROR,RequestID,error).sendToTarget();
	}

	/**
	 * 收到手机请求设备信息命令的回调
	 * @param RequestID 请求的ID
     */
	public static void OnRequestDeviceInfos(int RequestID)
	{
		if(mUiMessageHandler == null)
		{
			Trace.Debug("######"+"UI Message handler is null!");
			return ;
		}

		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.REQUEST_DEVICE_INFOS,RequestID).sendToTarget();
	}


	/**
	 * 收到手机信息
	 * @param RequestID 请求的ID
	 * @param Infos 手机信息
     */
	public static void OnResponseMobileDeviceInfos(int RequestID,QPlayAutoMobileDeviceInfos Infos)
	{
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.RESPONSE_MOBILE_DEVICE_INFOS,RequestID,Infos).sendToTarget();
	}

	/**
	 * 收到歌曲列表
	 * @param RequestID 请求的ID
	 * @param Count 这个列表的总歌曲数
	 * @param ParentID 列表的父ID
	 * @param PageIndex 列表分页的索引
     * @param PlayList 列表分页歌曲信息
     */
	public static void OnResponsePlayList(int RequestID, int Count, String ParentID, int PageIndex, QPlayAutoSongListItem[] PlayList)
	{
		if(mUiMessageHandler == null)
		{
			Trace.Debug("######"+"Message handler is null!");
			return ;
		}
		Trace.Debug("######"+"OnResponsePlayList:====");
		ResponsePlayList playList = new ResponsePlayList();
		playList.count = Count;
		playList.parentID = ParentID;
		playList.pageIndex = PageIndex;
		playList.playList = PlayList;
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.RESPONSE_PLAY_LIST,RequestID,playList).sendToTarget();
	}

	/**
	 * 收到歌曲的PCM数据
	 * @param SongID 歌曲ID
	 * @param PackageIndex PCM数据包的索引
	 * @param Length PCM数据长度
	 * @param TotalLength 此歌曲PCM总长度
     * @param Data PCM数据
     */
	public static void OnReceivePCMData(String SongID, int PackageIndex, int Length, int TotalLength, byte[] Data)
	{
		if(PCMPackageIndex != PackageIndex)//接收到一个新的包或者一个新的数据传送
		{
			if( CurrentPCMSongID.equalsIgnoreCase(SongID))
			{
				PCMTotalLen = TotalLength;//当前歌曲总长度
				PCMPackageLen = Length;//当前包总长度
				PCMPackageIndex = PackageIndex;//当前包序号

				PCMReceivePackageLen = 0;

				//Trace.Debug(TAG,"Receive package index:" + PCMPackageIndex);

				String infos = "PCM Data,ID:" + CurrentPCMSongID + " TotalLen:" + PCMTotalLen + "(" + PCMReceiveTotalLen + ") PackageIndex:" + PCMPackageIndex + " PackageLen:" + PCMPackageLen ;
				SendInfo(MESSAGE_INFOS_TYPE_NORMAL,"",infos);
			}
			else
			{
				String infos = "PCM数据传送错误,当前ID:" + CurrentPCMSongID + " 数据ID:" + SongID + " 不一致!" ;
				SendInfo(MESSAGE_INFOS_TYPE_NORMAL,"",infos);
				return;
			}
		}
		else if(CurrentPCMSongID.equals(""))
		{
			String infos = "PCM数据传送完成!多余数据忽略!(本次丢掉" + Data.length + "字节)";
			SendInfo(MESSAGE_INFOS_TYPE_NORMAL,"",infos);
			return;
		}

		if(Data.length == 0 )
			return;

		if(PCMReceiveTotalLen + Data.length > PCMTotalLen)//数据超过歌曲大小，忽略
		{
			if(PCMReceiveTotalLen < PCMTotalLen)
			{
				byte[] UseData = new byte[PCMTotalLen - PCMReceiveTotalLen];
				System.arraycopy(Data, 0, UseData, 0, UseData.length);
				PCMData = CopyPCMDataToPlay(UseData,PCMData);
				if(PCMData != null)
					PcmQueue.offer(PCMData);
				PCMReceiveTotalLen = 0;
				PCMReceivePackageLen = 0;
				CurrentPCMSongID = "";
				PCMData = null;
				PcmQueue.offer(new byte[0]);//播放结束标志
			}
			PCMReceiveTotalLen += Data.length;

			String infos = "PCM Data1,ID:" + CurrentPCMSongID + " TotalLen:" + PCMTotalLen + "(" + PCMReceiveTotalLen + ") PackageIndex:" + PCMPackageIndex + " PackageLen:" + PCMPackageLen + "(" + PCMReceivePackageLen + ")";
			SendInfo(MESSAGE_INFOS_TYPE_NORMAL,"",infos);

			return;
		}

		PCMData = CopyPCMDataToPlay(Data,PCMData);

		PCMReceiveTotalLen += Data.length;
		PCMReceivePackageLen += Data.length;


		String infos = "PCM Data2,ID:" + CurrentPCMSongID + " TotalLen:" + PCMTotalLen + "(" + PCMReceiveTotalLen + ") PackageIndex:" + PCMPackageIndex + " PackageLen:" + PCMPackageLen + "(" + PCMReceivePackageLen + ")";
		SendInfo(MESSAGE_INFOS_TYPE_NORMAL,"",infos);

		if(PCMTotalLen == PCMReceiveTotalLen || //文件接收完成
				(PCMPackageLen < PCM_BUFFER_LENGTH && PCMReceivePackageLen >= PCMPackageLen))//文件没接收完成，但接收到的包的长度没有给定QQ音乐要发送的长度，认为是最后一个包，播放结束
		{
			PCMReceiveTotalLen = 0;
			PCMReceivePackageLen = 0;
			CurrentPCMSongID = "";
			PCMData = null;
			PcmQueue.offer(new byte[0]);//结束标志

		}
	}

	/**
	 * 收到歌曲的专辑图数据
	 * @param SongID 歌曲ID
	 * @param PackageIndex 专辑图数据包的索引
	 * @param Length 专辑图数据长度
	 * @param TotalLength 此歌曲专辑图总长度
	 * @param Data 专辑图数据
	 */
	public static void OnReceiveAlbumData(String SongID, int PackageIndex, int Length, int TotalLength, byte[] Data)
	{
		Trace.Debug("######"+"OnReceiveAlbumData:====正在接收PIC数据");
		if(!CurrentPICSongID.equalsIgnoreCase(SongID))
		{
			//新歌曲
			CurrentPICSongID = SongID;
			PICTotalLen = TotalLength;
			PICPackageLen = Length;

			//Trace.Debug(TAG,"Receive PIC data new Song,songID:" + CurrentPICSongID + " TotalLen:" + PICTotalLen + " PackageLen:" + PICPackageLen + " DataLen:" + Data.length);

			PICBitData = new byte[PICTotalLen];

			PICPackageIndex = 0;
		}
		else
		{
			if(PackageIndex != PICPackageIndex)
			{
				//新的数据包
				PICPackageLen = Length;
			}
		}

		if(Data == null || Data.length == 0 )
			return;

		System.arraycopy(Data, 0, PICBitData, PICBitData.length - PICTotalLen, Data.length);

		PICPackageLen -= Data.length;
		PICTotalLen -= Data.length;

		if(PICTotalLen == 0 )//文件接收完成
		{
			CurrentPICSongID = "";
			PICPackageIndex = -1;
			mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_DATA,BIN_DATA_TYPE_PIC,0,PICBitData).sendToTarget();
		}
		else if(PICPackageLen == 0)//这个包接收完成
		{
			//PICPackageIndex++;
			//Trace.Debug(TAG,"GetPICData--Package Index:" + PICPackageIndex + "->" + QPlayAutoJNI.GetPICData(CurrentPICSongID, PICPackageIndex));
			RequestAlbumData(CurrentPICSongID, PICPackageIndex+1);
		}
		else if(PICPackageLen < 0)
		{
			Trace.Debug("######"+"接收PIC数据出错--需要数据:" + PICPackageLen + Data.length + "实际数据:" + Data.length);
		}
		else if(PICTotalLen < 0)
		{
			Trace.Debug("######"+"接收PIC数据出错--需要总数据:" + PICTotalLen + Data.length + "实际数据:" + Data.length);
		}
	}

	/**
	 * 收到歌曲的歌词数据
	 * @param SongID 歌曲ID
	 * @param PackageIndex 歌词数据包的索引
	 * @param Length 歌词数据长度
	 * @param TotalLength 此歌曲歌词总长度
	 * @param Data 歌词数据
	 */
	public static void OnReceiveLyricData(String SongID, int PackageIndex, int Length, int TotalLength, int LyricType, byte[] Data)
	{
		if(!CurrentLyricSongID.equalsIgnoreCase(SongID))
		{
			//新歌曲
			CurrentLyricSongID = SongID;
			LyricTotalLen = TotalLength;
			LyricPackageLen = Length;

			//Trace.Debug(TAG,"Receive PIC data new Song,songID:" + CurrentLyricSongID + " TotalLen:" + LyricTotalLen + " PackageLen:" + LyricPackageLen + " DataLen:" + Data.length);

			LyricBitData = new byte[LyricTotalLen];

			LyricPackageIndex = 0;
		}
		else
		{
			if(PackageIndex != LyricPackageIndex)
			{
				//新的数据包
				LyricPackageLen = Length;
			}
		}

		if(Data == null || Data.length == 0 )
			return;

		System.arraycopy(Data, 0, LyricBitData, LyricBitData.length - LyricTotalLen, Data.length);

		LyricPackageLen -= Data.length;
		LyricTotalLen -= Data.length;

		if(LyricTotalLen == 0 )//文件接收完成
		{
			CurrentLyricSongID = "";
			LyricPackageIndex = -1;
			mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_DATA,BIN_DATA_TYPE_LRC,LyricType,LyricBitData).sendToTarget();
		}
		else if(LyricPackageLen == 0)//这个包接收完成
		{
			//LyricPackageIndex++;
			//Trace.Debug(TAG,"GetLyricData--Package Index:" + LyricPackageIndex + "->" + RequestLyricData(CurrentLyricSongID, LyricPackageIndex,LyricType));
			RequestLyricData(CurrentLyricSongID, LyricPackageIndex + 1,LyricType);
		}
		else if(LyricPackageLen < 0)
		{
			Trace.Debug("######"+"接收歌词数据出错--需要数据:" + LyricPackageLen + Data.length + "实际数据:" + Data.length);
		}
		else if(LyricTotalLen < 0)
		{
			Trace.Debug("######"+"接收歌词数据出错--需要总数据:" + LyricTotalLen + Data.length + "实际数据:" + Data.length);
		}
	}

	/**
	 * 收到搜索歌曲列表
	 * @param RequestID 请求的ID
	 * @param Key 请求的内容
	 * @param PageFlag 请求页的标识 0：请求第一页 1：请求下一页
	 * @param PlayList 搜索到的歌曲列表
     */
	public static void OnResponseSearch(int RequestID, String Key, int PageFlag, QPlayAutoSongListItem[] PlayList)
	{
		ResponseSearch searchList = new ResponseSearch();
		searchList.key = Key;
		searchList.pageFlag = PageFlag;
		searchList.searchList = PlayList;
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, QPlayAutoArguments.RESPONSE_SEARCH,RequestID,searchList).sendToTarget();
	}

	/**
	 * 收到歌曲信息
	 * @param RequestID 请求的ID
	 * @param SongID 歌曲ID
	 * @param PCMDataLength 歌曲PCM的总长度
	 * @param Rate 采样率
	 * @param Bit 采样位数
     * @param Channel 声道数
     */
	public static void OnResponseMediaInfo(int RequestID, String SongID, int PCMDataLength, int Rate, int Bit, int Channel)
	{
		ResponseMediaInfos mediaInfos = new ResponseMediaInfos();

		mediaInfos.songID = SongID;
		mediaInfos.frequency = Rate;
		mediaInfos.bit = Bit;
		mediaInfos.channel = Channel;
		mediaInfos.PCMTotalLength = PCMDataLength;
		mediaInfos.songDuration = mediaInfos.PCMTotalLength/(mediaInfos.frequency * mediaInfos.channel * mediaInfos.bit / 8);

		if (mediaInfos.bit == 8)
			mediaInfos.bit = AudioFormat.ENCODING_PCM_8BIT;
		else
			mediaInfos.bit = AudioFormat.ENCODING_PCM_16BIT;

		if (mediaInfos.channel == 1)
			mediaInfos.channel = AudioFormat.CHANNEL_IN_MONO;
		else
			mediaInfos.channel = AudioFormat.CHANNEL_IN_STEREO;

		mUiMessageHandler.obtainMessage(QPlayAutoArguments.RESPONSE_MEDIA_INFOS,RequestID,QPlayAutoArguments.RESPONSE_MEDIA_INFOS,mediaInfos).sendToTarget();
	}

	/**
	 * 收到停止传送数据的结果
	 * @param RequestID 请求ID
	 * @param SongID 歌曲ID
	 * @param DataType 数据类型
     * @param Result 是否成功停止 1：成功 0：失败
     */
	public static void OnResponseStopSendData(int RequestID, String SongID, int DataType, int Result)
	{

	}

	/**
	 * 收到注册播放状态是否成功
	 * @param RequestID  请求ID
	 * @param State 返回注册结果  0-注册成功 1-注册失败  2-不支持
     */
	public static void OnResponseRegisterPlayState(int RequestID,int State)
	{

	}

	/**
	 * 收到注销播放状态是否成功
	 * @param RequestID  请求ID
	 * @param State 返回注销结果  0-注销成功 1-注销失败
	 */
	public static void OnResponseUnRegisterPlayState(int RequestID,int State)
	{

	}

	/**
	 * 收到手机播放命令
	 * @param RequestID 请求ID
     */
	public static void OnRequestDevicePlayPlay(int RequestID)
	{

	}

	/**
	 * 收到手机暂停播放命令
	 * @param RequestID 请求ID
     */
	public static void OnRequestDevicePlayPause(int RequestID)
	{

	}

	/**
	 * (手表使用)收到手机播放上一首命令
	 * @param RequestID 请求ID
     */
	public static void OnRequestDevicePlayPre(int RequestID)
	{

	}

	/**
	 * (手表使用)收到手机播放下一首命令
	 * @param RequestID 请求ID
     */
	public static void OnRequestDevicePlayNext(int RequestID)
	{

	}

	/**
	 * 收到手机停止播放命令
	 * @param RequestID 请求ID
     */
	public static void OnRequestDevicePlayStop(int RequestID)
	{

	}

	/**
	 * 收到手机当前播放状态
	 * @param RequestID 请求ID
	 * @param SongID 歌曲ID
	 * @param State 当前播放的状态 0-播放  1-暂停  2-停止
	 * @param Times 当前播放的时间（秒）
     * @param Duration 总的时长（秒）
     */
	public static void OnResponsePlayState(int RequestID, String SongID, int State, int Times, int Duration)
	{

	}

	/**
	 * 收到手机请求播放状态
	 * @param RequestID 请求ID
	 * @param SongID 歌曲ID
	 * @param State 当前播放的状态 0-播放  1-暂停  2-停止
	 * @param Times 当前播放的时间（秒）
	 * @param Duration 总的时长（秒）
	 */
	public static void OnRequestPlayState(int RequestID, String SongID, int State, int Times, int Duration)
	{

	}

	/**
	 * (手表使用）收到手机变更播放列表的命令
	 * @param RequestID 请求ID
	 * @param ParentID 变量播放列表的父ID
     */
	public static void OnRequestPlayListChange(int RequestID,String ParentID)
	{

	}

	/**
	 * 收到请求网络状态的结果
	 * @param RequestID 请求ID
	 * @param State 0:无网线  1:Wifi  2:3G/4G
     */
	public static void OnResponseNetworkState(int RequestID,int State)
	{

	}

	/*
	 * 把接收到数据做成10K大小的数据包，这样做的目的是播放时可以预计什么时候发送PCM数据请求，目前是还剩下1M(100个数据包)数据时发送PCM请求
	 */
	private static byte[] CopyPCMDataToPlay(byte[] SourceData,byte[] PlayData)
	{
		int CopyDataLen = 0;
		int SourceIndex = 0;
		if(PlayData == null)
		{
			PlayData = new byte[PCMPlayDataLength];
			PCMPlayDataCount = 0;
		}
		while(SourceData.length - SourceIndex > PlayData.length - PCMPlayDataCount)
		{
			CopyDataLen = PlayData.length - PCMPlayDataCount;
			System.arraycopy(SourceData, SourceIndex, PlayData, PCMPlayDataCount, CopyDataLen);
			PCMPlayDataCount = 0;
			SourceIndex += CopyDataLen;			
			PcmQueue.offer(PlayData);//加入到播放队列
			PlayData = new byte[PCMPlayDataLength];
		}
		
		CopyDataLen = SourceData.length - SourceIndex;
		System.arraycopy(SourceData, SourceIndex, PlayData, PCMPlayDataCount, CopyDataLen);
		PCMPlayDataCount += CopyDataLen;
		
		if(PCMPlayDataCount == PlayData.length)//刚好一个包数据
		{
			PcmQueue.offer(PlayData);
			PCMPlayDataCount = 0;
			return null;
		}
		else
			return PlayData;		
	}

	/*
	 * 为接收歌曲PCM做准备
	 */
	public static void InitPCMData(String SongID)
	{
		CurrentPCMSongID = SongID;
		PcmQueue.clear();
		PCMPackageIndex = -1;
		PCMReceiveTotalLen = 0;
	}
	
	/*
	函数名	:OnReceiveLyricData
	说明	:接收歌曲表词图数据回调函数,把歌曲歌词数据传给Java层处理。歌曲歌词的数据发送是由车机端调用GetLyricData 根据指定的PackageIndex来发送数据包,一个包可能分几次发送
	参数	:
			HeadFlag 如果为null  说明是同一个包的数据,如果不为null 说明 是一个新包。
			Data:具体的歌曲专辑图数据
	返回值	:无
	*/


	
//	/*
//	函数名	:OnReceiveCommand
//	说明	:通知上层应用接收到车机命令，需要上层应用处理
//	参数	:
//			CmdJson:车机发给移动设备的Json命令
//	返回值	:无
//	*/
//	public static void OnReceiveCommand(HashMap Command)
//	{
//		if(Command.toString().indexOf("Heartbeat") < 0)
//			Trace.Debug(TAG,"Receive Command:" + Command.toString());
//		MessageHandler.obtainMessage(MESSAGE_RECEIVE_COMM, Command).sendToTarget();
//	}
	
	public static void ClearPCMData()
	{
		PcmQueue.clear();
	}
	
	public static void SendInfo(int Type, String tag, String infos)
	{
		Trace.Debug("######"+infos);
		mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_INFOS,Type,0,infos).sendToTarget();
	}

	public static void PrintHashMap(String tag, HashMap Result, String Head)
	{
		if(Result == null)
		{
			Trace.Debug("######"+Head + "--Result is null");
			return;
		}
		Trace.Debug(tag,Head + Result.toString());
	}
	
	public static void PrintList(List list, String Head)
	{
		for(int i = 0;i < list.size();i++)
		{
			PrintHashMap("",(HashMap)list.get(i),Head);
		}
	}
	
	public static void GetSongPicture(String SongID)
	{
		RequestAlbumData(SongID,0);
	}
	
	/*
	函数名	:GetSongList
	说明	:根据父ID读取此ID下的列表
	参数	:
			parentID:要查询的父ID
			Type 2:普通目录   3:电台目录
	返回值	:无
	*/
//	public static void ReadSongList(final String parentID,final int Type)
//	{
//		if(TextUtils.isEmpty(parentID))
//			return;
//		new Thread()
//		{
//			@Override
//			public void run()
//			{
//				int count;//记录列表总数
//				int pageIndex = 0;//当前请求的页序号
//				final int PagePreCount = 10;//每次返回列表的个数(最大不能超过50个)
//				ArrayList SendList = new ArrayList();//记录列表信息
//				ArrayList SongList;//记录每次请求列表的信息
//
//				SendList.add(parentID);//首先加入父ID
//
//				if(parentID.equals("-1")) {
//					HashMap<Object, Object> SearchItem = new HashMap<Object, Object>();
//					SearchItem.put("id", SONG_LIST_SEARCH_ID);
//					SearchItem.put("name", "搜索歌曲");
//					SearchItem.put("key","天空");
//					SearchItem.put("artist","");
//					SearchItem.put("album","");
//					SearchItem.put("type",SONG_ITEM_TYPE_LIST);
//					SendList.add(SearchItem);
//				}
//				do
//				{
//					SongList = new ArrayList();
//					String infos = "Get song list ParentID1:" + parentID + "  pageIndex:" + pageIndex + "  PagePreCount:" + PagePreCount;
//					SendInfo(MESSAGE_INFOS_TYPE_NORMAL,TAG,infos);
//					HashMap hm = QPlayAutoJNI.RequestPlayList(SongList, parentID, pageIndex, PagePreCount);//第一次发命令，
//					if(hm == null)
//					{
//						hm = QPlayAutoJNI.GetSongLists(SongList, parentID, pageIndex, PagePreCount);//第二冷发命令
//						if(hm == null)
//						{
//							infos = "Get song list error!";
//							SendInfo(MESSAGE_INFOS_TYPE_NORMAL,TAG,infos);
//							return;
//						}
//					}
//					Object val = hm.get("count");
//					if(val == null)
//					{
//						Object error = hm.get("error");
//						if(error != null)
//						{
//							mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_ERROR,0,0,error).sendToTarget();//发送整个列表给UI处理
//							return;
//						}
//						else
//							count = 0;
//					}
//					else
//						count = Integer.parseInt(val.toString());
//
//					infos = "Get song list count:" + count + " Items count:" + SongList.size();
//					if(SongList.size() <= 0 && SendList.size() <= 0) {
//						SendInfo(MESSAGE_INFOS_TYPE_NORMAL,TAG,"没有发现歌曲列表!");
//						return;
//					}
//					SendInfo(MESSAGE_INFOS_TYPE_NORMAL,TAG,infos);
//
//					SendList.addAll(SongList);
//					pageIndex++;
//				}while(SendList.size() < count);
//
//				mUiMessageHandler.obtainMessage(MESSAGE_RECEIVE_SONG_ITEMS,Type,0,SendList).sendToTarget();//发送整个列表给UI处理
//			}
//		}.start();
//
//	}

	
	public static void StopPlay()
	{
		if(!CurrentPICSongID.equals(""))
			//StopSendData(CurrentPICSongID, 1);
			RequestStopSendData(CurrentPICSongID, 1);
		//PcmQueue.clear();
		//PcmQueue.offer(new byte[0]);//结束标志
	}
	
}


