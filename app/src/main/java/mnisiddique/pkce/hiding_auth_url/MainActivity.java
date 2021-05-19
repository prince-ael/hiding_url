package mnisiddique.pkce.hiding_auth_url;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimationController;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebView;


import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;

import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import net.openid.appauth.ResponseTypeValues;

import org.json.JSONException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "www.d.com";
    //http://172.16.4.113:8080/oauth/authorize?response_type=code&client_id=public&redirect_uri=http://public-client/&scope=read
    private WebView webView;
//    private static final String CLIENT_ID = "public";
//    private static final String SCOPE = "read";
//    private static final String REDIRECT_URI = "http://public-client/";
//    private static final String AUTH_ENDPOINT = "https:///172.16.4.113:808/oauth/authorize";
//    private static final String TOKEN_ENDPOINT = "https://ab.nrbcommercialbank.com/oauth/token";
//    private static final String CLIENT_ID = "app";
//    private static final String SCOPE = "read";
//    private static final String REDIRECT_URI = "ibmobile.celloscope.net:/oauthredirect";
//    private static final String AUTH_ENDPOINT = "https://bkbapp-accounts.celloscope.net/oauth/authorize";
//    private static final String TOKEN_ENDPOINT = "https://bkbapp-accounts.celloscope.net/oauth/token";

    public static final String CLIENT_ID = "interactive.public";
    public static final String SCOPE = "openid profile email offline_access api";
    public static final String REDIRECT_URI = "io.identityserver.demo:/oauthredirect";
    public static final String AUTH_ENDPOINT = "https://demo.identityserver.io/connect/authorize";
    public static final String TOKEN_ENDPOINT = "https://demo.identityserver.io/connect/token";

    private AuthState authState;
    private AuthorizationService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.web_view);

        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        authService = new AuthorizationService(getApplicationContext(), builder.build());
        configService();

    }

    private void configService() {
        AuthorizationServiceConfiguration authService =
                new AuthorizationServiceConfiguration(
                        Uri.parse(AUTH_ENDPOINT),
                        Uri.parse(TOKEN_ENDPOINT)
                );
        authState = new AuthState(authService);
        finishServiceConfig();
    }

    private void finishServiceConfig() {
        Log.d(TAG, "Finishing service config");
        Log.d(TAG, "  authorization endpoint: " + authState.getAuthorizationServiceConfiguration().authorizationEndpoint);
        Log.d(TAG, "  token endpoint: " + authState.getAuthorizationServiceConfiguration().tokenEndpoint);
        startUserAuth();
    }

    private void startUserAuth() {
        Log.i(TAG, "Starting user auth");

        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                authState.getAuthorizationServiceConfiguration(),
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(REDIRECT_URI))
                .setScope(SCOPE);
        AuthorizationRequest authRequest = authRequestBuilder.build();
        openWithWebView(authRequest.toUri().toString(), authRequest);
    }

    private void openWithWebView(String url, AuthorizationRequest authRequest) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new MyWebClient(this, authRequest));
        Log.d(TAG, "openWithWebView: "+url);
        webView.loadUrl(url);
    }
}