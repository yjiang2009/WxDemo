package com.jy.wxtest.wxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tencent.mm.sdk.openapi.IWXAPI;

import net.sourceforge.simcpux.R;

public class MainActivity extends AppCompatActivity {

    public static final String APP_ID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // registerToWx();

    }


/*    public static IWXAPI api;
    private void registerToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
    }*/






}
