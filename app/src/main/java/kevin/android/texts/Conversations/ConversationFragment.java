package kevin.android.texts.Conversations;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kevin.android.texts.GameManager;
import kevin.android.texts.R;
import kevin.android.texts.SharedViewModel;

public class ConversationFragment extends Fragment implements EditTextDialog.EditTextDialogListener {
    private static final String TAG = "ConversationFragment";
    private ConversationViewModel conversationViewModel;
    private SharedViewModel sharedViewModel;
    // private FloatingActionButton testNextConversationButton;

    public ConversationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.conversation_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        final ConversationAdapter adapter = new ConversationAdapter();
        recyclerView.setAdapter(adapter);

        // request the conversation ViewModel from the system, owner is the underlying activity
        conversationViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(ConversationViewModel.class);
        // start observing the active conversation list
        conversationViewModel.getActiveConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            // when an update is changed
            public void onChanged(List<Conversation> activeConversations) {
                // update recycler view
                adapter.setActiveConversations(activeConversations);
                conversationViewModel.setActiveConversations(activeConversations);
                // Log.e(TAG, "active conversations updated");
            }
        });

        conversationViewModel.getInactiveConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                // Log.e(TAG, "set " + conversations.size() + " inactive conversations.");
                conversationViewModel.setInactiveConversations(conversations);
                if (conversations.size() == 2) checkAdvance();
            }
        });

        // shared ViewModel for the last message
        sharedViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(SharedViewModel.class);
        sharedViewModel.getCurrentRunning().observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                // update last message and read status
                conversationViewModel.update(conversation);
                if (conversation.getConversationState() == Conversation.STATE_DONE) {
                    // advance group
                    // not tested
                    conversation.setConversationState(Conversation.STATE_PAUSED);
                    checkAdvance();
                }
            }
        });


//        testNextConversationButton = view.findViewById(R.id.test_add_conversation_button);
//        testNextConversationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                conversationViewModel.loadNextConversation();
//            }
//        });

        // setup
//        if (GameManager.isFirstRun()) {
//            Log.e(TAG, "first run! Running setup.");
//            GameManager.setFirstRun(false);
//            final LiveData<List<Conversation>> liveData = conversationViewModel.getAllConversations();
//            liveData.observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
//                @Override
//                public void onChanged(List<Conversation> conversations) {
//                    // set the profile pictures
//                    for (Conversation conversation : conversations) {
//                        int id = conversation.getId();
//                        conversation.setProfilePictureID(Conversation.profilePictures[id - 1]);
//                    }
//                    // get all player and NPC information
//                    for (int i = conversations.size() - 1; i >= 0; i--) {
//                        Conversation conversation = conversations.get(i);
//                        EditTextDialog editTextDialog = new EditTextDialog("Enter player information",
//                                conversation.getFirstName(), conversation.getLastName(), conversation.getNickname(), conversation.getId());
//                        editTextDialog.show(getChildFragmentManager(), "setup");
//                    }
//                Log.e(TAG, "opened all EditTextDialog windows.");
                    // we don't need to observe all conversations anymore
//                    liveData.removeObserver(this);
//                }
//            });
//        }
    }

    @Override
    public void applyNames(final String firstName, final String lastName, final String nickname, int id) {
        final LiveData<Conversation> liveData = conversationViewModel.getConversationById(id);
        liveData.observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                conversation.setFirstName(firstName);
                conversation.setLastName(lastName);
                conversation.setNickname(nickname);
                conversation.setInitialized(true);
                conversationViewModel.update(conversation);
//                Log.e(TAG, "initialized conversation. name: " + conversation.getFullName());
                liveData.removeObserver(this);
            }
        });
    }

    private void checkAdvance() {
        String cmd = GameManager.timeline.get(0);
        Log.e(TAG, "command: " + cmd);
        GameManager.timeline.remove(0);
        if (cmd.substring(0, 4).equals("open")) {
            Log.e(TAG, "opened next conversation of id: " + cmd.charAt(4));
            conversationViewModel.loadNextConversation();
        } else if (cmd.substring(0, 4).equals("incr")) {
            int conversationId = cmd.charAt(4) - '0';
            Log.e(TAG, "incremented conversation with id: " + conversationId);
            conversationViewModel.incrementConversationWithID(conversationId);
        }
    }
}
