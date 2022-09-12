package kevin.android.texts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import kevin.android.texts.Conversations.Conversation;
import kevin.android.texts.Message.ChatFragmentDirections;

public class ChatInfoFragment  extends Fragment {
    private ImageView profilePicture;
    private EditText editName;
    // private EditText editNickname;
    private TextView description;

    private Conversation conversation;
    public static final String CHAT_INFO_KEY = "chat_info_key";

    public ChatInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_info, container, false);
        profilePicture = view.findViewById(R.id.chat_info_profile_picture);
        editName = view.findViewById(R.id.chat_info_name);
        // editNickname = view.findViewById(R.id.chat_info_nickname);
        description = view.findViewById(R.id.chat_info_description);
        getActivity().setTitle("Character Information");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversation = ChatInfoFragmentArgs.fromBundle(getArguments()).getConversation();
        editName.setText(conversation.getFullName());
        profilePicture.setImageResource(conversation.getProfilePictureID());
        description.setText(Utils.replaceName(conversation.getDescription()));


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // send data back to ChatFragment
                String[] firstAndLast = editName.getText().toString().split(" ");
                conversation.setFirstName(firstAndLast[0]);
                conversation.setLastName("");
                if (firstAndLast.length > 1) conversation.setLastName(firstAndLast[1]);
                GameManager.setNPCNames(conversation.getId(), firstAndLast[0], firstAndLast[1], conversation.getNickname());
                NavController navController = NavHostFragment.findNavController(ChatInfoFragment.this);
                navController.getPreviousBackStackEntry().getSavedStateHandle().set(CHAT_INFO_KEY, conversation);
                this.setEnabled(false);
                requireActivity().onBackPressed();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
