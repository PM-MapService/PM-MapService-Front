package com.campstone.welcome5jo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.campstone.welcome5jo.placeholder.PassPoint;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements onLocationChangedCallback {

    TextView textView;
    Button button;

    double curlat,curlon;
    TextView textOri, textParse;

    TMapView tMapView;
    String curCircldId;
    RequestQueue queue;
    public String pid;

    List<PassPoint> passPoints;
    List<PassPoint> points;
    List<PassPoint> calpoints;
    TMapGpsManager gps;
    boolean flag=false;
    boolean con=true;
    int p=0;
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
        tcircle.setRadius(5);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setRadiusVisible(true);
        curCircldId=tcircle.getID();
        tMapView.addTMapCircle(curCircldId, tcircle);
        tMapView.setCenterPoint(curlon,curlat);

        if(con) {
        init();
        con=false;
        }
        if(flag){
            navigate(curlat,curlon);
        }


    }
    protected void navigate(double lat, double lon){
        for(PassPoint cpoint:calpoints){
            double theta = cpoint.lon - lon;
            double dist = Math.sin(deg2rad(cpoint.lat)) * Math.sin(deg2rad(lat)) + Math.cos(deg2rad(cpoint.lat)) * Math.cos(deg2rad(lat)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1609.344; //미터단위
            if(dist<8){
                if(cpoint.getTurntype()==11){
                    final MediaPlayer mp = MediaPlayer.create(RouteActivity.this, R.raw.straight);
                    mp.start();
                }
                else if(cpoint.getTurntype()==12){
                    final MediaPlayer mp = MediaPlayer.create(RouteActivity.this, R.raw.turn_left);
                    mp.start();
                }
                else if(cpoint.getTurntype()==13){
                    final MediaPlayer mp = MediaPlayer.create(RouteActivity.this, R.raw.turn_right);
                    mp.start();
                }
                else if(cpoint.getTurntype()==201){
                    final MediaPlayer mp = MediaPlayer.create(RouteActivity.this, R.raw.immediate);
                    mp.start();
                }
                calpoints.remove(cpoint);
            }
        }

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
        tMapView.setZoomLevel(16);
        Intent intent=getIntent();
        pid=intent.getStringExtra("pid");
        String pname= intent.getStringExtra("pname");
        textView.setText(pname);

        gps=new TMapGpsManager(this);
        gps.setProvider(gps.GPS_PROVIDER);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.OpenGps();
        Toast.makeText(RouteActivity.this, "좌표값을 받아오는 중입니다"
                , Toast.LENGTH_SHORT).show();





    }

    protected void init(){
        if(queue==null){
            queue= Volley.newRequestQueue(this);
        }

        String url="http://13.124.179.76:8085/api/route?startLng="+Double.toString(curlon)+"&startLat="+Double.toString(curlat)+"&endId="+pid;

        //JSON형태로 호출 및 응답 받기

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //응답받은 JSONObject에서 데이터 꺼내오기
                try {
                    parseData(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //오류 발생 시 실행
                Toast.makeText(RouteActivity.this, "error: " + error.toString()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);


/*        TMapPoint tMapPointEnd = new TMapPoint(lat,lon);
        TMapData tMapData = new TMapData();*/
        //경로?
/*        try {
            TMapPolyLine tMapPolyLine = new TMapPolyLine();
            //tMapPolyLine.addLinePoint(tMapPointStart);
            //tMapPolyLine.addLinePoint(tMapPointEnd);
            tMapPolyLine = tMapData.findPathData(tMapPointStart, tMapPointEnd);
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);

        }catch(Exception e) {
            e.printStackTrace();
        }*/

        tMapView.setCenterPoint(curlon,curlat);

        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
                tMapView.setZoomLevel(20);
                calpoints=points;
                flag=true;
            }
        });
    }
    protected TMapMarkerItem makePointMarker(PassPoint item){
        TMapMarkerItem markerItem = new TMapMarkerItem();
        TMapPoint tMapPoint = new TMapPoint(item.lat, item.lon);

        Bitmap marker = BitmapFactory.decodeResource(getResources(), R.raw.placeholder);
        marker = Bitmap.createScaledBitmap(marker, 100, 100, true);
        markerItem.setIcon(marker); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(tMapPoint); // 마커의 좌표 지정
        markerItem.setName(item.type); // 마커의 타이틀 지정
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

    protected void parseData(JSONObject jsonObject) throws JSONException {
        passPoints = new ArrayList<>();
        points = new ArrayList<>();

        JSONArray features=jsonObject.getJSONArray("features");
        int scnt=1,lcnt=1;
        for(int i = 0; i < features.length(); i++){
            JSONObject data=(JSONObject) features.get(i);
            JSONObject geometry=data.getJSONObject("geometry");
            JSONObject properties=data.getJSONObject("properties");

            if(geometry.getString("type").equals("Point")){
                JSONArray coord= (JSONArray) geometry.get("coordinates");
                PassPoint passPoint = new PassPoint(coord.getDouble(1), coord.getDouble(0),"points",scnt);
                passPoint.setTurntype(properties.getInt("turnType"));
                passPoint.setDescription(properties.getString("description"));
                points.add(passPoint);
                scnt++;
            }
            else{
                JSONArray coord= (JSONArray) geometry.get("coordinates");
                for(int j=0;j<coord.length();j++){
                    PassPoint passPoint = new PassPoint(coord.getJSONArray(j).getDouble(1), coord.getJSONArray(j).getDouble(0),"line",lcnt);
                    passPoint.setDescription(properties.getString("description"));
                    passPoints.add(passPoint);
                    lcnt++;
                }

            }

        }

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);
        for(int i=0;i<passPoints.size();i++){
            tMapPolyLine.addLinePoint(new TMapPoint(passPoints.get(i).lat,passPoints.get(i).lon));
        }
        tMapView.addTMapPolyLine("Line1", tMapPolyLine);

        for(PassPoint point:points){
            TMapMarkerItem parkingMarker=makePointMarker(point);
            parkingMarker.setCalloutTitle(point.description);
            parkingMarker.setAutoCalloutVisible(true);
            tMapView.addMarkerItem(Integer.toString(point.id), parkingMarker); // 지도에 마커 추가
        }

        Toast.makeText(RouteActivity.this,"화면을 길게 누르면 안내가 시작됩니다"
                , Toast.LENGTH_SHORT).show();
    }

    public void onClick1(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
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