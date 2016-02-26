package com.fournodes.ud.pranky.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

/*********************************
 * Created by Usman on 21/1/2016.*
 *********************************/
public class CameraControls {
    private static final String TAG = "Camera Controls";

    private Context context;
    private Camera camera;
    private boolean flashOn;
    private Thread blinkFlashThread;


    private static volatile CameraControls instance = null;


    private CameraControls(Context context) {
        this.context = context;
    }

    public static CameraControls getInstance(Context context) {
        if (instance == null) {
            synchronized (CameraControls.class) {
                if (instance == null) {
                    instance = new CameraControls(context);
                }
            }
        }

        return instance;
    }


    public boolean isFlashOn() {
        return flashOn;
    }

    public void setFlashOn(boolean flashOn) {
        this.flashOn = flashOn;
    }

    public Camera prepareCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                Camera.Parameters params = camera.getParameters();
                camera.setPreviewTexture(new SurfaceTexture(0));
                return camera;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (releaseCamera()) {
            return prepareCamera();
        }
        return null;
    }

    public boolean releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            return true;
        } else
            return false;
    }


    public boolean turnFlashOn(int duration) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            try {
                if (prepareCamera() != null) {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    camera.startPreview();
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (releaseCamera())
                    turnFlashOn(duration);
            }

            Handler turnFlashOff = new Handler();
            turnFlashOff.postDelayed(new Runnable() {
                public void run() {
                    releaseCamera();
                }
            }, duration * 1000);
            return true;
        } else
            return false;
    }


    public void blinkFlash(final int count) {
        try {
            if (prepareCamera() != null) {
                blinkFlashThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startPreview();
                        for (int i = 0; i < count; i++) {
                            try {
                                if (camera != null) {
                                    toggleFlash(camera);
                                    Thread.sleep(100);
                                    toggleFlash(camera);
                                    Thread.sleep(100);
                                } else
                                    break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        releaseCamera();
                    }
                });
                blinkFlashThread.start();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (releaseCamera())
                blinkFlash(count);
        }
    }

    private void toggleFlash(Camera camera) {
        try {
            if (camera != null) {
                Camera.Parameters params = camera.getParameters();
                if (isFlashOn()) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    setFlashOn(false);
                } else if (!isFlashOn()) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                    setFlashOn(true);
                }
            }
        } catch (RuntimeException e) {
            Log.w(TAG, e.toString());
        }
    }
}
