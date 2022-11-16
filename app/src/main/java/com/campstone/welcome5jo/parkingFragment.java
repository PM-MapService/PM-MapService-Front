package com.campstone.welcome5jo;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent.ParkingAreaItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class parkingFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static parkingFragment newInstance(int columnCount) {
        parkingFragment fragment = new parkingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public parkingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    TextView textOri, textParse;
    MyparkingRecyclerViewAdapter adapter;
    List<ParkingAreaItem> parkingAreaItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String value=this.getArguments().getString("query");
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

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);



        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, adapter.getItemCount()));
            }
            recyclerView.setAdapter(adapter);
        }


        return view;
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
                    ParkingAreaItem parkingAreaItem= new ParkingAreaItem(id, name, lat, lon );
                    parkingAreaItems.add(parkingAreaItem);
                }
                adapter=new MyparkingRecyclerViewAdapter(parkingAreaItems);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}