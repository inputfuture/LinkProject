//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.amap.api.col.ea;
import com.amap.api.col.eb;
import com.amap.api.col.ej;
import com.amap.api.col.ha;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrow;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNaviException;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.letv.leauto.ecolink.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class EcoRouteOverLay {
    private Bitmap startBitmap;
    private Bitmap endBitmap;
    private Bitmap wayBitmap;
    private BitmapDescriptor startBitmapDescriptor;
    private BitmapDescriptor endBitmapDescriptor;
    private BitmapDescriptor wayPointBitmapDescriptor;
    private Marker startMarker;
    private List<Marker> wayMarkers;
    private Marker endMarker;
    private BitmapDescriptor arrowOnRoute = null;
    private BitmapDescriptor normalRoute = null;
    private BitmapDescriptor unknownTraffic = null;
    private BitmapDescriptor smoothTraffic = null;
    private BitmapDescriptor slowTraffic = null;
    private BitmapDescriptor jamTraffic = null;
    private BitmapDescriptor veryJamTraffic = null;
    private List<Polyline> mTrafficColorfulPolylines = new ArrayList();
    private RouteOverlayOptions mRouteOverlayOptions = null;
    private float mWidth = 40.0F;
    private AMapNaviPath mAMapNaviPath = null;
    private Polyline mDefaultPolyline;
    private AMap aMap;
    private Context mContext;
    private List<LatLng> mLatLngsOfPath;
    private Polyline guideLink = null;
    private List<Circle> gpsCircles = null;
    private boolean emulateGPSLocationVisibility = true;
    private NavigateArrow naviArrow = null;
    private boolean isTrafficLine = true;
    private List<Polyline> mCustomPolylines = new ArrayList();
    private int arrowColor = Color.parseColor("#4DF6CC");
    private ArrayList<String> wayPointStrings;
    private int[] wayPointId=new int[]{R.mipmap.way_point_1,R.mipmap.way_point_2,R.mipmap.way_point_3};

    public EcoRouteOverLay(AMap var1, AMapNaviPath var2, Context var3) {
        this.mContext = var3;
        this.mWidth = (float) ea.a(var3, 22);
        this.init(var1, var2);
    }

    public float getWidth() {
        return this.mWidth;
    }

    /** @deprecated */
    public void setWidth(float var1) throws AMapNaviException {
        if(var1 > 0.0F) {
            this.mWidth = var1;
        }
    }

    public RouteOverlayOptions getRouteOverlayOptions() {
        return this.mRouteOverlayOptions;
    }

    public void setRouteOverlayOptions(RouteOverlayOptions overlayOptions) {
        this.mRouteOverlayOptions = overlayOptions;
        if(overlayOptions != null && overlayOptions.getNormalRoute() != null) {
            this.normalRoute = BitmapDescriptorFactory.fromBitmap(overlayOptions.getNormalRoute());
        }

        if(overlayOptions != null && overlayOptions.getArrowOnTrafficRoute() != null) {
            this.arrowOnRoute = BitmapDescriptorFactory.fromBitmap(overlayOptions.getArrowOnTrafficRoute());
        }

        if(overlayOptions != null && overlayOptions.getUnknownTraffic() != null) {
            this.unknownTraffic = BitmapDescriptorFactory.fromBitmap(overlayOptions.getUnknownTraffic());
        }

        if(overlayOptions != null && overlayOptions.getSmoothTraffic() != null) {
            this.smoothTraffic = BitmapDescriptorFactory.fromBitmap(overlayOptions.getSmoothTraffic());
        }

        if(overlayOptions != null && overlayOptions.getSlowTraffic() != null) {
            this.slowTraffic = BitmapDescriptorFactory.fromBitmap(overlayOptions.getSlowTraffic());
        }

        if(overlayOptions != null && overlayOptions.getJamTraffic() != null) {
            this.jamTraffic = BitmapDescriptorFactory.fromBitmap(overlayOptions.getJamTraffic());
        }

        if(overlayOptions != null && overlayOptions.getVeryJamTraffic() != null) {
            this.veryJamTraffic = BitmapDescriptorFactory.fromBitmap(overlayOptions.getVeryJamTraffic());
        }

        if(overlayOptions != null && overlayOptions.getLineWidth() > 0.0F) {
            this.mWidth = overlayOptions.getLineWidth();
        }

        if(overlayOptions != null && overlayOptions.getArrowColor() != this.arrowColor) {
            this.arrowColor = overlayOptions.getArrowColor();
        }

    }

    public AMapNaviPath getAMapNaviPath() {
        return this.mAMapNaviPath;
    }

    public void setAMapNaviPath(AMapNaviPath var1) {
        this.mAMapNaviPath = var1;
    }

    /** @deprecated */
    @Deprecated
    public void setRouteInfo(AMapNaviPath var1) {
        this.mAMapNaviPath = var1;
    }

    private void init(AMap aMap, AMapNaviPath naviPath) {
        try {
            this.aMap = aMap;
            this.mAMapNaviPath = naviPath;
            this.normalRoute = BitmapDescriptorFactory.fromAsset("custtexture.png");
        } catch (Throwable var4) {
            ea.a(var4);
            ha.b(var4, "RouteOverLay", "init(AMap amap, AMapNaviPath aMapNaviPath)");
        }

        this.arrowOnRoute = BitmapDescriptorFactory.fromAsset("custtexture_aolr.png");
        this.smoothTraffic = BitmapDescriptorFactory.fromAsset("custtexture_green.png");
        this.unknownTraffic = BitmapDescriptorFactory.fromAsset("custtexture_no.png");
        this.slowTraffic = BitmapDescriptorFactory.fromAsset("custtexture_slow.png");
        this.jamTraffic = BitmapDescriptorFactory.fromAsset("custtexture_bad.png");
        this.veryJamTraffic = BitmapDescriptorFactory.fromAsset("custtexture_grayred.png");
    }

    public void addToMap() {
        try {
            if(this.aMap == null) {
                return;
            }

            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.remove();
                this.mDefaultPolyline = null;
            }

            if(this.mWidth == 0.0F || this.mAMapNaviPath == null) {
                return;
            }

            if(this.naviArrow != null) {
                this.naviArrow.setVisible(false);
            }

            List<NaviLatLng> naviLatLngs = null;
            naviLatLngs = this.mAMapNaviPath.getCoordList();
            if(naviLatLngs == null) {
                return;
            }

            int pointSize = naviLatLngs.size();
            this.mLatLngsOfPath = new ArrayList(pointSize);
            Iterator naviLatLngIterator = naviLatLngs.iterator();

            while(naviLatLngIterator.hasNext()) {
                NaviLatLng naviLatLng = (NaviLatLng)naviLatLngIterator.next();
                LatLng latLng = new LatLng(naviLatLng.getLatitude(), naviLatLng.getLongitude(), false);
                this.mLatLngsOfPath.add(latLng);
            }

            if(this.mLatLngsOfPath.size() == 0) {
                return;
            }

            this.clearTrafficLineAndInvisibleOriginalLine();
            this.mDefaultPolyline = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).setCustomTexture(this.normalRoute).width(this.mWidth));
            this.mDefaultPolyline.setVisible(true);
            LatLng startLatLng = null;
            LatLng endLatLng = null;
            List<NaviLatLng> wayLatLngs = null;
            if(this.mAMapNaviPath.getStartPoint() != null && this.mAMapNaviPath.getEndPoint() != null) {
                startLatLng = new LatLng(this.mAMapNaviPath.getStartPoint().getLatitude(), this.mAMapNaviPath.getStartPoint().getLongitude());
                endLatLng = new LatLng(this.mAMapNaviPath.getEndPoint().getLatitude(), this.mAMapNaviPath.getEndPoint().getLongitude());
                wayLatLngs = this.mAMapNaviPath.getWayPoint();
            }

            if(this.startMarker != null) {
                this.startMarker.remove();
                this.startMarker = null;
            }

            if(this.endMarker != null) {
                this.endMarker.remove();
                this.endMarker = null;
            }


            if(this.wayMarkers != null && this.wayMarkers.size() > 0) {
                for(int i = 0; i < this.wayMarkers.size(); ++i) {
                    Marker marker = (Marker)this.wayMarkers.get(i);
                    if(marker != null) {
                        marker.remove();
                        marker = null;
                    }
                }
            }
            int wayPointSize;
            if(this.startBitmap == null) {
                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(startLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(eb.a(), 1191313534))));
            } else if(this.startBitmapDescriptor != null) {
                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(startLatLng).icon(this.startBitmapDescriptor));
            }

            if(wayLatLngs != null && wayLatLngs.size() > 0) {
                wayPointSize = wayLatLngs.size();
                if(this.wayMarkers == null) {
                    this.wayMarkers = new ArrayList(wayPointSize);
                }

                Marker wayPointMarker;
                if (wayPointSize==1){
                    LatLng wayLatLng = new LatLng(wayLatLngs.get(0).getLatitude(), wayLatLngs.get(0).getLongitude());
                    wayPointMarker = null;
                    if(this.wayBitmap == null) {
                        wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.way_point_single))).title("0").snippet(wayPointStrings.get(0)));
                    } else if(this.wayPointBitmapDescriptor != null) {
                        wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(this.wayPointBitmapDescriptor).title("0").snippet(wayPointStrings.get(0)));
                    }
                    this.wayMarkers.add(wayPointMarker);
                }else{
                    for (int i = 0; i < wayLatLngs.size(); i++) {
                        NaviLatLng wayNaviLatLng=wayLatLngs.get(i);
                        LatLng wayLatLng = new LatLng(wayNaviLatLng.getLatitude(), wayNaviLatLng.getLongitude());
                        wayPointMarker = null;
                        if(this.wayBitmap == null) {
                            wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(mContext.getResources(),wayPointId[i]))).title(""+i).snippet(wayPointStrings.get(i)));
                        } else if(this.wayPointBitmapDescriptor != null) {
                            wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(this.wayPointBitmapDescriptor).title(""+i).snippet(wayPointStrings.get(i)));
                        }
                        this.wayMarkers.add(wayPointMarker);
                    }
                }
