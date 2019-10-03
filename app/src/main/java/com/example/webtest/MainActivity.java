package com.example.webtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button login, read;
    private EditText userid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final OkHttpClient client = new OkHttpClient();
        login = findViewById(R.id.login);
        userid = findViewById(R.id.userId);
        password = findViewById(R.id.password);
        final ExecutorService service = Executors.newSingleThreadExecutor();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                service.submit(new Runnable() {
                    @Override
                    public void run() {

                        Request request = new Request.Builder().url("http://192.168.10.128:63914/Login/login?id=" + userid.getText() + "&password=" + password.getText()).build();
                        try {
                            final Response response = client.newCall(request).execute();
                            final String resStr = response.body().string();
                            JSONObject jsonObject = new JSONObject(resStr);
                            String Access = jsonObject.getString("Access").trim();
                            String Token = jsonObject.getString("Token").trim();
                            switch (Access) {
                                case ("True"): {
                                    Global gv = (Global) getApplicationContext();
                                    gv.Token = Token.trim();
                                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                    startActivity(intent);
                                    break;
                                }
                                case ("無此帳號"): {
                                    Snackbar.make(view,"帳號錯誤",Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    Log.e("狀態", "帳號錯誤");
                                    break;
                                }
                                case ("密碼錯誤"): {
                                    Snackbar.make(view,"密碼錯誤",Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                                    Log.e("狀態", "密碼錯誤");
                                    break;
                                }
                                case ("此帳號已登入"): {
                                    Snackbar.make(view,"此帳號已登入",Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                                    Log.e("狀態", "此帳號已登入");
                                    break;
                                }


                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}
