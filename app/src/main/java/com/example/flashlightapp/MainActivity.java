package com.example.flashlightapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button flashlightButton;
    private SeekBar powerSeekBar;
    private Button aboutButton;
    private boolean isFlashlightOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashlightButton = findViewById(R.id.flashlightButton);
        powerSeekBar = findViewById(R.id.powerSeekBar);
        aboutButton = findViewById(R.id.aboutButton);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        flashlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isFlashlightOn) {
                        turnOffFlashlight();
                    } else {
                        turnOnFlashlight();
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        powerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Adjust flashlight power if possible (note: not all devices support variable flashlight intensity)
                // This is just a placeholder, as Android's Camera2 API doesn't directly support variable torch brightness
                if (isFlashlightOn) {
                    try {
                        cameraManager.setTorchMode(cameraId, false);
                        Thread.sleep(100);
                        cameraManager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });
    }

    private void turnOnFlashlight() throws CameraAccessException {
        if (cameraManager != null) {
            cameraManager.setTorchMode(cameraId, true);
            isFlashlightOn = true;
            flashlightButton.setText("Turn Off");
            Toast.makeText(this, "Flashlight is ON", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffFlashlight() throws CameraAccessException {
        if (cameraManager != null) {
            cameraManager.setTorchMode(cameraId, false);
            isFlashlightOn = false;
            flashlightButton.setText("Turn On");
            Toast.makeText(this, "Flashlight is OFF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setMessage("Flashlight App\nCreated by Kalia Creation");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 50);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 50) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission is required to use flashlight", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
