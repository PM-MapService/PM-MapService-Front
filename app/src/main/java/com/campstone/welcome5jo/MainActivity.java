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
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements onLocationChangedCallback {

    EditText editText;
    Button button;

    LocationManager locationManager;
    LocationListener locationListener;

    List<ParkingAreaContent.ParkingAreaItem> parkingAreaItems;
    double curlat,curlon;
    TextView textOri, textParse;

    TMapPoint tMapPoint;
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
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.text_input);
        button = findViewById(R.id.search_btn); //xml에서 생성한 id 매치

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);

        TMapMarkerItem markerItem1=makeMarker(new TMapPoint(37.45074006,126.6567586)); // 하이테크)
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
/*TMapMarkerItem2 markerItem2=new TMapMarkerItem2();
        markerItem2.setTMapPoint(;makeMarker(new TMapPoint(37.45074006,126.6567586)));*/
        tMapView.setSKTMapApiKey("l7xxcf8d3af1899b4f168f7a593671f0c749");

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
                    Toast.makeText(MainActivity.this, "error: " + error.toString()
                            , Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
        }

        //지도에 마커 추가
        for(ParkingAreaContent.ParkingAreaItem item:parkingAreaItems){
            TMapMarkerItem parkingMarker=makeparkingMarker(item);
            tMapView.addMarkerItem(item.name, parkingMarker); // 지도에 마커 추가
        }

        //마커 클릭이벤트 추가
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                Intent intent = new Intent();
                intent.putExtra("parkingId",markerList);
                return false;
            }
        });

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
                    int id=data.getInt("id");
                    double lat= data.getDouble("lat");
                    double lon=data.getDouble("lon");
                    String name = data.getString("name");
                    ParkingAreaContent.ParkingAreaItem parkingAreaItem= new ParkingAreaContent.ParkingAreaItem(id, name, lat, lon,curlat,curlon );
                    parkingAreaItems.add(parkingAreaItem);
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


}