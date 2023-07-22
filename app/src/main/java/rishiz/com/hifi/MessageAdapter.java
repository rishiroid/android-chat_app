package rishiz.com.hifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private final ArrayList<Message> messages;
    private final String senderImg;
    private final String recieverImg;
    private final Context context;

    public MessageAdapter(ArrayList<Message> messages, String senderImg, String recieverImg, Context context) {
        this.messages = messages;
        this.senderImg = senderImg;
        this.recieverImg = recieverImg;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_holder, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.txtMsg.setText(messages.get(position).getContent());
        ConstraintLayout constraintLayout = holder.ccll;
        if (messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            Glide.with(context).load(senderImg).error(R.drawable.ic_baseline_person_24).placeholder(R.drawable.ic_baseline_person_24).into(holder.proImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.LEFT);
            constraintSet.clear(R.id.text_Msg_Content, ConstraintSet.LEFT);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.RIGHT, R.id.ccLayout, ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.text_Msg_Content, ConstraintSet.RIGHT, R.id.profile_cardView, ConstraintSet.LEFT, 0);
            constraintSet.applyTo(constraintLayout);
        } else {
            Glide.with(context).load(recieverImg).error(R.drawable.ic_baseline_person_24).placeholder(R.drawable.ic_baseline_person_24).into(holder.proImg);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.text_Msg_Content, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.LEFT, R.id.ccLayout, ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.text_Msg_Content, ConstraintSet.LEFT, R.id.profile_cardView, ConstraintSet.RIGHT, 0);
            constraintSet.applyTo(constraintLayout);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        ConstraintLayout ccll;
        TextView txtMsg;
        ImageView proImg;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            ccll = itemView.findViewById(R.id.ccLayout);
            txtMsg = itemView.findViewById(R.id.text_Msg_Content);
            proImg = itemView.findViewById(R.id.small_img_profile);

        }
    }
}
