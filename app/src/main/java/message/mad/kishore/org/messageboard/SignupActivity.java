package message.mad.kishore.org.messageboard;
/*
* InClass10
* Group 27
* */
import android.app.ProgressDialog;
import android.net.Uri;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
    public static final String REF_USERS_REF = "users";
    ProgressDialog pdialog;
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference(REF_USERS_REF);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final String T_FULL_NAME = "fullname";
    public static final String T_EMAIL_ID = "emailId";
    public static String signUpKey;
    EditText name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Sign up");
        setContentView(R.layout.activity_signup);

        pdialog = new ProgressDialog(this);
        pdialog.setIndeterminate(true);
        pdialog.setMessage("Signing Up...");

        name = (EditText) findViewById(R.id.FullName_et);
        email = (EditText) findViewById(R.id.Email_et);
        password = (EditText) findViewById(R.id.Password_et);
        Button signup = (Button) findViewById(R.id.signup_button);
        Button cancel = (Button) findViewById(R.id.cancel_button);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email_str = email.getText().toString().trim();
                final String name_str = name.getText().toString().trim();
                final String password_str = password.getText().toString();
                if (isSignUpInfoValid(email_str, name_str, password_str)) {
                    final User user = new User(name_str, email_str, password_str);
                    Log.d("demo", "Sign up user info :" + user.toString());
                    showDialog();
                    isNotExistinfUserAndCreate(user);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private boolean isSignUpInfoValid(String email_str, String name_str, String password_str) {
        if (email_str == null || email_str.equals("") || !isValidEmail(email_str)) {
            toast("Enter valid email");
            return false;
        } else if (name_str == null || name_str.equals("")) {
            toast("Enter some name");
            return false;
        } else if (password_str == null || password_str.equals("")) {
            toast("Enter some password");
            return false;
        }
        return true;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void createUser(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("demo", "Task exceptions:", task.getException());
                if (task.isSuccessful()) {
                    Log.d("demo", "Creation of user successful");
                    DatabaseReference userRef = mUsersRef.push();
                    userRef.child(T_FULL_NAME).setValue(user.getName());
                    userRef.child(T_EMAIL_ID).setValue(user.getEmail());
                    signUpKey = userRef.getKey();
                    setFullName(user);
                    signOut();
                    dismissDialog();
                    toast("Sign up successful. Please Login.");
                    finishSignupActicity();
                } else {
                    Log.d("demo", "create user failed");
                    toast(task.getException().getMessage());
                    dismissDialog();
                }
            }
        });
    }

    private void setFullName(User user) {
        FirebaseUser fireuser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getName())
                .build();
        fireuser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "User profile updated.");
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        signUpKey = "";
    }


    private void toast(String s) {
        Toast.makeText(SignupActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private void finishSignupActicity() {
        this.finish();
    }

    private void isNotExistinfUserAndCreate(final User user) {
        mUsersRef.orderByChild(T_EMAIL_ID).equalTo(user.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            createUser(user);
                        } else {
                            toast("Email " + user.getEmail() + " already exists. Please use another");
                            email.setText("");
                            dismissDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("demo", "Something went wrong with the db request "+ databaseError.getDetails()+ databaseError.getMessage());
                        toast("Something went wrong with the database access");
                    emailId    dismissDialog();
                    }
                });
    }

    private void showDialog() {
        pdialog.show();
    }

    private void dismissDialog() {
        pdialog.dismiss();
    }
}
//todo password field shows password