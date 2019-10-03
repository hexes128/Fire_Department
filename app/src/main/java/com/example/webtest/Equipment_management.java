package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Equipment_management extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    final ExecutorService service = Executors.newSingleThreadExecutor();
    final OkHttpClient client = new OkHttpClient();
    ArrayList<String> myDataset;
    Global gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_management);
        gv = (Global) getApplicationContext();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);

        myDataset = new ArrayList<>();
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);


        service.submit(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url("http://192.168.10.128:63914/Selectplace/Selectplace?Token=" + gv.Token.trim()).build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();


                    JSONObject jsonObject = new JSONObject(resStr);

                    String Tokenstatus = jsonObject.getString("Tokenstatus");

                    switch (Tokenstatus) {

                        case ("認證成功"): {
                            gv.Token = jsonObject.getString("NewToken");
                            JSONArray jsonArray = jsonObject.getJSONArray("JsonResult");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                myDataset.add(jsonArray.getString(i));
                            }
                            mAdapter = new MyAdapter(myDataset);
                            mRecyclerView.setAdapter(mAdapter);

                            break;
                        }

                        case ("認證過期"): {

                            Intent intent = new Intent(Equipment_management.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        }

                    }


                } catch (IOException e) {
                    Log.e("IO", e.getMessage());
                } catch (JSONException e) {
                    Log.e("json", e.getMessage());
                }

            }
        });


    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View v) {
                super(v);
                mTextView = v.findViewById(R.id.place_name);
            }
        }

        public MyAdapter(List<String> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_dataview, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextView.setText(mData.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Equipment_management.this, item_page.class);
                    gv.Selectedplace = mData.get(position);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }


}



