package rishiz.com.hifi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText txtMsg;
    private TextView txtChattingWith;
    private ProgressBar progressBar;
    private ImageView sendMsg, imgToolbar;
    private String usernameOfRoommate, emailOfRoomate, chatRoomId;
    private MessageAdapter messageAdapter;

    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        usernameOfRoommate = getIntent().getStringExtra("username_of_roommate");
        emailOfRoomate = getIntent().getStringExtra("email_of_roommate");

        recyclerView = findViewById(R.id.recyclerViewMsg);
        txtMsg = findViewById(R.id.textMsg);
        txtChattingWith = findViewById(R.id.txtChattingWith);
        progressBar = findViewById(R.id.progressMsg);
        sendMsg = findViewById(R.id.sendMsg);
        imgToolbar = findViewById(R.id.img_toolbar);
        sendMsg.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("messages/" + chatRoomId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(), emailOfRoomate, txtMsg.getText().toString()));
            txtMsg.setText("");
        });
        txtChattingWith.setText(usernameOfRoommate);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, getIntent().getStringExtra("my_img"), getIntent().getStringExtra("img_of_roomate"), MessageActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        recyclerView.setAdapter(messageAdapter);
        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("img_of_roommate")).placeholder(R.drawable.ic_baseline_person_24).error(R.drawable.ic_baseline_person_24).into(imgToolbar);
        setUpChatRoom();
    }


    private void setUpChatRoom() {
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUsername = snapshot.getValue(User.class).getUsername();
                if (usernameOfRoommate.compareTo(myUsername) > 0) {
                    chatRoomId = myUsername + usernameOfRoommate;
                } else if (usernameOfRoommate.compareTo(myUsername) == 0) {
                    chatRoomId = myUsername + usernameOfRoommate;
                } else {
                    //chatRoomId = myUsername + usernameOfRoommate;
                    chatRoomId = usernameOfRoommate + myUsername;
                }
                attachMessageListener(chatRoomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void attachMessageListener(String chatRoomId) {
        FirebaseDatabase.getInstance().getReference("messages/" + chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    messages.add(dataSnapshot.getValue(Message.class));
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}