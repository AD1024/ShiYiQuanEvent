package ccoderad.bnds.shiyiquanevent.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ccoderad.bnds.shiyiquanevent.R;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by CCoderAD on 16/9/14.
 * http://ad1024.github.io
 */
public class ScannerActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private ZXingView mQRScanner;
    private Button btnOpenFlash;
    private boolean bFlashOpen;
    private Button btnOpenGallery;
    private static final int SUCCESS_RESULT_CODE = 6666;
    private static final int FAIL_RESULT_CODE = 9999;
    private static final int SCANNER_BACK = 10086;
    private static final int CHOOSE_PICTURE_REQ = 7777; // %%%
    private String QRContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        setSupportActionBar((Toolbar) findViewById(R.id.scanner_toolbar));
        mQRScanner = (ZXingView) findViewById(R.id.QR_Scanner);
        mQRScanner.setDelegate(this);

        btnOpenFlash = (Button) findViewById(R.id.Scanner_OpenLight);
        btnOpenGallery = (Button) findViewById(R.id.scanner_choose_photo);

        bFlashOpen = false;
        btnOpenFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bFlashOpen){
                    mQRScanner.openFlashlight();
                    btnOpenFlash.setText("关闭闪光灯");
                    bFlashOpen=true;
                }else {
                    mQRScanner.closeFlashlight();
                    btnOpenFlash.setText("打开闪光灯");
                    bFlashOpen=false;
                }
            }
        });
        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(BGAPhotoPickerActivity.newIntent(ScannerActivity.this,null,1,null,false),CHOOSE_PICTURE_REQ);
            }
        });
        mQRScanner.startSpot();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mQRScanner.startCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQRScanner.stopCamera();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Viberate(200);
        Intent retData = new Intent();
        retData.putExtra("QRContent",result);
        setResult(SUCCESS_RESULT_CODE,retData);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Viberate(500);
        setResult(FAIL_RESULT_CODE);
        finish();
    }

    @Override
    protected void onDestroy() {
        mQRScanner.onDestroy();
        super.onDestroy();
    }

    public void Viberate(long time){
        Vibrator vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    @Override
    public void onBackPressed() {
        mQRScanner.stopSpot();
        mQRScanner.stopCamera();
        setResult(SCANNER_BACK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mQRScanner.showScanRect();
        if(requestCode==CHOOSE_PICTURE_REQ && resultCode == Activity.RESULT_OK){
            ParseTask task =  new ParseTask();
            task.execute(BGAPhotoPickerActivity.getSelectedImages(data).get(0));
        }
    }

    private class ParseTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            return QRCodeDecoder.syncDecodeQRCode(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(TextUtils.isEmpty(s)){
                Toast.makeText(ScannerActivity.this,"未发现二维码",Toast.LENGTH_SHORT).show();
            }else{
                Intent it = new Intent();
                it.putExtra("QRContent",s);
                setResult(SUCCESS_RESULT_CODE,it);
                ScannerActivity.this.finish();
            }
        }
    }
}
