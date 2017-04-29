package com.imaginarywings.capstonedesign.remo;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gun0912.tedpermission.TedPermission;
import com.imaginarywings.capstonedesign.remo.model.PhotoSpotModel;
import com.imaginarywings.capstonedesign.remo.navermap.NMapPOIflagType;
import com.imaginarywings.capstonedesign.remo.navermap.NMapViewerResourceProvider;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import butterknife.ButterKnife;

/**
 * Created by S.JJ on 2017-04-11.
 * 포토스팟 지도(네이버) 액티비티
 */

public class PhotospotActivity extends NMapActivity {

    //현재 클래스명 얻어오기
    private final String TAG = getClass().getSimpleName();

    private  static final String TAG_SPOT_DETAIL_DIALOG = "SpotDetailDialog";

    //애플리케이션 클라이언트 아이디 값( API 키 )
    private final String CLIENT_ID = "xQ50GyWn_EU3eQE4A1sL";

    private NMapView mMapView;  //지도 화면 View
    private NMapController mMapController;
    private NMapLocationManager mMapLocationManager;

    private NMapResourceProvider mMapResourceProvider;
    private NMapOverlayManager mMapOverlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new NMapView(this);

        //클라이언트 아이디 값 설정(API 값 설정)
        mMapView.setClientId(CLIENT_ID);
        setContentView(mMapView);

        ButterKnife.bind(this);
        checkGPSPermissions();

        /*
        1. 맵의 기초적인 부분을 설정한다.
        2. 맵이 정상적으로 초기화가 되면 현재 위치르 가져오게 하낟.
        3. 현재 위치가 정상적으로 확인되면 마커를 생성한다.
         */

        setNaverMap();
    }

    @Override
    protected void onDestroy() {
        if(mMapLocationManager.isMyLocationEnabled())
        {
            mMapLocationManager.disableMyLocation();
            mMapLocationManager.setOnLocationChangeListener(null);
        }

        mMapView.setOnMapStateChangeListener((NMapView.OnMapStateChangeListener) null);

        super.onDestroy();
    }

    /* 리스너 */
    private NMapView.OnMapStateChangeListener mStateChangeListener = new NMapView.OnMapStateChangeListener()
    {
        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
            if (errorInfo == null) {
                // 초기 위치 설정
                mMapController.setMapCenter(new NGeoPoint(127.131342, 35.847532), 11);

                startMyLocation();    //사용자 정의 함수!

            } else {
                Toast.makeText(PhotospotActivity.this, "지도를 초기화하는데 실패하였습니다.\nmessage: " + errorInfo.message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {

        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {

        }
    };

    private NMapLocationManager.OnLocationChangeListener mLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
            mMapController.animateTo(myLocation);
            return false;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
            Log.e(TAG, "onLocationUpdateTimeout: ");
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Log.e(TAG, "onLocationUnavailableArea: " + myLocation.toString());
        }
    };

    //네이버 맵 초기 셋팅
    private void setNaverMap() {
        mMapView.setOnMapStateChangeListener(mStateChangeListener);
        mMapView.setClickable(true);

        //자세한 확대를 위해서 이렇게 해야한다.
        mMapView.setScalingFactor(2.0f);

        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapController = mMapView.getMapController();
        mMapResourceProvider = new NMapViewerResourceProvider(this);
    }

    //위치 초기화
    private void startMyLocation() {
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(mLocationChangeListener);
        boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
        if (!isMyLocationEnabled) {
            Toast.makeText(this, "현재 위치를 가져올 수 없습니다. GPS 상태를 확인해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            createSpotMarker();
        }
    }

    //포토스팟 마커 만들기
    private void createSpotMarker() {
        int markerId = NMapPOIflagType.PIN;

        PhotoSpotModel model1 = new PhotoSpotModel(1, "MAIN", "aid", "main1", "주소1", Consts.IMAGE_URL, 35.852548, 127.100824);
        PhotoSpotModel model2 = new PhotoSpotModel(2, "MAIN", "aid", "main2", "주소2", Consts.IMAGE_URL, 35.852037, 127.101738);
        PhotoSpotModel model3 = new PhotoSpotModel(3, "MAIN", "aid", "main3", "주소3", Consts.IMAGE_URL, 35.847722, 127.123735);

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(2, mMapResourceProvider);
//        poiData.beginPOIdata(2);
        // 경도, 위도, 타이틀, 마커타입, 사용자오브젝트, 번호
        poiData.addPOIitem(127.130746, 35.847756, null, markerId, model1, 1);
        poiData.addPOIitem(127.132028, 35.847834, null, markerId, model2, 2);
        poiData.addPOIitem(127.123735, 35.847722, null, markerId, model3, 3);
//        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mMapOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener() {
            @Override
            public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
                if (item != null) {
                    Log.i(TAG, "onFocusChanged: " + item.getTag().toString());

                    PhotoSpotModel model = (PhotoSpotModel) item.getTag();
                    FragmentManager manager = getFragmentManager();
                    Fragment frag = manager.findFragmentByTag(TAG_SPOT_DETAIL_DIALOG);
                    if (frag != null) {
                        manager.beginTransaction().remove(frag).commit();
                    }

                    /* 포토스팟을 눌렀을 때 나오는 팝업창!! SpotDetailDialog 라는 클래스 먼저 구현하자
                    SpotDetailDialog dialog = new SpotDetailDialog();
                    Bundle data = new Bundle();
                    data.putParcelable("detail", model);
                    dialog.setArguments(data);
                    dialog.show(manager, TAG_SPOT_DETAIL_DIALOG);
                    */
                }
            }

            @Override
            public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
                if (item != null) {
                    Log.i(TAG, "onCalloutClick: " + item.toString());
                }
            }
        });

        mMapOverlayManager.addOverlay(poiDataOverlay);
    }

    //GPS 퍼미션 요청
    private void checkGPSPermissions() {
        new TedPermission(this)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    //GPS 기능 확인
    private void checkLocationEnabled() {
        /*
        if (!SmartLocation.with(this).location().state().isGpsAvailable()) {
            Toast.makeText(this, "GPS기능을 활성화 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(goToSettings, REQUEST_LOCATION_ENABLE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
            */
        }
    }

}
