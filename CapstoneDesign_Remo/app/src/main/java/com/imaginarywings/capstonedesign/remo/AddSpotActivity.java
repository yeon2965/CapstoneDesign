package com.imaginarywings.capstonedesign.remo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import butterknife.ButterKnife;

public class AddSpotActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_spot;
    private int id_view;
    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);

        iv_spot = (ImageView)this.findViewById(R.id.ImgView_PhotoSpot);

        ImageButton btnAddSpot = (ImageButton)this.findViewById(R.id.Btn_AddSpot);

        btnAddSpot.setOnClickListener(this);
    }

    private void TakePhotoAction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + "jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void TakeAlbumAction()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        id_view = v.getId();

        if(v.getId() == R.id.Btn_AddSpot)
        {
            DialogInterface.OnClickListener cameralistener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TakePhotoAction();
                }
            };

            DialogInterface.OnClickListener albumlistener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            //버튼 옵션에 따라서 바뀜.. Positive, Title, Neutral, Negative 등등
            new AlertDialog.Builder(this).setTitle("업로드할 이미지 선택")
                    .setPositiveButton("카메라 촬영", cameralistener)
                    .setNeutralButton("앨범 선택", albumlistener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode)
        {
            case PICK_FROM_ALBUM :
            {
                mImageCaptureUri = data.getData();
                Log.d("StartWheel", mImageCaptureUri.getPath().toString());

                //break;
            }

            case PICK_FROM_CAMERA :
            {
                //이부분은 구현 안해도 됨! 사진에서 찍고 바로 하는 기능!!!
                //후에 사용하려면 사용해도 됨

                //이미지를 가져온 이후에 리사이즈할 이미지 크기를 결정
                //이후에 이미지 크롭 어플리케이션을 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                //CROP 할 이미지를 300*300 크기로 저장
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);

                break;
            }

            case CROP_FROM_IMAGE:
            {
                //크롭이 된 이후의 이미지를 넘겨 받는다.
                //이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에 임시파일을 삭제한다
                if(resultCode != RESULT_OK)
                {
                    return;
                }

                final Bundle extras = data.getExtras();

                //CROP된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/SmartWheel/" + System.currentTimeMillis() + "jpg";

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");    //CROP된 BITMAP
                    iv_spot.setImageBitmap(photo);                  //레이아웃의 이미지뷰에 CROP된 BITMAP을 보여줌

                    storeCropImage(photo,filePath);                 //CROP된 이미지를 외부 저장소, 앨범에 저장한ㄷ.
                    absoultePath = filePath;
                    break;

                }

                File f = new File(mImageCaptureUri.getPath());

                if(f.exists())
                {
                    f.delete();
                }
            }
        }
    }

    /**
     * 외부저장소에 크롭된 이미지를 저장하는 함수
     * 비트맵을 저장하는 부분
     * @param bitmap
     * @param filePath
     */
    private void storeCropImage(Bitmap bitmap, String filePath)
    {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartWheel/";
        File directory_SmartWheel = new File(dirPath);

        //디렉터리에 폴더가 없다면 (새로 이미지를 저장하는 경우에 속한다.)
        if(!directory_SmartWheel.exists())
            directory_SmartWheel.mkdir();

        File copyFile = new File(dirPath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(getIntent().ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
