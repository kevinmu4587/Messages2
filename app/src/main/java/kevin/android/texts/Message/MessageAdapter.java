package kevin.android.texts.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kevin.android.texts.R;

public class MessageAdapter extends RecyclerView.Adapter {
    public static final int VIEWHOLDER_NPC_MESSAGE = 0;
    public static final int VIEWHOLDER_MY_MESSAGE = 1;
    public static final int VIEWHOLDER_ACTION_MESSAGE = 2;
    public static final int VIEWHOLDER_TYPING_MESSAGE = 3;
    private List<Message> sentMessages = new ArrayList<>();

    private static final String TAG = "MessageAdapter";
    private int profilePictureID;

    public MessageAdapter(int profilePictureID) {
        this.profilePictureID = profilePictureID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWHOLDER_NPC_MESSAGE:
                return new NPCMessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.npc_message, parent, false));
            case VIEWHOLDER_MY_MESSAGE:
                return new MyMessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_message, parent, false));
            case VIEWHOLDER_TYPING_MESSAGE:
                return new TypingViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.typing_message, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Message current = sentMessages.get(position);
        switch (holder.getItemViewType()) {
            case VIEWHOLDER_NPC_MESSAGE:
                ((NPCMessageHolder) holder).bind(current);
                return;
            case VIEWHOLDER_MY_MESSAGE:
                ((MyMessageHolder) holder).bind(current);
                return;
            case VIEWHOLDER_TYPING_MESSAGE:
                ((TypingViewHolder) holder).bind();
            default:
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message current = sentMessages.get(position);
        if (current == null) {
            return VIEWHOLDER_TYPING_MESSAGE;
        } else if (current.getType().equals("npc")) {
            return VIEWHOLDER_NPC_MESSAGE;
        } else if (current.getType().equals("my")) {
            return VIEWHOLDER_MY_MESSAGE;
        } else {
            return VIEWHOLDER_ACTION_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return sentMessages.size();
    }

    public void setSentMessages(List<Message> sentMessages) {
        int originalSize = this.sentMessages.size();
        this.sentMessages = sentMessages; // add messages to the top
        if (this.sentMessages.size() - originalSize == 1) {
            notifyItemInserted(sentMessages.size() - 1);
            // Log.e(TAG, "notified item inserted at " + (sentMessages.size() - 1));
        } else {
            notifyDataSetChanged();
        }
        Log.e("MessageAdapter", "set sent messages. size(): " + sentMessages.size());
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    class NPCMessageHolder extends RecyclerView.ViewHolder {
        private ImageView profilePicture;
        private TextView content;
//        private TextView timeStamp;

        public NPCMessageHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.npc_msg_profile_pic);
            content = itemView.findViewById(R.id.npc_msg_content);
//            timeStamp = itemView.findViewById(R.id.npc_msg_timestamp);
        }

        void bind(Message message) {
            profilePicture.setImageResource(profilePictureID);
            content.setText(message.getContent()[message.getChoice()]);
//            timeStamp.setText(message.getTime());
        }
    }

    class MyMessageHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private ImageView readIndication;
        private TextView timestamp;

        public MyMessageHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.my_msg_content);
            readIndication = itemView.findViewById(R.id.my_msg_read_sign);
            timestamp = itemView.findViewById(R.id.my_msg_timestamp);
        }

        void bind(Message message) {
            content.setText(message.getContent()[message.getChoice()]);
            readIndication.setImageResource(R.drawable.ic_check_filled);
            timestamp.setText(message.getTime());
        }
    }

    class TypingViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePicture;

        public TypingViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.typing_msg_profile_picture);
        }

        void bind() {
            profilePicture.setImageResource(profilePictureID);
        }
    }
}
