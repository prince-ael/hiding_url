package mnisiddique.pkce.hiding_auth_url;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView accessTokenTV;
    private TextView refreshTokenTV;
    private TextView tokenExceptionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accessTokenTV = findViewById(R.id.tv_access_token);
        refreshTokenTV = findViewById(R.id.tv_refresh_token);
        tokenExceptionTV = findViewById(R.id.tv_exception);

        populateView();
    }

    private void populateView() {
        Intent incoming = getIntent();
        if(incoming != null){
            Bundle args = incoming.getExtras();;
            if(args != null){
                String accTok = args.getString("access_token");
                String refTok = args.getString("refresh_token");
                String ex = args.getString("exception");

                if(ex != null && !ex.isEmpty()){
                    tokenExceptionTV.setText(ex);
                    tokenExceptionTV.setVisibility(View.VISIBLE);
                }
                if(accTok != null && !accTok.isEmpty()){
                    accessTokenTV.setText("Total Acc Tok Length: "+accTok.length()+" accTok: "+accTok.substring(0,25)+"...");
                }
                if(refTok != null && !refTok.isEmpty()){
                    refreshTokenTV.setText("Total Ref Tok Length: "+refTok.length()+" accTok: "+refTok.substring(0,25)+"...");
                }
            }
        }
    }
}