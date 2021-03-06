package edu.temple.foodgo;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRScanActivity extends AppCompatActivity {

    private BarcodeDetector detector;
    private CameraSource cameraSource;
    private View cameraView, barcodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        cameraView = cameraView = (SurfaceView) findViewById(R.id.cameraView);
        barcodeInfo = (TextView) findViewById(R.id.QRInfoView);

        setupBarcodeDetector();
        setupCamera();
        connectCameraAndBarcode();

        setupSubmitListener();
    }

    public void setupBarcodeDetector() {
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();
        if (!detector.isOperational()) {
            Log.d("DetectorSetup", "Could not set up the detector!");
            detector = null;
        }
        this.detector = detector;
    }

    public void setupCamera() {


        ((SurfaceView) cameraView).getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 4){
            startCamera();
        }
    }

    private void startCamera(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(QRScanActivity.this,
                    new String[]{"android.permission.CAMERA"},
                    4);

            return;
        }else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(500, 500)
                    .setRequestedFps(15.0f)
                    .build();
            SurfaceView surfaceView = (SurfaceView) cameraView;
            SurfaceHolder theHolder = surfaceView.getHolder();
            try {
                cameraSource.start(theHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectCameraAndBarcode(){
        detector.setProcessor(new Detector.Processor() {

            @Override public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections detections) {
                final SparseArray barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) { barcodeInfo.post(new Runnable() { // Use the post method of the TextView
                        public void run() {
                            ((TextView)barcodeInfo).setText(((Barcode)barcodes.valueAt(0)).rawValue);
                        }
                    });
                }
            }
        });
    }

    public void setupSubmitListener(){
        Button submitButton = findViewById(R.id.selectButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView)(QRScanActivity.this).findViewById(R.id.QRInfoView);
                if(textView.getText().toString().equals(getResources().getString(R.string.QRInfoDefault))){
                    Toast.makeText(QRScanActivity.this, R.string.QRInfoDefault, Toast.LENGTH_LONG).show();
                }else{
                    cameraSource.release();
                    Intent intent = new Intent(QRScanActivity.this, RestaurantActivity.class);
                    intent.putExtra("restaurantID", textView.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

}
