package com.imaginarywings.capstonedesign.remo;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.imaginarywings.capstonedesign.remo.model.PhotoSpotModel;
import com.imaginarywings.capstonedesign.remo.navermap.NMapCalloutCustomOldOverlay;
import com.imaginarywings.capstonedesign.remo.navermap.NMapPOIflagType;
import com.imaginarywings.capstonedesign.remo.navermap.NMapViewerResourceProvider;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.ArrayList;

import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by S.JJ on 2017-04-11.
 * 포토스팟 지도(네이버) 액티비티
 *
 * Fragment를 활용, 총 4개의 파일로 구성되어 있으며, 버튼을 클릭하면 지도 fragment를 화면에 추가합니다.
 * 1. FragmentMapActivity.java - 메인 액티비티
 * 2. fragment_main.xml - 메인 레이아웃 xml
 * 3. Fragement1.java - Fragment 정의
 * 4. fragment1.xml - Fragment에 포함될 UI요소
 */

public class PhotospotActivity extends NMapActivity {

    //현재 클래스명 얻어오기
    private final String TAG = getClass().getSimpleName();

    private static final String TAG_SPOT_DETAIL_DIALOG = "SpotDetailDialog";

    //애플리케이션 클라이언트 아이디 값( API 키 )
    private final String CLIENT_ID = "xQ50GyWn_EU3eQE4A1sL";
    private static final int REQUEST_LOCATION_ENABLE = 717;

    //네이버 맵
    private NMapView mMapView;

    //맵을 추가할 레이아웃..?
    private MapContainerView    mMapContainerView;

    //맵 컨트롤러
    private NMapController mMapController;

    //지도 위에 표시되는 오버레이 객체를 관리
    private NMapOverlayManager mOverlayManager;

    private NMapLocationManager mMapLocationManager;
    private NMapMyLocationOverlay mMyLocationOverlay;       //현재 위치를 표시하기 위한 오버레이 객체
    private NMapCompassManager mMapCompassManager;          //나침반 매니져


    //오버레이의 리소스를 제공하기 위한 객체
    private NMapViewerResourceProvider mMapViewerResourceProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create map view
        mMapView = new NMapView(this);

        //클라이언트 아이디 값 설정(API 값 설정)
        mMapView.setClientId(CLIENT_ID);

        //create parent view to rotate map view
        mMapContainerView = new MapContainerView(this);
        mMapContainerView.addView(mMapView);

        //set the activity content to the parent view
        //setContentView(mMapContainerView);
        setContentView(R.layout.activity_photospot);

        ButterKnife.bind(this);

        checkGPSPermissions();
        /*
        1. 맵의 기초적인 부분을 설정한다.
        2. 맵이 정상적으로 초기화가 되면 현재 위치르 가져오게 하낟.
        3. 현재 위치가 정상적으로 확인되면 마커를 생성한다.
         */

