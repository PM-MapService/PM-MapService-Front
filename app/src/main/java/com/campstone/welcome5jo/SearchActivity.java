package com.campstone.welcome5jo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private LinearLayout linearLayoutTmap;
    private TMapView tMapView;
    private ListView listView=null;
    private ListViewAdapter adapter=null;
    List<ParkingAreaItem> parkingAreaItems;
    double curlat,curlon;
    TextView textOri, textParse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Intent intent=getIntent();
        String value=intent.getStringExtra("input_text");

        try {
            String url="http://3.39.158.43:8088/api/diaries/search?q="+value;

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
                }
            });
        }catch (Exception e){
        }

        initMap();

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

    protected void initMap() {
        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        linearLayoutTmap.addView(tMapView);
        //지도 축척 조정
        tMapView.setZoomLevel(17);
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
                    ParkingAreaItem parkingAreaItem= new ParkingAreaItem(id, name, lat, lon,curlat,curlon );
                    parkingAreaItems.add(parkingAreaItem);
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }


    public class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return parkingAreaItems.size();
        }

        @Override
        public Object getItem(int i) {
            return parkingAreaItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return parkingAreaItems.get(i).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final ParkingAreaItem item = parkingAreaItems.get(position);

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
            distance.setText((int) item.distance);

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

}