package net.com.gopal.qrscanner;


import static android.webkit.URLUtil.isValidUrl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.zxing.Result;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;


public class QRScanner extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private CodeScannerView camView;
    private CodeScanner codeScanner;
 private TextView scannedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        camView = findViewById(R.id.Scanner);
        scannedText = findViewById(R.id.scannedText);
        codeScanner = new CodeScanner(this, camView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            codeScanner.startPreview();
        }
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannedText.setText(result.getText());
                        restartScanningAfterDelay();
                        handleScannedResult(result.getText());
                    }
                });
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Throwable error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannedText.setText("Error: " + error.getMessage());
                    }
                });
            }
        });

    }

    private void handleScannedResult(String scannedText) {
        if (isValidUrl(scannedText)) {
            // Open the URL in the default web browser
            openUrlInBrowser(scannedText);
        } else {
            // Handle other types of scanned data as needed
            Toast.makeText(this, "Scanned Text: " + scannedText, Toast.LENGTH_SHORT).show();
        }

    }

    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


    private void restartScanningAfterDelay() {
        camView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the scanning process again
                codeScanner.startPreview();
            }
        }, 3000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            codeScanner.startPreview();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }


@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            codeScanner.startPreview();
        } else {
            Toast.makeText(this, "Permission Denied \n you cannot scan QR without permission", Toast.LENGTH_SHORT).show();
        }
    }
}
}