        //본 클래스 구현시 필요한 객체들의 생성 및 초기화 함수
        setNaverMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOCATION_ENABLE)
        {
            checkLocationEnabled();
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapLocationManager.isMyLocationEnabled()) {
            mMapLocationManager.disableMyLocation();
            mMapLocationManager.setOnLocationChangeListener(null);
        }

        mMapView.setOnMapStateChangeListener((NMapView.OnMapStateChangeListener) null);

        super.onDestroy();
    }

    /* 맵 상태변화 리스너 */
    private NMapView.OnMapStateChangeListener mStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {
            if (errorInfo == null) {
                // 초기 위치 설정
                mMapController.setMapCenter(new NGeoPoint(127.131342, 35.847532), 11);

            } else {
                Toast.makeText(PhotospotActivity.this, "지도를 초기화하는데 실패하였습니다.\n message: " + errorInfo.message, Toast.LENGTH_SHORT).show();
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

    //단말기의 현재 위치 상태 변경 시 호출되는 콜백 인터페이스.
    private NMapLocationManager.OnLocationChangeListener mLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        //현재 위치 변경시 호출.
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            if (mMapController != null) {

                //맵컨트롤러 void animateTo(NGeoPoint) : 지도 중심점을 전달된 좌표로 변경한다. 지도 중심점 이동 시 패닝 애니메이션이 수행된다.
                //myLocation 객체에 변경된 좌표를 전달
                mMapController.animateTo(myLocation);
            }

            //현재 위치를 계속 탐색하려면 true를 반환
            return true;
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

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay)itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(PhotospotActivity.this, text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem)overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    //네이버 맵 셋팅
    private void setNaverMap() {
        mMapView.setOnMapStateChangeListener(mStateChangeListener);

        //자세한 확대 (수치는 적절히 조정하도록)
        mMapView.setScalingFactor(2.0f);

        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapController = mMapView.getMapController();

        //create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        //create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

        //현재위치 매니저
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(mLocationChangeListener);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);

        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

    }

    //시작했을때 나의 현재 위치 보여주록 함.
    private void startMyLocation()
    {
        if(mMyLocationOverlay != null)
        {
            if(!mOverlayManager.hasOverlay(mMyLocationOverlay))
            {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }
        }

        boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);

        if (!isMyLocationEnabled) {
            Toast.makeText(this, "현재 위치를 가져올 수 없습니다. GPS 상태를 확인해주세요!", Toast.LENGTH_SHORT).show();
            createSpotMarker();
        } else {
            createSpotMarker();
        }
    }

    //포토스팟 마커 생성
    private void createSpotMarker() {
        int markerId = NMapPOIflagType.PIN;

        //포토스팟을 생성
        PhotoSpotModel model1 = new PhotoSpotModel(1, "MAIN", "aid", "main1", "주소1", Consts.IMAGE_URL, 35.852548, 127.100824);
        PhotoSpotModel model2 = new PhotoSpotModel(2, "MAIN", "aid", "main2", "주소2", Consts.IMAGE_URL, 35.852037, 127.101738);
        PhotoSpotModel model3 = new PhotoSpotModel(3, "MAIN", "aid", "main3", "주소3", Consts.IMAGE_URL, 35.847722, 127.123735);

        //여러 개의 오버레이 아이템을 하나의 오버레이 객체에서 관리하기 위한 오버레이 클래스의 객체 생성
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);

        // 경도, 위도, 표시문구, 표시할 마커 이미지의 id값, 객체, 오버레이를 관리하기 위한 id값
        //poiData.beginPOIdata(3);
        poiData.addPOIitem(127.130746, 35.847756, null, markerId, model1, 1);
        poiData.addPOIitem(127.132028, 35.847834, null, markerId, model2, 2);
        poiData.addPOIitem(127.123735, 35.847722, null, markerId, model3, 3);
        //poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener() {

            //오버레이 아이템의 선택 상태 변경 시 호출되는 콜백 인터페이스를 정의합니다.
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

                    //포토스팟을 눌렀을 때 나오는 팝업창
                    SpotDetailDialog dialog = new SpotDetailDialog();
                    Bundle data = new Bundle();
                    data.putParcelable("detail", model);
                    dialog.setArguments(data);
                    dialog.show(manager, TAG_SPOT_DETAIL_DIALOG);
                }
            }

            @Override
            public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
                if (item != null) {
                    Log.i(TAG, "onCalloutClick: " + item.toString());
                }
            }
        });

        mOverlayManager.addOverlay(poiDataOverlay);
    }

    //퍼미션 리스터 인터페이스 오버라이드
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            checkLocationEnabled();
            startMyLocation();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(PhotospotActivity.this, "권한이 허용되지 않아서 앱을 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    //GPS 퍼미션 요청
    private void checkGPSPermissions() {
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한요청을 거절하시면 이 서비스를 이용할 수 없습니다.\n\n 환경설정에서 권한요청을 허용해 주십시오.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    //GPS 기능 확인
    private void checkLocationEnabled() {
        if (!SmartLocation.with(this).location().state().isGpsAvailable()) {
            Toast.makeText(this, "GPS기능을 활성화 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(goToSettings, REQUEST_LOCATION_ENABLE);
        } else {
            //mButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    //현재 위치 정보 반환
    private NGeoPoint checkMyLocationInfo() {
        NGeoPoint location = mMapLocationManager.getMyLocation();
        return location;
    }




    /**
     * Container view class to rotate map view.
     */
    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpecWidth = widthMeasureSpec;
            int sizeSpecHeight = heightMeasureSpec;

            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);

                if (view instanceof NMapView) {
                    if (mMapView.isAutoRotateEnabled()) {
                        int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
                        sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
                        sizeSpecHeight = sizeSpecWidth;
                    }
                }

                view.measure(sizeSpecWidth, sizeSpecHeight);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
