package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRCodeScanner extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    Global gv;

    private JSONArray itemArray;
    private ArrayList idSet;

    final ExecutorService service = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);
        getPermissionsCamera();
        gv = (Global) getApplicationContext();



        surfaceView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.textView);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(400, 400).setAutoFocusEnabled(true).build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    return;
                try {
                    cameraSource.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0) {



             String id=       qrCodes.valueAt(0).displayValue;

                for(int i=0;i<gv.itemDetaiArray.length();i++){
                    try {

                        Log.e("掃到的資料", qrCodes.valueAt(0).displayValue);
                        Log.e("list的資料", gv.itemDetaiArray.getJSONObject(i).getString("item_id").trim());
                        if(id.trim().equals(gv.itemDetaiArray.getJSONObject(i).getString("item_id").trim())){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(qrCodes.valueAt(0).displayValue);
                                }
                            });

                                setVibrate(100); // 震動 0.1 秒
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
//                    textView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.e("", qrCodes.valueAt(0).displayValue);
//                            if (qrCodes.valueAt(0).displayValue.equals(idSet.get(position).toString())) {
//                                textView.setText(qrCodes.valueAt(0).displayValue);
//                                setVibrate(100); // 震動 0.1 秒
//                            }
//                        }
//                    });
                }
            }
        });
    }


    //震動設定
    public void setVibrate(int time) {
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }

    //相機權限
    public void getPermissionsCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
}
