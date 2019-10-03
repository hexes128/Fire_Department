package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
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

public class item_page extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    final ExecutorService service = Executors.newSingleThreadExecutor();
    final OkHttpClient client = new OkHttpClient();
    ArrayList<String> myidset;
    ArrayList<String> mynameset;
    Global gv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);

        gv = (Global) getApplicationContext();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.recyclerView1);
        mRecyclerView.setLayoutManager(layoutManager);


        myidset = new ArrayList<>();
        mynameset = new ArrayList<>();
        mAdapter = new MyAdapter(myidset,mynameset);
        mRecyclerView.setAdapter(mAdapter);



        service.submit(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url("http://192.168.10.128:63914/Selectitembyplace/Selectitembyplace?item_place="+gv.Selectedplace+ "&Token="+gv.Token).build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();


                    JSONObject jsonObject = new JSONObject(resStr);

                    String Tokenstatus = jsonObject.getString("Tokenstatus");

                    switch (Tokenstatus) {

                        case ("認證成功"): {
                            gv.Token = jsonObject.getString("NewToken");
                            JSONArray item = jsonObject.getJSONArray("Item");

                            for (int i = 0; i < item.length(); i++) {
                                JSONObject itemcolunm = item.getJSONObject(i);
                              myidset.add(itemcolunm.getString("item_id"));
                              mynameset.add(itemcolunm.getString("item_name"));
                            }
                            mAdapter = new MyAdapter(myidset,mynameset);
                            mRecyclerView.setAdapter(mAdapter);

                            break;
                        }

                        case ("認證過期"): {

                            Intent intent = new Intent(item_page.this, MainActivity.class);
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
        private List<String> mid;
        private List<String> mname;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView item_id;
            public CheckedTextView item_name;

            public ViewHolder(View v) {
                super(v);
                item_id = v.findViewById(R.id.item_id);
                item_name = v.findViewById(R.id.item_name);
            }
        }

        public MyAdapter(List<String> id,List<String> name) {
            mid = id;
            mname=name;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dataview, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.item_id.setText(mid.get(position).trim());
            holder.item_name.setText(mname.get(position).trim());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return mid.size();
        }
    }

}
