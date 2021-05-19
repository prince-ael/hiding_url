package mnisiddique.pkce.hiding_auth_url;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class MyWebClient extends WebViewClient implements AuthorizationService.TokenResponseCallback {
    public static final String TAG = "www.d.com";
    private Activity activity;
    private AuthorizationRequest authRequest;

    public MyWebClient(Activity activity, AuthorizationRequest authRequest) {
        this.activity = activity;
        this.authRequest = authRequest;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.d(TAG, "shouldOverrideUrlLoading");
        view.loadUrl(request.getUrl().toString());
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "onPageFinished: " + url);
        if (url != null && !url.isEmpty()) {
            Uri uri = Uri.parse(url);
            String redirectionPath = uri.getQueryParameter("redirectionPath");
            Log.d(TAG, "onPageFinished redirectionPath: " + redirectionPath);
            boolean isManaged = manageRedirectionPath(redirectionPath);
            if (!isManaged) {
                //TODO: Exchange token
                String code = uri.getQueryParameter("code");
                Log.d(TAG, "onPageFinished code: " + code);
                exchangeCode(code, view);
            }
        }
    }

    private void exchangeCode(String code, WebView view) {
        if(code != null && !code.isEmpty() && code.length() > 1){
            view.setVisibility(View.GONE);
            AuthorizationResponse resp = new AuthorizationResponse.Builder(authRequest).setAuthorizationCode(code).build();
            TokenRequest tokReq =  resp.createTokenExchangeRequest();
            Log.d(TAG, "tokReq: "+tokReq.jsonSerializeString());
            AuthorizationService authService = new AuthorizationService(activity);
            authService.performTokenRequest(tokReq, this);
        }
    }

    private boolean manageRedirectionPath(String redirectionPath) {
        if (redirectionPath != null && !redirectionPath.isEmpty()) {
            if (redirectionPath.equalsIgnoreCase("register")) {
                activity.finish();
                activity.startActivity(new Intent(activity, RegistrationActivity.class));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
        Intent homeIntent = new Intent(activity, HomeActivity.class);
        if(response != null){
            homeIntent.putExtra("access_token", response.accessToken);
            homeIntent.putExtra("refresh_token", response.refreshToken);
            Log.d(TAG, "accessToken: "+response.accessToken);
            Log.d(TAG, "refreshToken: "+response.refreshToken);
        }else{
//            Log.d(TAG, "AuthorizationException: "+ex.getMessage());
            homeIntent.putExtra("exception", ex.getMessage());
            ex.printStackTrace();
        }

        activity.finish();
        activity.startActivity(homeIntent);
    }
}
