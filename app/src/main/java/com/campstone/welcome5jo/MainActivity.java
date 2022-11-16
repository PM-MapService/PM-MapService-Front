package com.campstone.welcome5jo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.text_input);
        button = findViewById(R.id.search_btn); //xml에서 생성한 id 매치

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);

        TMapMarkerItem markerItem1=makeMarker(new TMapPoint(37.45074006,126.6567586)); // 하이테크)
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
/*TMapMarkerItem2 markerItem2=new TMapMarkerItem2();
        markerItem2.setTMapPoint(;makeMarker(new TMapPoint(37.45074006,126.6567586)));*/
        tMapView.setCenterPoint(126.6471360,37.447591973);
        tMapView.setSKTMapApiKey("l7xxcf8d3af1899b4f168f7a593671f0c749");

        TMapCircle tcircle = new TMapCircle();
        tcircle.setCenterPoint(new TMapPoint(37.447591973,126.6471360));
        tcircle.setRadius(10);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        tMapView.addTMapCircle(tcircle.getID(), tcircle);

        linearLayoutTmap.addView(tMapView);
        //지도 축척 조정
        tMapView.setZoomLevel(17);

/*      button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ParkingDetailActivity.class);
                startActivity(intent);
            }
        }); */

        //검색 버튼 클릭
        SettingListener();
    }

    protected TMapMarkerItem makeMarker(TMapPoint tMapPoint){
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        Bitmap marker = BitmapFactory.decodeResource(getResources(), R.raw.parking);
        marker = Bitmap.createScaledBitmap(marker, 150, 150, true);
        markerItem1.setIcon(marker); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint(tMapPoint); // 마커의 좌표 지정
        markerItem1.setName("하이테크센터"); // 마커의 타이틀 지정
        return markerItem1;
    }


    private void SettingListener() {
        //버튼에 클릭 이벤트 적용
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editText.getText().toString(); //editText에 입력한 문자열을 얻어 온다.

                //인텐트 선언 및 정의
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                //입력한 input값을 intent로 전달한다.
                intent.putExtra("input_text", input);
                //액티비티 이동
                startActivity(intent);
            }
        });
    }

}