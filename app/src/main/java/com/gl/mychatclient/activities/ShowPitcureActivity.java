package com.gl.mychatclient.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gl.mychatclient.R;
import com.gl.mychatclient.utils.ZoomImageView;

/**
 * Created by gl on 2016/2/12.
 */
public class ShowPitcureActivity extends AppCompatActivity  {
    ZoomImageView zivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        zivPicture = (ZoomImageView) findViewById(R.id.asp_ziv_picture);
        byte[] bytes = getIntent().getByteArrayExtra("picture");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        zivPicture.setImageBitmap(bitmap);// 填充控件

    }

}
