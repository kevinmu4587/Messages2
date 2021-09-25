package kevin.android.texts.Conversations;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationAction;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kevin.android.texts.R;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private static final String TAG = "ConversationAdapter";
    private List<Conversation> activeConversations = new ArrayList<>();

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation current = activeConversations.get(position);

        holder.profilePicture.setImageResource(current.getProfilePictureID());
        holder.name.setText(current.getFullName());
        holder.lastMessage.setText(current.getLastMessage());
        if (current.isUnread()) {
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
        }
        holder.timestamp.setText("12:34am");
        holder.readIndication.setImageResource(R.drawable.ic_check_filled);
        holder.screen.setTag(current);
    }

    @Override
    public int getItemCount() {
        return activeConversations.size();
    }

    public void setActiveConversations(List<Conversation> activeConversations) {
        int oldSize = this.activeConversations.size();
        this.activeConversations = activeConversations;
        if (activeConversations.size() - oldSize == 1) {
            notifyItemInserted(oldSize);
        } else {
            notifyDataSetChanged();
        }
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout screen;
        private ImageView profilePicture;
        private TextView name;
        private TextView lastMessage;
        private TextView timestamp;
        private ImageView readIndication;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            screen = itemView.findViewById(R.id.conversation_container);
            profilePicture = itemView.findViewById(R.id.conversation_profile_picture);
            name = itemView.findViewById(R.id.conversation_name);
            lastMessage = itemView.findViewById(R.id.conversation_last_sent_text);
            timestamp = itemView.findViewById(R.id.conversation_timestamp);
            readIndication = itemView.findViewById(R.id.conversation_read_indication);

            screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Conversation conversation = (Conversation)screen.getTag();
                    // navigate to chat fragment
                    Navigation.findNavController(view).navigate(ConversationFragmentDirections.
                            actionConversationFragmentToChatFragment(conversation));
                }
            });
        }
    }
}
