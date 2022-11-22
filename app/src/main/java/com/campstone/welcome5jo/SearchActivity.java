package com.campstone.welcome5jo;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent.ParkingAreaItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent;
import com.google.gson.JsonObject;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchActivity extends AppCompatActivity{

    LinearLayout linearLayoutTmap;
    TMapView tMapView;
    private ListView listView;
    private ListViewAdapter adapter=null;
    protected List<ParkingAreaItem> searchedParking;
    protected double curlat,curlon;
    double blat, blon;
    TextView textOri, textParse;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Intent intent=getIntent();
        String value=intent.getStringExtra("input_text");
        curlat=intent.getDoubleExtra("curlat",0);
        curlon=intent.getDoubleExtra("curlon",0);
        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmapp);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxcf8d3af1899b4f168f7a593671f0c749");
        linearLayoutTmap.addView(tMapView);

        //지도 축척 조정
        tMapView.setZoomLevel(17);
        listView= (ListView) findViewById(R.id.list_fgm);


        queue=Volley.newRequestQueue(this);
            String uurl="http://13.124.179.76:8085/api/parking-areas/building?buildingName="+value;
            String burl="http://13.124.179.76:8085/api/building?buildingName="+value;
            //JSON형태로 호출 및 응답 받기

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    uurl, null, new Response.Listener<JSONArray>() {
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
                    Toast.makeText(SearchActivity.this, "error: " + error.toString()
                            , Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("jsObjRequest",  "" + jsonArrayRequest);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                burl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                //응답받은 JSONObject에서 데이터 꺼내오기
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //오류 발생 시 실행
                Toast.makeText(SearchActivity.this, "error: " + error.toString()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);
        queue.add(jsonObjectRequest);

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                if(arrayList.size()>0){
                    Intent intent = new Intent(SearchActivity.this,ParkingDetailActivity.class);
                    //입력한 input값을 intent로 전달한다.
                    intent.putExtra("selected",Integer.parseInt(arrayList.get(0).getID()));
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

    protected void parseData(JSONArray jsonArray) {
        searchedParking=new ArrayList<>();
        try {
            //data 담기
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject data=(JSONObject) jsonArray.get(i);
                int id=data.getInt("parkingAreaId");
                double lat= data.getDouble("latitude");
                double lon=data.getDouble("longitude");
                String name = data.getString("name");
                ParkingAreaItem parkingAreaItem= new ParkingAreaItem(id, name, lat, lon,curlat,curlon );
                searchedParking.add(parkingAreaItem);
            }
            for(ParkingAreaItem item:searchedParking){
                TMapMarkerItem parkingMarker=makeparkingMarker(item);
                parkingMarker.setCalloutTitle(item.name);
                parkingMarker.setAutoCalloutVisible(true);
                tMapView.addMarkerItem(Integer.toString(item.id), parkingMarker); // 지도에 마커 추가
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        adapter=new ListViewAdapter();
        listView.setAdapter(adapter);
    }
    protected void parseData(JSONObject data) {
        try {
            //data 담기
                blat= data.getDouble("latitude");
                blon=data.getDouble("longitude");
            tMapView.setCenterPoint(blon,blat);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return searchedParking.size();
        }

        @Override
        public Object getItem(int i) {
            return searchedParking.get(i);
        }

        @Override
        public long getItemId(int i) {
            return searchedParking.get(i).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final ParkingAreaItem item = searchedParking.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.fragment_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView parkingName = (TextView) convertView.findViewById(R.id.parking_name);
            TextView distance = (TextView) convertView.findViewById(R.id.distance);


            parkingName.setText(item.name);
            distance.setText(Double.toString(item.distance)+"m");

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchActivity.this,ParkingDetailActivity.class);
                    //입력한 input값을 intent로 전달한다.
                    intent.putExtra("selected",item.id);
                    //액티비티 이동
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    public void onClick1(View v){
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
    }
}