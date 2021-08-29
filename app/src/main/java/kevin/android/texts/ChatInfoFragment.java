package kevin.android.texts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import kevin.android.texts.Conversations.Conversation;
import kevin.android.texts.Message.ChatFragment;

public class ChatInfoFragment  extends Fragment {
    private ImageView profilePicture;
    private EditText editName;
    private EditText editNickname;
    private TextView description;

    private Conversation conversation;

    public ChatInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_info, container, false);
        profilePicture = view.findViewById(R.id.chat_info_profile_picture);
        editName = view.findViewById(R.id.chat_info_name);
        editNickname = view.findViewById(R.id.chat_info_nickname);
        description = view.findViewById(R.id.chat_info_description);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversation = ChatInfoFragmentArgs.fromBundle(getArguments()).getConversation();
        editName.setText(conversation.getFullName());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                // send data back to ChatFragment
//                NavController navController = NavHostFragment.findNavController(ChatInfoFragment.this);
//                navController.getPreviousBackStackEntry().getSavedStateHandle().set("key", conversation);
//                this.setEnabled(false);
//                requireActivity().onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}
