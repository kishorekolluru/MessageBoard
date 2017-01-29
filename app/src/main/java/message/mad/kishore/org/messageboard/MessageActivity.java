package message.mad.kishore.org.messageboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static message.mad.kishore.org.messageboard.SignupActivity.T_EMAIL_ID;


public class MessageActivity extends AppCompatActivity {
    public static String userId;
    public static String userName;
    private static String userN;
    public static final String REF_USERS_REF = "users";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference(REF_USERS_REF);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chat");
        setContentView(R.layout.activity_message);

        if (getIntent().getExtras().containsKey(LoginActivity.USER_ID)) {
            userId = (String) getIntent().getExtras().get(LoginActivity.USER_ID);
            userName = (String) getIntent().getExtras().get(LoginActivity.FIREBASE_USER);
            TextView loginName = (TextView) findViewById(R.id.loginNameText_view);
            userN = getUserName(userName);
            loginName.setText("Logged in as " + userN);
        }

        hook();

    }

    public static final String T_MSG_BOARD = "message_board_msges";
    ChildEventListener mChildListener;
    ArrayList<Message> messagesList = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        mMessagesRef = mRootRef.child(T_MSG_BOARD);
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Message msg = new Message();
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                    String user = (String) values.get("user");
                    String msgstr = (String) values.get("message");
                    Date d = new Date((Long)values.get("time"));
                    String imageUrl = (String) values.get("imageUrl");
                    String msgId = (String) values.get("msgId");
                    String userId = (String) values.get("userId");
                    ArrayList<Comment> comments = (ArrayList<Comment>) values.get("comments");
                    msg.setUser(user);
                    msg.setUserId(userId);
                    msg.setMsgId(msgId);
                    msg.setTime(d.getTime());
                    msg.setMessage(msgstr);
                    msg.setImageUrl(imageUrl);
                    msg.setComments(comments);
                    MessageActivity.this.addMessageToList(msg);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message msg = new Message();
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();
                    String user = (String) values.get("user");
                    String msgstr = (String) values.get("message");
                    Date d = new Date((Long)values.get("time"));
                    String imageUrl = (String) values.get("imageUrl");
                    String msgId = (String) values.get("msgId");
                    String userId = (String) values.get("userId");
                    ArrayList<Comment> comments = (ArrayList<Comment>) values.get("comments");
                    msg.setUser(user);
                    msg.setUserId(userId);
                    msg.setMsgId(msgId);
                    msg.setTime(d.getTime());
                    msg.setMessage(msgstr);
                    msg.setImageUrl(imageUrl);
                    msg.setComments(comments);
                    MessageActivity.this.removeMessage(msg);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mMessagesRef.addChildEventListener(mChildListener);


    }

    private void removeMessage(Message msg) {
    for(int i =0; i<adapter.getCount();i++) {
        if (msg.getMsgId().equals(adapter.getItem(i).getMsgId())) {
            adapter.remove(adapter.getItem(i));
            break;
        }
    }

    }

    private void addMessageToList(Message msg) {
        /*List<Message> messtemp = new ArrayList<>();
        for(int i =0; i<adapter.getCount(); i++) {
            messtemp.add(adapter.getItem(i));
        }
        if (!messtemp.contains(msg)) {
            adapter.add(msg);
        }*/

        for(int i =0; i<adapter.getCount();i++) {
            if (msg.getMsgId().equals(adapter.getItem(i).getMsgId())) {
                break;
            }
            if (i == adapter.getCount() - 1) {
                adapter.add(msg);
            }
        }
        if (adapter.getCount() == 0) {
            adapter.add(msg);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessagesRef.removeEventListener(mChildListener);
    }

    private String getUserName(String userEmail) {
        String str = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

           str =  FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        return str;
    }

    ImageView msgSendImage, imageSendimage, imageLogout, imageCOmment;
    EditText messageEditText;
ListView lview;
MessageListAdapter adapter;
    private void hook() {
        messageEditText = (EditText) findViewById(R.id.message_input);
        msgSendImage = (ImageView) findViewById(R.id.imageViewMessage);
        imageSendimage = (ImageView) findViewById(R.id.imageViewImg);
        imageLogout = (ImageView) findViewById(R.id.imageView);

        msgSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEditText.getText().toString();
                if (msg != null && !"".equals(msg)) {
                    postMessage(msg);
                }
            }
        });
        imageSendimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.this.chooseImage();
            }
        });
        imageLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.this.initLoginActivity();
            }
        });
        lview = (ListView) findViewById(R.id.listView);
        adapter = new MessageListAdapter(this, R.layout.message_list_item, messagesList);
        adapter.setNotifyOnChange(true);
        lview.setAdapter(adapter);

    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mMessagesRef = mRootRef.child("message_board_msges");

    private void postMessage(String strmsg) {
        DatabaseReference ref = mMessagesRef.push();
        Message msg = new Message();
        msg.setMsgId(ref.getKey());
        msg.setUserId(userId);
        msg.setImageUrl("");
        msg.setMessage(strmsg);
        msg.setTime(new Date().getTime());
        msg.setUser(userN);
        ref.setValue(msg);
        messageEditText.setText("");
    }
public static final int SELECT_PICTURE_REQUEST = 100;

    public void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        Log.d("data", data.getDataString());
        Log.d("reg", String.valueOf(requestCode));
        Log.d("sel", String.valueOf(resultCode));
        if (requestCode == SELECT_PICTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                Log.d("demo", imageUri.toString());
                postImage(imageUri);
            }

        }
    }
    private void toast(String s) {
        Toast.makeText(MessageActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    StorageReference mountainRef = FirebaseStorage.getInstance().getReference().child("message_images");

    private void postImage(Uri imageUri){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e("demo", "Exception", e);
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] imageBytes = baos.toByteArray();
        mountainRef.child("image_"+new Date().getTime()+"_"+userId).putBytes(imageBytes, new StorageMetadata.Builder().setContentType("image/jpg").build())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri url = taskSnapshot.getDownloadUrl();
                        DatabaseReference ref = mMessagesRef.push();
                        Message msg = new Message();
                        msg.setComments(new ArrayList<Comment>());
                        msg.setMessage("");
                        msg.setTime(new Date().getTime());
                        msg.setMsgId(ref.getKey());
                        msg.setUserId(userId);
                        msg.setImageUrl(url.toString());
                        msg.setUser(userN);
                        ref.setValue(msg);
                        Log.d("demo", "The download URL is " + url.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo","The upload failed :", e);
                    }
                });
    }

    private void initLoginActivity() {
        mAuth.signOut();
        LoginActivity.userId = "";
        Intent intent = new Intent(MessageActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void sendCommentForMessage(Message msg, String comment) {
        Comment com = new Comment();
        com.setUserId(userId);
        com.setComment(comment);
        com.setTime(new Date().getTime());
        mMessagesRef.child(msg.getMsgId()).child("comments").push().setValue(comment);
    }
}
