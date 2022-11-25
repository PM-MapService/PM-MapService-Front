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
    String id="0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_detail);

        parkingImage = findViewById(R.id.imageView);
        parkingName = findViewById(R.id.parkingName);

        Intent intent=getIntent();
        int value=intent.getIntExtra("selected",0);
        id=Integer.toString(value);

        if(queue==null){
            queue= Volley.newRequestQueue(this);
        }
            String url="http://13.124.179.76:8085/api/parking-areas/"+id;

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
            queue.add(jsonObjectRequest);
    }

    private void parseData(JSONObject object) {
        try{
        parkingName.setText(object.getString("name"));
        String imageStr=object.getString("image");
        Glide.with(this).load(imageStr).into(parkingImage);
    }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void Onclick1(View v){
        Intent intent = new Intent(ParkingDetailActivity.this,RouteActivity.class);
        //입력한 input값을 intent로 전달한다.
        //액티비티 이동
        intent.putExtra("pid",id);
        startActivity(intent);

    }
    public void Onclick2(View v){
        Intent intent = new Intent(ParkingDetailActivity.this,MainActivity.class);
        //입력한 input값을 intent로 전달한다.
        //액티비티 이동
        startActivity(intent);
    }
}
