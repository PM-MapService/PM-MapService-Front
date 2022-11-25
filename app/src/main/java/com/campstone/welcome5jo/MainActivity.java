package com.campstone.welcome5jo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent.ParkingAreaItem;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
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

    List<ParkingAreaItem> parkingAreaItems;
    double curlat=37.45074006,curlon=126.6567586;
    TextView textOri, textParse;

    TMapPoint tMapPoint;
    TMapView tMapView;
    String curCircldId;
    public static RequestQueue queue;
    TMapGpsManager gps;
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    @Override
    public void onLocationChange(Location location){



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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText = findViewById(R.id.text_input);
        button = findViewById(R.id.search_btn); //xml에서 생성한 id 매치

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);

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

        queue = Volley.newRequestQueue(this);

        //주차 리스트 출력
            String url="http://13.124.179.76:8085/api/parking-areas";

            //JSON형태로 호출 및 응답 받기
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, response.toString());
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
        Log.e("jsObjRequest",  "" + jsonArrayRequest);
            queue.add(jsonArrayRequest);


        //마커 클릭이벤트 추가
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

                if(arrayList.size()>0){
                    String input = arrayList.get(0).getID(); //editText에 입력한 문자열을 얻어 온다.

                    //인텐트 선언 및 정의
                    Intent intent = new Intent(MainActivity.this, ParkingDetailActivity.class);
                    //입력한 input값을 intent로 전달한다.
                    intent.putExtra("selected", Integer.parseInt(input));
                    //액티비티 이동
                    startActivity(intent);

                }

            return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });

        tMapView.setCenterPoint(curlon,curlat);;
        OnCheckPermission();

        gps=new TMapGpsManager(this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.OpenGps();
    }

    protected TMapMarkerItem makeparkingMarker(ParkingAreaItem item){
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
                intent.putExtra("curlat",curlat);
                intent.putExtra("curlon",curlon);
                //액티비티 이동
                startActivity(intent);
            }
        });
    }

    protected void parseData(JSONArray jsonArray) {
        parkingAreaItems=new ArrayList<>();
        try {
            //data 담기
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject data=(JSONObject) jsonArray.get(i);
                    int id=data.getInt("parkingAreaId");
                    double lat= data.getDouble("latitude");
                    double lon=data.getDouble("longitude");
                    String name = data.getString("name");
                    ParkingAreaItem parkingAreaItem= new ParkingAreaItem(id, name, lat, lon,curlat,curlon );
                    parkingAreaItems.add(parkingAreaItem);
                }
            for(ParkingAreaItem item:parkingAreaItems){
                TMapMarkerItem parkingMarker=makeparkingMarker(item);
                tMapView.addMarkerItem(Integer.toString(item.id), parkingMarker); // 지도에 마커 추가
            }
            } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public void OnCheckPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},

                        PERMISSIONS_REQUEST);

            } else {

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},

                        PERMISSIONS_REQUEST);

            }
        }
    }


}