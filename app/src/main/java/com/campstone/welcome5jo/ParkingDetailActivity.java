package com.campstone.welcome5jo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ParkingDetailActivity extends AppCompatActivity {
    private ImageView parkingImage;
    private TextView parkingName;

    TextView textOri, textParse;
    RequestQueue queue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_detail);

        parkingImage = findViewById(R.id.imageView);
        parkingName = findViewById(R.id.parkingName);

        Intent intent=getIntent();
        String value=intent.getStringExtra("input_text");

        if(queue==null){
            queue= Volley.newRequestQueue(this);
        }
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
                    //오류 발생 시 실행
                    Toast.makeText(ParkingDetailActivity.this, "error: " + error.toString()
                            , Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
        }

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

                    //키값 변수에 담기
                    String key = keyList.get(i).toString();

                    //키값을 통해 데이터 꺼내오기
                    String value = data.getString(key);

                    //파싱 텍스트뷰에 담기
                    textParse.append(key + ": " + value + "\n");



                }
                parkingName.setText(data.getString("parkingName"));
                String imageStr=data.getString("image");
                Glide.with(this).load(imageStr).into(parkingImage);
            }

        }catch(JSONException e){

            e.printStackTrace();
        }
    }
}
