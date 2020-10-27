package ab.cd.firstorder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Store extends AppCompatActivity {
    WebView webView;
    String mNumber;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_store);
        handler = new Handler();
        int REQUEST_CODE = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);

        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);


        @SuppressLint("MissingPermission") String PhoneNum = telManager.getLine1Number();
        mNumber = PhoneNum.replace("+82", "0");
        if(PhoneNum.startsWith("+82")) // 국제번호(+82 10...)로 되어 있을경우 010 으로 변환
        {
            mNumber = PhoneNum.replace("+82", "0");
        }

        ActionBar ab = getSupportActionBar();
        ab.hide();
        webView = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webView.addJavascriptInterface(this, "android");

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                webView.loadUrl("javascript:setNumber('" + mNumber + "')");

                super.onPageFinished(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){

        });


        webView.loadUrl("http://192.168.0.100:4100/showProduct/"+mNumber);



      /*  */
    }


    @JavascriptInterface
    public void setMessage(final String arg) {


    }

    @JavascriptInterface
    public void gps(final String gps){
        Log.d("CODERS",gps);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri gmmIntentUri = Uri.parse("geo:"+gps);
                intent.setData(gmmIntentUri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(webView.getUrl().contains("showProduct/product")==false && webView.getUrl().contains("showProduct") ==true){
            finish();
        }
        else{
            webView.loadUrl("http://192.168.0.100:4100/showProduct/"+mNumber);
        }
    }
}
