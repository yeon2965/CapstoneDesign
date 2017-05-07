package com.imaginarywings.capstonedesign.remo.navermap;

/**
 * Created by S.JJ on 2017-05-06.
 */

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.imaginarywings.capstonedesign.remo.R;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.overlay.NMapPOIitem;

import static java.security.AccessController.getContext;

public class NMapCalloutCustomOverlayView extends NMapCalloutOverlayView {

    private View mCalloutView;
    private TextView mCalloutText;
    private View mRightArrow;


    public NMapCalloutCustomOverlayView(Context context, NMapOverlay itemOverlay, NMapOverlayItem item, Rect itemBounds) {
        super(context, itemOverlay, item, itemBounds);

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);

        /* 네이버 지도 내에 있던 마커의 말풍선
        li.inflate(R.layout.callout_overlay_view, this, true);

        mCalloutView = findViewById(R.id.callout_overlay);
        mCalloutText = (TextView)mCalloutView.findViewById(R.id.callout_text);
        mRightArrow = findViewById(R.id.callout_rightArrow);
        */

        mCalloutView.setOnClickListener(callOutClickListener);

        mCalloutText.setText(item.getTitle());

        if (item instanceof NMapPOIitem) {
            if (((NMapPOIitem)item).hasRightAccessory() == false) {
                mRightArrow.setVisibility(View.GONE);
            }
        }
    }

    private final View.OnClickListener callOutClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(null, mItemOverlay, mOverlayItem);
            }
        }
    };

}
