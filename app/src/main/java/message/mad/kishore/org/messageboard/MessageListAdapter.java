package message.mad.kishore.org.messageboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ocpsoft.pretty.time.PrettyTime;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;


/**
 * Created by kishorekolluru on 11/14/16.
 */

public class MessageListAdapter extends ArrayAdapter<Message> {

    List<Message> messages;
    Context context;
    private static PrettyTime pt = new PrettyTime();
    DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("message_board_msges");

    public MessageListAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        messages = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.message_list_item, parent, false);
        }
        TextView tvmsg = (TextView) convertView.findViewById(R.id.mmessage_text_view);
        TextView tvUser = (TextView) convertView.findViewById(R.id.owner_text_view);
        TextView tvtime = (TextView) convertView.findViewById(R.id.time_text_view);
        ImageView iv = (ImageView) convertView.findViewById(R.id.message_image);
        ImageView ivDel = (ImageView) convertView.findViewById(R.id.delMsgImage);
        ImageView imgViewComment = (ImageView) convertView.findViewById(R.id.commentMsgImage);
        ivDel.setTag(position);
        imgViewComment.setTag(position);
        final View finalConvertView = convertView;
        imgViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (Integer) v.getTag();
                finalConvertView.setEnabled(false);
                final LinearLayout linearLayout = (LinearLayout) finalConvertView.findViewById(R.id.linLayMain);
                LinearLayout horizontalLayout = new LinearLayout(context);
                horizontalLayout.setTag(pos);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                horizontalLayout.setLayoutParams(params);
                horizontalLayout.setId(pos);

                final EditText editText = new EditText(context);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
                editText.setLayoutParams(params1);
                editText.setId(pos + 1000);
                horizontalLayout.addView(editText);
                ImageView img = new ImageView(context);
                img.setId(pos + 1001);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(40, 40);
                img.setLayoutParams(params2);
                horizontalLayout.addView(img);
                img.setImageResource(R.mipmap.send);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().length() > 0) {
                            int count = linearLayout.getChildCount();
                            View view;
                            int viewPos = -1;
                            for (int i = 0; i < count; i++) {
                                view = linearLayout.getChildAt(i);
                                if (view.getId() == pos) {
                                    viewPos = i;
                                    break;
                                }

                            }
                            if (viewPos >= 0)
                                linearLayout.removeViewAt(viewPos);
                            v.setEnabled(true);

                            MessageActivity activity = (MessageActivity) context;
                            activity.sendCommentForMessage(getItem(position), editText.getText().toString());
                        }
                    }
                });
                linearLayout.addView(horizontalLayout);
            }
        });

        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = getItem(position).getMsgId();
                mMessagesRef.child(str).setValue(null);
            }
        });
        if (!getItem(position).getUserId().equals(MessageActivity.userId)) {
            ivDel.setVisibility(View.INVISIBLE);
        }
        Message msg = getItem(position);
        tvtime.setText(pt.format(new Date(msg.getTime())));
        tvUser.setText(msg.getUser());
        if (msg.getImageUrl() == null || msg.getImageUrl().equals("")) {
            tvmsg.setText(msg.getMessage());
            iv.setVisibility(View.GONE);
        } else {
            tvmsg.setText("");
//            StorageReference imageRef = FirebaseStorage.getInstance().getReference(msg.getImageUrl());

            Picasso.with(context).load(msg.getImageUrl()).into(iv);
        }
        return convertView;
    }
}