//                for(Iterator wayLatlngIterator = wayLatLngs.iterator(); wayLatlngIterator.hasNext(); this.wayMarkers.add(wayPointMarker)) {
//                    NaviLatLng wayNaviLatLng = (NaviLatLng)wayLatlngIterator.next();
//                    LatLng wayLatLng = new LatLng(wayNaviLatLng.getLatitude(), wayNaviLatLng.getLongitude());
//                    wayPointMarker = null;
//                    if(this.wayBitmap == null) {
//                        wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(ej.a(), 1191313535))).title("0").snippet(wayPointStrings.get(0)));
//                    } else if(this.wayPointBitmapDescriptor != null) {
//                        wayPointMarker = this.aMap.addMarker((new MarkerOptions()).position(wayLatLng).icon(this.wayPointBitmapDescriptor).title("0").snippet(wayPointStrings.get(0)));
//                    }
//                }
            }

            if(this.endBitmap == null) {
                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(endLatLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(eb.a(), 1191313426))));
            } else if(this.endBitmapDescriptor != null) {
                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(endLatLng).icon(this.endBitmapDescriptor));
            }

            if(this.isTrafficLine) {
                this.setTrafficLine(Boolean.valueOf(this.isTrafficLine));
            }
        } catch (Throwable var11) {
            ea.a(var11);
            ha.b(var11, "RouteOverLay", "addToMap()");
        }

    }

    public void drawGuideLink(LatLng startLatLng, LatLng endLatLng, boolean show) {
        if(show) {
            ArrayList latlngs = new ArrayList(2);
            latlngs.add(startLatLng);
            latlngs.add(endLatLng);
            if(this.guideLink == null) {
                this.guideLink = this.aMap.addPolyline((new PolylineOptions()).addAll(latlngs).width(this.mWidth / 3.0F).setDottedLine(true));
            } else {
                this.guideLink.setPoints(latlngs);
            }

            this.guideLink.setVisible(true);
        } else if(this.guideLink != null) {
            this.guideLink.setVisible(false);
        }

    }

    public void drawEmulateGPSLocation(Vector<String> var1) {
        try {
            Iterator var2;
            if(this.gpsCircles == null) {
                this.gpsCircles = new ArrayList(var1.size());
            } else {
                var2 = this.gpsCircles.iterator();

                while(true) {
                    if(!var2.hasNext()) {
                        this.gpsCircles.clear();
                        break;
                    }

                    Circle var3 = (Circle)var2.next();
                    var3.remove();
                }
            }

            var2 = var1.iterator();

            while(var2.hasNext()) {
                String var8 = (String)var2.next();
                String[] var4 = var8.split(",");
                if(var4 != null && var4.length >= 11) {
                    LatLng var5 = new LatLng(Double.parseDouble(var4[0]), Double.parseDouble(var4[1]));
                    Circle var6 = this.aMap.addCircle((new CircleOptions()).center(var5).radius(1.5D).strokeWidth(0.0F).fillColor(-65536));
                    this.gpsCircles.add(var6);
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
            ha.b(var7, "RouteOverLay", "drawEmulateGPSLocation(Vector<String> gpsData)");
        }

    }

    public void setEmulateGPSLocationVisible() {
        if(this.gpsCircles != null) {
            this.emulateGPSLocationVisibility = !this.emulateGPSLocationVisibility;
            Iterator var1 = this.gpsCircles.iterator();

            while(var1.hasNext()) {
                Circle var2 = (Circle)var1.next();
                var2.setVisible(this.emulateGPSLocationVisibility);
            }
        }

    }

    public void setStartPointBitmap(Bitmap bitmap) {
        this.startBitmap = bitmap;
        if(this.startBitmap != null) {
            this.startBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.startBitmap);
        }

    }

    public void setWayPointBitmap(Bitmap bitmap) {
        this.wayBitmap = bitmap;
        if(this.wayBitmap != null) {
            this.wayPointBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.wayBitmap);
        }

    }

    public void setEndPointBitmap(Bitmap var1) {
        this.endBitmap = var1;
        if(this.endBitmap != null) {
            this.endBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(this.endBitmap);
        }

    }

    public void removeFromMap() {
        try {
            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setVisible(false);
            }

            if(this.startMarker != null) {
                this.startMarker.setVisible(false);
            }

            Iterator var1;
            if(this.wayMarkers != null) {
                var1 = this.wayMarkers.iterator();

                while(var1.hasNext()) {
                    Marker var2 = (Marker)var1.next();
                    var2.setVisible(false);
                }
            }

            if(this.endMarker != null) {
                this.endMarker.setVisible(false);
            }

            if(this.naviArrow != null) {
                this.naviArrow.remove();
            }

            if(this.guideLink != null) {
                this.guideLink.setVisible(false);
            }

            if(this.gpsCircles != null) {
                var1 = this.gpsCircles.iterator();

                while(var1.hasNext()) {
                    Circle var4 = (Circle)var1.next();
                    var4.setVisible(false);
                }
            }

            this.clearTrafficLineAndInvisibleOriginalLine();
        } catch (Throwable var3) {
            ea.a(var3);
            ha.b(var3, "RouteOverLay", "removeFromMap()");
        }

    }

    private void clearTrafficLineAndInvisibleOriginalLine() {
        if(this.mTrafficColorfulPolylines.size() > 0) {
            for(int i = 0; i < this.mTrafficColorfulPolylines.size(); ++i) {
                if(this.mTrafficColorfulPolylines.get(i) != null) {
                    ((Polyline)this.mTrafficColorfulPolylines.get(i)).remove();
                }
            }
        }

        this.mTrafficColorfulPolylines.clear();
        if(this.mDefaultPolyline != null) {
            this.mDefaultPolyline.setVisible(false);
        }

        if(this.mCustomPolylines.size() > 0) {
            for(int i = 0; i < this.mCustomPolylines.size(); ++i) {
                if(this.mCustomPolylines.get(i) != null) {
                    ((Polyline)this.mCustomPolylines.get(i)).setVisible(false);
                }
            }
        }

    }

    private void colorWayUpdate(List<AMapTrafficStatus> trafficStatuses) {
        if(this.aMap != null) {
            if(this.mLatLngsOfPath != null && this.mLatLngsOfPath.size() > 0) {
                if(trafficStatuses != null && trafficStatuses.size() > 0) {
                    this.clearTrafficLineAndInvisibleOriginalLine();
                    int var2 = 0;
                    LatLng startLatlng = (LatLng)this.mLatLngsOfPath.get(0);
                    LatLng endLatlng = null;
                    double var5 = 0.0D;
                    ArrayList var8 = new ArrayList();
                    Polyline var9 = null;

                    for(int i = 0; i < this.mLatLngsOfPath.size() && var2 < trafficStatuses.size(); ++i) {
                        AMapTrafficStatus trafficStatus = (AMapTrafficStatus)trafficStatuses.get(var2);
                        endLatlng = (LatLng)this.mLatLngsOfPath.get(i);
                        NaviLatLng var11 = new NaviLatLng(startLatlng.latitude, startLatlng.longitude);
                        NaviLatLng var12 = new NaviLatLng(endLatlng.latitude, endLatlng.longitude);
                        double var13 = (double)ea.a(var11, var12);
                        var5 += var13;
                        if(var5 > (double)(trafficStatus.getLength() + 1)) {
                            double var15 = var13 - (var5 - (double)trafficStatus.getLength());
                            NaviLatLng var17 = ea.a(var11, var12, var15);
                            LatLng var18 = new LatLng(var17.getLatitude(), var17.getLongitude());
                            var8.add(var18);
                            startLatlng = var18;
                            --i;
                        } else {
                            var8.add(endLatlng);
                            startLatlng = endLatlng;
                        }

                        if(var5 >= (double)trafficStatus.getLength() || i == this.mLatLngsOfPath.size() - 1) {
                            if(var2 == trafficStatuses.size() - 1 && i < this.mLatLngsOfPath.size() - 1) {
                                ++i;

                                while(i < this.mLatLngsOfPath.size()) {
                                    LatLng var19 = (LatLng)this.mLatLngsOfPath.get(i);
                                    var8.add(var19);
                                    ++i;
                                }
                            }

                            ++var2;
                            switch(trafficStatus.getStatus()) {
                                case 0:
                                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(var8).width(this.mWidth).setCustomTexture(this.unknownTraffic));
                                    break;
                                case 1:
                                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(var8).width(this.mWidth).setCustomTexture(this.smoothTraffic));
                                    break;
                                case 2:
                                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(var8).width(this.mWidth).setCustomTexture(this.slowTraffic));
                                    break;
                                case 3:
                                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(var8).width(this.mWidth).setCustomTexture(this.jamTraffic));
                                    break;
                                case 4:
                                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(var8).width(this.mWidth).setCustomTexture(this.veryJamTraffic));
                            }

                            this.mTrafficColorfulPolylines.add(var9);
                            var8.clear();
                            var8.add(startLatlng);
                            var5 = 0.0D;
                        }
                    }

                    var9 = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).width(this.mWidth).setCustomTexture(this.arrowOnRoute));
                    this.mTrafficColorfulPolylines.add(var9);
                }
            }
        }
    }

    public void zoomToSpan() {
        this.zoomToSpan(100);
    }

    public void zoomToSpan(int var1) {
        try {
            if(this.mAMapNaviPath == null) {
                return;
            }

            CameraUpdate var2 = CameraUpdateFactory.newLatLngBounds(this.mAMapNaviPath.getBoundsForPath(), var1);
            this.aMap.animateCamera(var2, 1000L, (CancelableCallback)null);
        } catch (Throwable var3) {
            ea.a(var3);
            ha.b(var3, "RouteOverLay", "zoomToSpan()");
        }

    }

    public void zoomToSpan(int var1, AMapNaviPath var2) {
        try {
            if(var2 == null) {
                return;
            }

            CameraUpdate var3 = CameraUpdateFactory.newLatLngBounds(var2.getBoundsForPath(), var1);
            this.aMap.animateCamera(var3, 1000L, (CancelableCallback)null);
        } catch (Throwable var4) {
            ea.a(var4);
            ha.b(var4, "RouteOverLay", "zoomToSpan()");
        }

    }

    public void destroy() {
        try {
            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.remove();
            }

            this.mAMapNaviPath = null;
            if(this.arrowOnRoute != null) {
                this.arrowOnRoute.recycle();
            }

            if(this.smoothTraffic != null) {
                this.smoothTraffic.recycle();
            }

            if(this.unknownTraffic != null) {
                this.unknownTraffic.recycle();
            }

            if(this.slowTraffic != null) {
                this.slowTraffic.recycle();
            }

            if(this.jamTraffic != null) {
                this.jamTraffic.recycle();
            }

            if(this.veryJamTraffic != null) {
                this.veryJamTraffic.recycle();
            }

            if(this.startBitmap != null) {
                this.startBitmap.recycle();
            }

            if(this.endBitmap != null) {
                this.endBitmap.recycle();
            }

            if(this.wayBitmap != null) {
                this.wayBitmap.recycle();
            }
        } catch (Throwable var2) {
            ea.a(var2);
            ha.b(var2, "RouteOverLay", "destroy()");
        }

    }

    public void drawArrow(List<NaviLatLng> var1) {
        try {
            if(var1 == null) {
                this.naviArrow.setVisible(false);
                return;
            }

            int var2 = var1.size();
            ArrayList var3 = new ArrayList(var2);
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
                NaviLatLng var5 = (NaviLatLng)var4.next();
                LatLng var6 = new LatLng(var5.getLatitude(), var5.getLongitude(), false);
                var3.add(var6);
            }

            if(this.naviArrow == null) {
                this.naviArrow = this.aMap.addNavigateArrow((new NavigateArrowOptions()).addAll(var3).topColor(this.arrowColor).width(this.mWidth * 0.4F));
            } else {
                this.naviArrow.setPoints(var3);
            }

            this.naviArrow.setZIndex(1.0F);
            this.naviArrow.setVisible(true);
        } catch (Throwable var7) {
            var7.printStackTrace();
            ha.b(var7, "RouteOverLay", "drawArrow(List<NaviLatLng> list) ");
        }

    }

    public List<NaviLatLng> getArrowPoints(int var1) {
        if(this.mAMapNaviPath == null) {
            return null;
        } else {
            try {
                if(var1 >= this.mAMapNaviPath.getStepsCount()) {
                    return null;
                }

                List var2 = this.mAMapNaviPath.getCoordList();
                int var3 = var2.size();
                List var4 = this.mAMapNaviPath.getSteps();
                AMapNaviStep var5 = (AMapNaviStep)var4.get(var1);
                int var6 = var5.getEndIndex();
                NaviLatLng var7 = (NaviLatLng)var2.get(var6);
                Vector var8 = new Vector();
                NaviLatLng var9 = var7;
                int var10 = 0;
                byte var11 = 50;

                int var12;
                NaviLatLng var13;
                int var14;
                NaviLatLng var15;
                for(var12 = var6 - 1; var12 >= 0; --var12) {
                    var13 = (NaviLatLng)var2.get(var12);
                    var14 = ea.a(var9, var13);
                    var10 += var14;
                    if(var10 >= var11) {
                        var15 = ea.a(var9, var13, (double)(var11 + var14 - var10));
                        var8.add(var15);
                        break;
                    }

                    var9 = var13;
                    var8.add(var13);
                }

                Collections.reverse(var8);
                var8.add(var7);
                var10 = 0;
                var9 = var7;

                for(var12 = var6 + 1; var12 < var3; ++var12) {
                    var13 = (NaviLatLng)var2.get(var12);
                    var14 = ea.a(var9, var13);
                    var10 += var14;
                    if(var10 >= var11) {
                        var15 = ea.a(var9, var13, (double)(var11 + var14 - var10));
                        var8.add(var15);
                        break;
                    }

                    var9 = var13;
                    var8.add(var13);
                }

                if(var8.size() > 2) {
                    return var8;
                }
            } catch (Exception var16) {
                var16.printStackTrace();
                ha.b(var16, "RouteOverLay", "getArrowPoints(int roadIndex)");
            }

            return null;
        }
    }

    public boolean isTrafficLine() {
        return this.isTrafficLine;
    }

    public void setTrafficLine(Boolean var1) {
        try {
            if(this.mContext == null) {
                return;
            }

            this.isTrafficLine = var1.booleanValue();
            List var2 = null;
            this.clearTrafficLineAndInvisibleOriginalLine();
            if(this.isTrafficLine) {
                if(this.mAMapNaviPath != null) {
                    var2 = this.mAMapNaviPath.getTrafficStatuses();
                }

                if(var2 != null && var2.size() != 0) {
                    this.colorWayUpdate(var2);
                } else {
                    this.NoTrafficStatusDisplay();
                }
            } else {
                this.NoTrafficStatusDisplay();
            }
        } catch (Throwable var3) {
            var3.printStackTrace();
            ha.b(var3, "RouteOverLay", "setTrafficLine(Boolean enabled)");
        }

    }

    private void NoTrafficStatusDisplay() {
        if(this.mDefaultPolyline != null) {
            this.mDefaultPolyline.setVisible(true);
        }

        if(this.mCustomPolylines.size() > 0) {
            for(int var1 = 0; var1 < this.mCustomPolylines.size(); ++var1) {
                if(this.mCustomPolylines.get(var1) != null) {
                    ((Polyline)this.mCustomPolylines.get(var1)).setVisible(true);
                }
            }
        }

    }

    private void addToMap(int[] var1, int[] var2, BitmapDescriptor[] var3) {
        try {
            if(this.aMap == null) {
                return;
            }

            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.remove();
                this.mDefaultPolyline = null;
            }

            if(this.mWidth == 0.0F || this.mAMapNaviPath == null || this.normalRoute == null) {
                return;
            }

            if(this.naviArrow != null) {
                this.naviArrow.setVisible(false);
            }

            List var4 = this.mAMapNaviPath.getCoordList();
            if(var4 == null) {
                return;
            }

            this.clearTrafficLineAndInvisibleOriginalLine();
            int var5 = var4.size();
            this.mLatLngsOfPath = new ArrayList(var5);
            ArrayList var6 = new ArrayList();
            int var7 = 0;
            boolean var9 = false;
            int var19;
            if(var1 == null) {
                var19 = var3.length;
            } else {
                var19 = var1.length;
            }

            Polyline var8;
            for(int var10 = 0; var10 < var19; ++var10) {
                if(var2 == null || var10 >= var2.length || var2[var10] > 0) {
                    var6.clear();

                    while(var7 < var4.size()) {
                        NaviLatLng var11 = (NaviLatLng)var4.get(var7);
                        LatLng var12 = new LatLng(var11.getLatitude(), var11.getLongitude(), false);
                        this.mLatLngsOfPath.add(var12);
                        var6.add(var12);
                        if(var2 != null && var10 < var2.length && var7 == var2[var10]) {
                            break;
                        }

                        ++var7;
                    }

                    if(var3 != null && var3.length != 0) {
                        var8 = this.aMap.addPolyline((new PolylineOptions()).addAll(var6).setCustomTexture(var3[var10]).width(this.mWidth));
                    } else {
                        var8 = this.aMap.addPolyline((new PolylineOptions()).addAll(var6).color(var1[var10]).width(this.mWidth));
                    }

                    var8.setVisible(true);
                    this.mCustomPolylines.add(var8);
                }
            }

            var8 = this.aMap.addPolyline((new PolylineOptions()).addAll(this.mLatLngsOfPath).width(this.mWidth).setCustomTexture(this.arrowOnRoute));
            this.mCustomPolylines.add(var8);
            LatLng var20 = null;
            LatLng var21 = null;
            List var22 = null;
            if(this.mAMapNaviPath.getStartPoint() != null && this.mAMapNaviPath.getEndPoint() != null) {
                var20 = new LatLng(this.mAMapNaviPath.getStartPoint().getLatitude(), this.mAMapNaviPath.getStartPoint().getLongitude());
                var21 = new LatLng(this.mAMapNaviPath.getEndPoint().getLatitude(), this.mAMapNaviPath.getEndPoint().getLongitude());
                var22 = this.mAMapNaviPath.getWayPoint();
            }

            if(this.startMarker != null) {
                this.startMarker.remove();
                this.startMarker = null;
            }

            if(this.endMarker != null) {
                this.endMarker.remove();
                this.endMarker = null;
            }

            int var13;
            if(this.wayMarkers != null && this.wayMarkers.size() > 0) {
                for(var13 = 0; var13 < this.wayMarkers.size(); ++var13) {
                    Marker var14 = (Marker)this.wayMarkers.get(var13);
                    if(var14 != null) {
                        var14.remove();
                        var14 = null;
                    }
                }
            }

            if(this.startBitmap == null) {
                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var20).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(eb.a(), 1191313534))));
            } else if(this.startBitmapDescriptor != null) {
                this.startMarker = this.aMap.addMarker((new MarkerOptions()).position(var20).icon(this.startBitmapDescriptor));
            }

            if(var22 != null && var22.size() > 0) {
                var13 = var22.size();
                if(this.wayMarkers == null) {
                    this.wayMarkers = new ArrayList(var13);
                }

                Marker var17;
                for(Iterator var23 = var22.iterator(); var23.hasNext(); this.wayMarkers.add(var17)) {
                    NaviLatLng var15 = (NaviLatLng)var23.next();
                    LatLng var16 = new LatLng(var15.getLatitude(), var15.getLongitude());
                    var17 = null;
                    if(this.wayBitmap == null) {
                        var17 = this.aMap.addMarker((new MarkerOptions()).position(var16).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(eb.a(), 1191313542))));
                    } else if(this.wayPointBitmapDescriptor != null) {
                        var17 = this.aMap.addMarker((new MarkerOptions()).position(var16).icon(this.wayPointBitmapDescriptor));
                    }
                }
            }

            if(this.endBitmap == null) {
                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var21).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(eb.a(), 1191313426))));
            } else if(this.endBitmapDescriptor != null) {
                this.endMarker = this.aMap.addMarker((new MarkerOptions()).position(var21).icon(this.endBitmapDescriptor));
            }

            if(this.isTrafficLine) {
                this.setTrafficLine(Boolean.valueOf(this.isTrafficLine));
            }
        } catch (Throwable var18) {
            ea.a(var18);
            ha.b(var18, "RouteOverLay", "addToMap(int[] color, int[] index, BitmapDescriptor[] resourceArray)");
        }

    }

    public void addToMap(int[] var1, int[] var2) {
        if(var1 != null && var1.length != 0) {
            this.addToMap(var1, var2, (BitmapDescriptor[])null);
        }
    }

    public void addToMap(BitmapDescriptor[] var1, int[] var2) {
        if(var1 != null && var1.length != 0) {
            this.addToMap((int[])null, var2, var1);
        }
    }

    public void setTransparency(float var1) {
        if(var1 < 0.0F) {
            var1 = 0.0F;
        } else if(var1 > 1.0F) {
            var1 = 1.0F;
        }

        if(this.mDefaultPolyline != null) {
            this.mDefaultPolyline.setTransparency(var1);
        }

        Iterator var2 = this.mTrafficColorfulPolylines.iterator();

        while(var2.hasNext()) {
            Polyline var3 = (Polyline)var2.next();
            var3.setTransparency(var1);
        }

    }

    public void setZindex(int var1) {
        try {
            if(this.mTrafficColorfulPolylines != null) {
                for(int var2 = 0; var2 < this.mTrafficColorfulPolylines.size(); ++var2) {
                    ((Polyline)this.mTrafficColorfulPolylines.get(var2)).setZIndex((float)var1);
                }
            }

            if(this.mDefaultPolyline != null) {
                this.mDefaultPolyline.setZIndex((float)var1);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void setWayPointStrings(ArrayList<String> wayPointStrings) {
        this.wayPointStrings = wayPointStrings;
    }
}
