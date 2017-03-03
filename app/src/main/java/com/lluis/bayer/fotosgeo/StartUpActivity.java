package com.lluis.bayer.fotosgeo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;

public class StartUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AVLoadingIndicatorView avi;
    private static final int RC_SIGN_IN = 123;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAuth();
            }
        });


        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator("BallPulseIndicator");
        avi.show();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                doAuth();
            }
        }, 1000);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        System.out.println(resultCode);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == ResultCodes.OK){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("uuid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(intent);
                finish();
            }else if(resultCode == ResultCodes.CANCELED){
                btnSignIn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doAuth(){
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("uuid", mAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish();
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                            )
                            .build(),
                    RC_SIGN_IN);
        }
    }
}
