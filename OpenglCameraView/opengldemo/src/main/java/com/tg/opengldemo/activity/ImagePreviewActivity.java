package com.tg.opengldemo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tg.opengldemo.R;

/**
 * @CreadBy ：DramaScript
 * @date 2017/8/16
 */
public class ImagePreviewActivity extends AppCompatActivity {

    private ImageView iv_preview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        String picPath = getIntent().getStringExtra("picPath");
        Glide.with(this).load(picPath).into(iv_preview);
    }

}
