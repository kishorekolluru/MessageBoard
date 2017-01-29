package message.mad.kishore.org.messageboard;
/*
* InClass10
* Group 27
* */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_ID = "userIdExpenseApp";
    public static final String FIREBASE_USER = "firebaseUser";
    //UI compo
    Button loginButton, signUPButton;
    EditText emailText, pwdText;
    ProgressDialog pdialog;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser;
    public static String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chat Room");
        pdialog = new ProgressDialog(this);
        pdialog.setIndeterminate(true);
        pdialog.setMessage("Logging in...");

        setContentView(R.layout.activity_login);

        hook();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }
        mUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("demo", "Auth status changed");
                if (mAuth.getCurrentUser() != null) {
                    mUser = mAuth.getCurrentUser();
                    userId = mAuth.getCurrentUser().getUid();
                    displayExpenseActivity();
                    Log.d("demo", "The signed in user is " + mUser.getDisplayName());
                }
            }
        };
       /* if (mAuth.getCurrentUser() != null) {
            displayExpenseActivity();
        }*/
    }


    private void displayExpenseActivity() {
        Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(FIREBASE_USER, mUser.getEmail());
        startActivity(intent);
        finish();
    }

    private void hook() {
        loginButton = (Button) findViewById(R.id.login_button);
        signUPButton = (Button) findViewById(R.id.signup_button);
        emailText = (EditText) findViewById(R.id.email_tv);
        pwdText = (EditText) findViewById(R.id.password_tv);
        emailText.requestFocus();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = pwdText.getText().toString();
                if(!isValidEmail(email)){
                    toast("Enter a valid email id");
                } else if ("".equals(password)) {
                    toast("Enter your password");
                }else{
                    signIn(email, password);
                }
            }
        });
        signUPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignupActivity();
            }
        });
    }
    private final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    private void showSignupActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void signIn(String email, String password) {
        showDialog();
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("demo", "signInWithEmail:onComplete:" + task.isSuccessful());
                try {
                    if (task.isSuccessful()) {
                        Log.w("demo", "signInWithEmail success");
                        toast("Logged In!");
                        dismissDialog();
                    } else {
                        dismissDialog();
                        throw task.getException();
                    }
                } catch (Exception e) {
                    toast(e.getMessage());
                    Log.e("demo", "Exception in login :" + e.getMessage());
                }

            }
        });
    }

    private void toast(String s) {
        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private void showDialog(){
        pdialog.show();
    }
    private void dismissDialog(){
        pdialog.dismiss();
    }
}

//todo login cred validation