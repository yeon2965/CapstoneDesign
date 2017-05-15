package com.imaginarywings.capstonedesign.remo;

import android.support.v4.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.imaginarywings.capstonedesign.remo.model.PhotoSpotModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by S.JJ on 2017-04-30.
 */

public class SpotDetailDialog extends DialogFragment {
    @BindView(R.id.detail_image) ImageView mImage;
    @BindView(R.id.detail_address_text) TextView mAdressText;
    @BindView(R.id.detail_subject_text) TextView mSubjectText;

    private PhotoSpotModel mSpotModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();

        if(data == null || data.getParcelable("detail") == null)
        {
            Toast.makeText(getActivity(), "포토스팟 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        else
        {
            mSpotModel = data.getParcelable("detail");
        }
    }

    @Override
    public void onResume() {
        //Store access varivables for window and black point
        Window window = getDialog().getWindow();
        Point size = new Point();

        //Store dimensions of the screen in size
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        //Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int)(size.x * 0.8), (int)(size.y * 0.8));
        window.setGravity(Gravity.CENTER);

        //Call super onResume after sizing
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_spot_detail, container, false);
        ButterKnife.bind(this, rootView);

        mAdressText.setText(mSpotModel.getAddress());
        mSubjectText.setText(mSpotModel.getSubject());

        Glide.with(this)
                .load(mSpotModel.getImgSrc())
                .thumbnail(0.1f)
                .into(mImage);

        setCancelable(false);
        return rootView;
    }

    @OnClick(R.id.detail_close_btn)
    public void closeBtnClick() { dismiss(); }

}
