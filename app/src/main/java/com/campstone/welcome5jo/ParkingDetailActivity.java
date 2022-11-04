package com.campstone.welcome5jo;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class ParkingDetailActivity extends AppCompatActivity {
    private ImageView imageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_detail);

        imageView1 = findViewById(R.id.imageView);


        String imageStr = "https://welcome5jo-bucket.s3.ap-northeast-2.amazonaws.com/parking-area/%ED%95%98%EC%9D%B4%ED%85%8C%ED%81%AC%EC%9E%85%EA%B5%AC.png";
        Glide.with(this).load(imageStr).into(imageView1);
    }
}
