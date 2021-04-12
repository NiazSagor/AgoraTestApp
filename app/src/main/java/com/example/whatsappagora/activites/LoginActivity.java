package com.example.whatsappagora.activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappagora.R;
import com.example.whatsappagora.model.User;
import com.example.whatsappagora.rtm.AGApplication;
import com.example.whatsappagora.rtm.ChatManager;
import com.example.whatsappagora.rtm.RtmTokenBuilder;
import com.example.whatsappagora.utils.MessageUtil;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RtmClient;

public class LoginActivity extends AppCompatActivity {

    SignInButton signInButton;
    Button signOutButton;
    Button signInButton_2;
    EditText editText;
    TextView statusTextView;

    private GoogleSignInAccount acct;
    private RtmClient mRtmClient;
    private ChatManager mChatManager;
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChatManager = AGApplication.the().getChatManager();
        mRtmClient = mChatManager.getRtmClient();


        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                singInUser(user);
                // ...
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    private void singInUser(FirebaseUser firebaseUser) {
        final User user = new User(firebaseUser.getUid());

        String accessToken = "";
        String appId = getString(R.string.agora_app_id);

        user.setFireDisplayName(firebaseUser.getDisplayName());

        RtmTokenBuilder builder = new RtmTokenBuilder();

        try {
            accessToken = builder.buildToken(
                    appId,
                    "20d8527878124cf099ee814daa43bd8e",
                    user.getFireUid(),
                    RtmTokenBuilder.Role.Rtm_User,
                    24
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "" + accessToken, Toast.LENGTH_SHORT).show();


        // 1st parameter is access token which is generated from server
        // access token is needed when the project is certificate enabled
        mRtmClient.login(null, user.getFireDisplayName(), new io.agora.rtm.ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, ChannelSelectionActivity.class);
                        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, user);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(final ErrorInfo errorInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_failed) + " " + errorInfo.getErrorDescription(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
