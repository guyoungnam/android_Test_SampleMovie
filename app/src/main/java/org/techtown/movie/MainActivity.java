package org.techtown.movie;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;

    RecyclerView recyclerView;
    MovieAdapter adapter;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);

        recyclerView = findViewById(R.id.recyclerView); //xml 레이아웃에 정의한 리사이클러뷰 객체 참조

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest();
            }
        });

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MovieAdapter();
        recyclerView.setAdapter(adapter); //리사이클러븅 어댑터 설정하기
    }
    public void makeRequest(){
        String url = editText.getText().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                println("응답->" + response);
                processResponse(response); //응답을 받았을 때 processResponse() 메서드 호출하기
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                println("에러->"+error.getMessage());
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String,String>();
                return params;
            }

        };

        request.setShouldCache(false);
        requestQueue.add(request);
        println("요청 보냄");
    }

    public void println(String data){
        Log.d("MainActivity",data);
    }

    public void processResponse(String response){
        Gson gson = new Gson();
        MovieList movieList = gson.fromJson(response, MovieList.class); // 응답받은 JSON 문자열을 MovieList로 변환하기

        println("영화 정보 수:" +movieList.boxOfficeResult.dailyBoxOfficeList.size());

        for (int i =0; i< movieList.boxOfficeResult.dailyBoxOfficeList.size(); i++){
            Movie movie = movieList.boxOfficeResult.dailyBoxOfficeList.get(i);

            adapter.addItem(movie);
        }
        adapter.notifyDataSetChanged();
    }
}
