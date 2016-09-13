package net.sourceforge.simcpux.wxapi;

import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jy.wxtest.wxdemo.MainActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.simcpux.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {


    //这两个参数在文档中没有找到，可能是瞎了,,,自己在代码里面找了会才找到，这两个常量代表了微信返回的消息类型，是对登录的处理还是对分享的处理，登录会在后面介绍到
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private static final String TAG = "WXEntryActivity";
    public static final String APP_ID = "wxd930ea5d5a258f4f";
    public static final String SECRET = "52ecc579f2a902d03c9d6b3406a3df1a";
    public static IWXAPI api;

    private void registerToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        registerToWx();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //如果没回调onResp，八成是这句没有写
        api.handleIntent(getIntent(), this);
    }


    private void login() {
        if (!api.isWXAppInstalled()) {
            // AppData.showToast("您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        api.sendReq(req);
        Toast.makeText(getApplicationContext(), "请求发送了", Toast.LENGTH_SHORT).show();
    }


    //微信发送消息给app，app接受并处理的回调函数
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //app发送消息给微信，微信返回的消息回调函数,根据不同的返回码来判断操作是否成功
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //showToast("微信失败");
                break;

            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        SendAuth.Resp newResp = (SendAuth.Resp) resp;

                        //获取微信传回的code
                        String code = newResp.state;
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                        getInfo(code);
                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        //showToast("微信分享成功");
                        finish();
                        break;
                }
                break;
        }
    }

    private OkHttpClient mOKHttpClient;

    private OkHttpClient.Builder builder;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = (String) msg.obj;
            Toast.makeText(WXEntryActivity.this, "sss", Toast.LENGTH_LONG).show();
        }
    };

    private void getInfo(String code) {

        builder = new OkHttpClient.Builder();
        mOKHttpClient = builder.build();
        Request re = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                        APP_ID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code")
                .build();
        /*Request re=new Request.Builder()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token")
                .addHeader("appid", APP_ID)
                .addHeader("secret", SECRET)
                .addHeader("code", code)
                .addHeader("grant_type", "authorization_code")
                .build();*/
        mOKHttpClient.newCall(re).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("jysf", "erro");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("jysf", "okokokokok");
                Message message = Message.obtain();
                message.what = 0;
                //message.obj=response.body().string();
                handler.sendMessage(message);
                String s = response.body().string();
            }
        });

    }


}
