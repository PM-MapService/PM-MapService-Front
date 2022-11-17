package com.campstone.welcome5jo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements onLocationChangedCallback {

    TextView textView;
    Button button;

    double curlat,curlon;
    TextView textOri, textParse;

    TMapView tMapView;
    String curCircldId;

    @Override
    public void onLocationChange(Location location){
        TMapGpsManager gps=new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();

        TMapPoint point=gps.getLocation();
        curlat=point.getLatitude();
        curlon=point.getLongitude();
        tMapView.removeTMapCircle(curCircldId);

        TMapCircle tcircle = new TMapCircle();
        tcircle.setCenterPoint(point);
        tcircle.setRadius(10);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        curCircldId=tcircle.getID();
        tMapView.addTMapCircle(curCircldId, tcircle);
        tMapView.setCenterPoint(curlon,curlat);;
        gps.CloseGps();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        button = findViewById(R.id.fin_btn); //xml에서 생성한 id 매치
        textView=findViewById(R.id.destination);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey("l7xxcf8d3af1899b4f168f7a593671f0c749");

        linearLayoutTmap.addView(tMapView);
        //지도 축척 조정
        tMapView.setZoomLevel(17);


        //경로?
/*        try {
            TMapPolyLine tMapPolyLine = new TMapData().findPathData(tMapPointStart, tMapPointEnd);
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);

        }catch(Exception e) {
            e.printStackTrace();
        }*/
/*      button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ParkingDetailActivity.class);
                startActivity(intent);
            }
        }); */

        //주차 리스트 출력
        try {
            String url="http://3.39.158.43:8088/api/diaries/search?q=";

            //JSON형태로 호출 및 응답 받기
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //응답받은 JSONObject에서 데이터 꺼내오기
                    parseData(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //오류 발생 시 실행
                    Toast.makeText(RouteActivity.this, "error: " + error.toString()
                            , Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
        }



    }

    protected TMapMarkerItem makeparkingMarker(ParkingAreaContent.ParkingAreaItem item){
        TMapMarkerItem markerItem = new TMapMarkerItem();
        TMapPoint tMapPoint = new TMapPoint(item.lat, item.lon);

        Bitmap marker = BitmapFactory.decodeResource(getResources(), R.raw.parking);
        marker = Bitmap.createScaledBitmap(marker, 150, 150, true);
        markerItem.setIcon(marker); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(tMapPoint); // 마커의 좌표 지정
        markerItem.setName(item.name); // 마커의 타이틀 지정
        return markerItem;
    }

    protected TMapMarkerItem makeMarker(TMapPoint tMapPoint) {
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        Bitmap marker = BitmapFactory.decodeResource(getResources(), R.raw.parking);
        marker = Bitmap.createScaledBitmap(marker, 150, 150, true);
        markerItem1.setIcon(marker); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint(tMapPoint); // 마커의 좌표 지정
        markerItem1.setName("하이테크센터"); // 마커의 타이틀 지정
        return markerItem1;
    }


    private void parseData(JSONObject object) {

        //상태값
        String status = "";

        //원본 텍스트뷰에 담기
        textOri.setText(object.toString());

        //키값 리스트
        ArrayList<String> keyList = new ArrayList<>();

        try {
            //data 담기
            JSONObject data = object.getJSONObject("data");
            //status 담기
            status = object.getString("status");
            //data안의 전체 키값 담기
            Iterator iterator = data.keys();
            //반복문을 통해 list에 키값 담기
            while(iterator.hasNext()){
                String s = iterator.next().toString();
                keyList.add(s);
                //파싱 텍스트뷰에 담기
                textParse.append("status: " + status + "\n");
                //data안의 키값으로 데이터 꺼내오기
                for(int i = 0; i < keyList.size(); i++){

                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


}