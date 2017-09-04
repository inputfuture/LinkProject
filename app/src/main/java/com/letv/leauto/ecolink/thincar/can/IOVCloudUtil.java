//package com.letv.leauto.ecolink.thincar.can;
//
//import android.util.Log;
//
//import com.ff.iovcloud.domain.HeadUnit;
//import com.ff.iovcloud.domain.MessageType;
//import com.ff.iovcloud.domain.VehicleRegister;
//import com.ff.iovcloud.domain.VehicleSession;
//import com.ff.iovcloud.repository.IOVCloudRepository;
//import com.ff.iovcloud.service.IOVCloudServiceType;
//import com.ff.iovcloud.service.UVMVehicleControlUnitService;
//import com.ff.iovcloud.service.cloudmessage.IOVCloudMessageManager;
//import com.ff.iovcloud.utilities.FFError;
//import com.ff.iovcloud.utilities.FFResult;
//import com.ff.iovcloud.utilities.FFUtils;
//import com.leauto.link.lightcar.module.AVNInfo;
//
///**
// * Created by Jerome on 2017/5/22.
// */
//
//public class IOVCloudUtil {
//
//    private static final String TAG = "IOVCloud";
//    /**
//     * 注册车辆
//     * @param avnInfo
//     */
//    public static void registVehicle(AVNInfo avnInfo) {
//
//        UVMVehicleControlUnitService vehicleControlUnitService = (UVMVehicleControlUnitService) IOVCloudRepository.getInstance().createService(IOVCloudServiceType.UVMVehicleControlUnitService);
//        VehicleRegister.Builder builder = VehicleRegister.newBuilder(avnInfo.getMode(),//model
//                avnInfo.getPartnum(),//partNum
//                avnInfo.getSn(),//serialNum
//                avnInfo.getVin());
//
//        HeadUnit.Builder headUnitBuilder = HeadUnit.newBuilder()
//                .hwVer(avnInfo.getHwver())
//                .swVer(avnInfo.getSwver());
//
//        builder.headUnit(headUnitBuilder.build());
//
//        vehicleControlUnitService.registerVehicle(builder.build(), new UVMVehicleControlUnitService.OnRegisterVehicleCompletedListener() {
//            @Override
//            public void onRegisterVehicleCompletedCompleted(VehicleSession vehicleSession, FFResult ffResult) {
//                if(ffResult.getError() == FFError.NONE){
//                    Trace.Debug(TAG, "vehicleSession,token:"+vehicleSession.getToken());
//                    Trace.Debug(TAG, "vehicleSession,getSession:"+vehicleSession.getSession());
//                }else{
//                    Trace.Debug(TAG, ffResult.getErrorDescription());
//                }
//            }
//        });
//    }
//
//    /**
//     * publish msg
//     */
//    public static void doPublishMessage(final byte[] msgContent){
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //You can send any content you would like to server.
//                FFUtils.logging("publishing message by " + Thread.currentThread().getName());
//                int state = 127; //state value range is 0-255. if
//                // value is 0, it means your app doesn't need ack, otherwise you will get a ack from sever.
//                //MessageType.REMOTE_COMMAND it means which messagetype you would like send. your app can only send these messagetype which are predefined on server. otherwise you will get an permission denied error.
//                IOVCloudMessageManager.getInstance().publish(state, MessageType.REALTIME_GPS_LOCATION_DATA, msgContent, new IOVCloudMessageManager.OnPublishMessageListener() {
//                    @Override
//                    public void onPublishMessageCompleted(FFResult result) {
//                        if (result.getError() == FFError.NONE) {
//                            Trace.Debug(TAG, "publish completed.");
//                        } else {
//                            Trace.Debug(TAG, result.getErrorCode()+"= publish failed. reason:" + result.getErrorDescription());
//                        }
//                    }
//                });
//            }
//        });
//        thread.start();
//    }
//}
