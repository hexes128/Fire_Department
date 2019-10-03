package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

public class ItemPage extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    final ExecutorService service = Executors.newSingleThreadExecutor();
    final OkHttpClient client = new OkHttpClient();
    ArrayList<String> idSet, nameSet, aliasSet, buyDateSet, priceSet, placeSet, attachSet, spareSet, deptSet, custosSet;
    Global gv;
    List<Boolean> checkItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);

        gv = (Global) getApplicationContext();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.recyclerView1);
        mRecyclerView.setLayoutManager(layoutManager);
        checkItem = new ArrayList<>();

        idSet = new ArrayList<>();
        nameSet = new ArrayList<>();
        aliasSet = new ArrayList<>();
        buyDateSet = new ArrayList<>();
        priceSet = new ArrayList<>();
        placeSet = new ArrayList<>();
        attachSet = new ArrayList<>();
        spareSet = new ArrayList<>();
        deptSet = new ArrayList<>();
        custosSet = new ArrayList<>();

        mAdapter = new MyAdapter(idSet, nameSet);
        mRecyclerView.setAdapter(mAdapter);


        service.submit(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url("http://192.168.10.128:63914/Selectitembyplace/Selectitembyplace?item_place=" + gv.Selectedplace + "&Token=" + gv.Token).build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();

                    JSONObject jsonObject = new JSONObject(resStr);

                    String Tokenstatus = jsonObject.getString("Tokenstatus");


                    switch (Tokenstatus) {

                        case ("認證成功"): {

                            gv.Token = jsonObject.getString("NewToken");
                            JSONArray item = jsonObject.getJSONArray("Item");
                            gv.itemDetaiArray = item;
//                            JSONArray item_id = jsonObject.getJSONArray("id");
//                            JSONArray item_name = jsonObject.getJSONArray("name");

                            for (int i = 0; i < item.length(); i++) {
                                idSet.add(item.getJSONObject(i).getString("item_id").trim());
                                nameSet.add(item.getJSONObject(i).getString("item_name").trim());
                                aliasSet.add(item.getJSONObject(i).getString("item_alias").trim());
                                buyDateSet.add(item.getJSONObject(i).getString("item_buydate").trim());
                                priceSet.add(item.getJSONObject(i).getString("item_price").trim());
                                placeSet.add(item.getJSONObject(i).getString("item_place").trim());
                                attachSet.add(item.getJSONObject(i).getString("item_attach").trim());
                                spareSet.add(item.getJSONObject(i).getString("item_spare").trim());
                                deptSet.add(item.getJSONObject(i).getString("item_dept").trim());
                                custosSet.add(item.getJSONObject(i).getString("item_custos").trim());
                                checkItem.add(false);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();

                                }
                            });

                            break;
                        }

                        case ("認證過期"): {

                            Intent intent = new Intent(ItemPage.this, MainActivity.class);
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
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                JSONObject obj = new JSONObject();
                JSONArray array = new JSONArray();

                try {
                    obj.put("Token", gv.Token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < checkItem.size(); i++) {
                    if (checkItem.get(i)) {
                        try {
                            array.put(idSet.get(i));
                            obj.put("item_id", array);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.e("", obj.toString());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case (R.id.camera):
                Intent intent = new Intent(ItemPage.this,QRCodeScanner.class);
                startActivity(intent);
        }
        return true;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mid;
        private List<String> mname;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView item_id;
            public TextView item_name;
            public CheckBox checkBox;

            public ViewHolder(View v) {
                super(v);
                item_id = v.findViewById(R.id.item_id);
                item_name = v.findViewById(R.id.item_name);
                checkBox = v.findViewById(R.id.checkBox);
            }
        }

        public MyAdapter(List<String> id, List<String> name) {
            mid = id;
            mname = name;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_data_view, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.item_id.setText(mid.get(position).trim());
            holder.item_name.setText(mname.get(position).trim());

            holder.checkBox.setEnabled(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    checkItem.set(position, holder.checkBox.isChecked());
                }
            });


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "動產/非消耗品編號 : " + idSet.get(position) +
                                    "\n" + "動產/非消耗品名稱 : " + nameSet.get(position) +
                                    "\n" + "動產/非消耗品別名 : " + aliasSet.get(position) +
                                    "\n" + "購置日期 : " + buyDateSet.get(position) +
                                    "\n" + "價值 : " + priceSet.get(position) +
                                    "\n" + "存置地點 : " + placeSet.get(position) +
                                    "\n" + "附屬於 : " + attachSet.get(position) +
                                    "\n" + "備用品 : " + spareSet.get(position) +
                                    "\n" + "保管單位 : " + deptSet.get(position) +
                                    "\n" + "保管人 : " + custosSet.get(position)
                            , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.e("id", mid.get(position));

                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mid.size();
        }
    }

}
