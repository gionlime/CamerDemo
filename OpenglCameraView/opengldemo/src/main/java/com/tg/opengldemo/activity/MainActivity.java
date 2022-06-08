package com.tg.opengldemo.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tg.opengldemo.R;
import com.tg.opengldemo.glview.CameraView;
import com.tg.opengldemo.glview.FocusTouchGlView;
import com.tg.opengldemo.manager.ICameraHelper;
import com.tg.opengldemo.tools.PermissionUtils;
import com.tg.opengldemo.tools.Tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @CreadBy ：DramaScript
 * @date 2017/8/17
 */
public class MainActivity extends AppCompatActivity {

    private CameraView mCameraView;
    private ImageView iv_pai;
    private int flashMode = 0;
    private FocusTouchGlView flview;
    private boolean mirror;
    private boolean water;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA,Manifest
                .permission.WRITE_EXTERNAL_STORAGE},10,initViewRunnable);
    }

    private Runnable initViewRunnable=new Runnable() {
        @Override
        public void run() {
            setContentView(R.layout.activity_main);
            mCameraView= (CameraView)findViewById(R.id.mCameraView);
            flview= (FocusTouchGlView)findViewById(R.id.flview);
            flview.setCamaraHelper(mCameraView.getManager());
            flview.setmCameraView(mCameraView);
            flview.onStart();
            iv_pai= (ImageView) findViewById(R.id.iv_pai);
            iv_pai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCameraView.takePic(new ICameraHelper.TakePhotoCallback() {
                        @Override
                        public void onTakePhoto(Bitmap bm) {
                            if (mirror){
                                bm = Tools.convertBmp(bm);
                            }
                            if (water){
                                bm = Tools.createWaterMaskRightBottom(MainActivity.this,bm,BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),15,15);
                            }
                            BufferedOutputStream bos = null;
                            try {
                                String destDirName = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator;
                                makeRootDirectory(destDirName);
                                File file = new File(destDirName, Tools.getStringDate().replace(" ", "") + ".jpg");
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                bos = new BufferedOutputStream(new FileOutputStream(file));
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                                Intent i = new Intent(MainActivity.this, ImagePreviewActivity.class);
                                i.putExtra("picPath", file.getAbsolutePath().toString());
                                startActivity(i);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (flview!=null){
            flview.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_do, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                mCameraView.switchCamera();
                break;
            case R.id.item2:
                if (flashMode==0){
                    mCameraView.setFlash(1);
                    flashMode = 1;
                }else {
                    mCameraView.setFlash(0);
                    flashMode = 0;
                }
                break;
            case R.id.item8:
                if (mCameraView.getCameraId()==1){
                    mirror = true;
                }else {
                    Toast.makeText(this,"后置摄像头无镜像功能",Toast.LENGTH_SHORT).show();
                    mirror = false;
                }
                break;
            case R.id.item9:
                water = true;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }
}