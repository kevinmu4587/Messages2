package kevin.android.texts.Conversations;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kevin.android.texts.ChatInfoFragment;
import kevin.android.texts.GameManager;
import kevin.android.texts.Message.ChatFragment;
import kevin.android.texts.Message.ChatFragmentDirections;
import kevin.android.texts.R;

public class ConversationFragment extends Fragment implements EditTextDialog.EditTextDialogListener {
    private static final String TAG = "ConversationFragment";
    private ConversationViewModel conversationViewModel;
    SharedPreferences settingsSharedPref;
//    MenuItem endingsGuide;
    // private FloatingActionButton testNextConversationButton;

    public ConversationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        getActivity().setTitle("Schism");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
                if (activeConversations.size() == 0) {
                    Log.e(TAG, "waiting for first conversations to arrive");
                } else {
                    // update recycler view
                    adapter.setActiveConversations(activeConversations);
                    conversationViewModel.setActiveConversations(activeConversations);
                    for (Conversation c : activeConversations) {
                        GameManager.setNPCNames(c.getId(), c.getFirstName(), c.getLastMessage(), c.getNickname());
                    }
                    Log.e(TAG, "active conversations updated");
                }
            }
        });

        conversationViewModel.getInactiveConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                // Log.e(TAG, "set " + conversations.size() + " inactive conversations.");
                conversationViewModel.setInactiveConversations(conversations);
                // load the first conversation at the start
                if (conversations.size() == GameManager.numConversations) {
                    for (int i = conversations.size() - 1; i >= 0; i--) {
                        Conversation conversation = conversations.get(i);
                        if (conversation.isEditable()) {
                            EditTextDialog editTextDialog = new EditTextDialog(conversation.getConversationDialogTitle(),
                                    conversation.getFirstName(), conversation.getLastName(), conversation.getNickname(), conversation.getId());
                            editTextDialog.show(getChildFragmentManager(), "setup");
                        }
                    }
                    EditTextDialog editTextDialog = new EditTextDialog("Enter your player information:",
                            "Oliver", "Green", "Oli", -1);
                    editTextDialog.show(getChildFragmentManager(), "setup");
                    // Log.e(TAG, "opened all EditTextDialog windows.");
                    checkAdvance();
                }
            }
        });


        // get data back from ChatInfoFragment
        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<Conversation> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData(ChatFragment.CHAT_FRAGMENT_KEY);
        liveData.observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                Log.e(TAG, "Received updated conversation from ChatFragment");
                conversationViewModel.update(conversation);
                Log.e(TAG, "Conversation returned a state of " + conversation.getConversationState());
                if (conversation.getConversationState() == Conversation.STATE_DONE) {
                    // advance group
                    Log.e(TAG, "Conversation " + conversation.getFullName() + " set to PAUSED");
                    conversation.setConversationState(Conversation.STATE_PAUSED);
                    conversationViewModel.update(conversation);
                    checkAdvance();
                } else if (conversation.getConversationState() == Conversation.STATE_PAUSED) {
                    conversation.setUnread(false);
                    conversationViewModel.update(conversation);
                }
            }
        });

        // retrieve stored values from settings fragment
        settingsSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String playerFirstName = settingsSharedPref.getString("playerFirstName", GameManager.playerFirstName);
        String playerLastName = settingsSharedPref.getString("playerLastName", GameManager.playerLastName);
        String playerNickname = settingsSharedPref.getString("playerNickname", GameManager.playerNickname);
        boolean isFastMode = settingsSharedPref.getBoolean("isFastMode", false);
        boolean isAutoMode = settingsSharedPref.getBoolean("isAutoMode", false);
        GameManager.playerFirstName = playerFirstName;
        GameManager.playerLastName = playerLastName;
        GameManager.playerNickname = playerNickname;
        GameManager.isFastMode = isFastMode;
        GameManager.isAutoMode = isAutoMode;


//        testNextConversationButton = view.findViewById(R.id.test_add_conversation_button);
//        testNextConversationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                conversationViewModel.loadNextConversation();
//            }
//        });

    }

    @Override
    public void applyNames(final String firstName, final String lastName, final String nickname, final int id) {
        if (id == -1) {
            SharedPreferences.Editor editor = settingsSharedPref.edit();
            GameManager.playerFirstName = firstName;
            GameManager.playerLastName = lastName;
            GameManager.playerNickname = nickname;
            Log.e(TAG, "Writing player names to settings sharedPreferences");
            editor.putString("playerFirstName", GameManager.playerFirstName);
            editor.putString("playerLastName", GameManager.playerLastName);
            editor.putString("playerNickname", GameManager.playerNickname);
            editor.commit();
            return;
        }
        final LiveData<Conversation> liveData = conversationViewModel.getConversationById(id);
        liveData.observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                Log.e(TAG, "Updating names for ID #" + id + " , their name is " + firstName);
                conversation.setFirstName(firstName);
                conversation.setLastName(lastName);
                conversation.setNickname(nickname);
                GameManager.setNPCNames(id, firstName, lastName, nickname);
                conversation.setInitialized(true);
                conversationViewModel.update(conversation);
//                Log.e(TAG, "initialized conversation. name: " + conversation.getFullName());
                liveData.removeObserver(this);
            }
        });
    }

    private void checkAdvance() {
        if (GameManager.timeline.size() == 0) {
            // trigger the endgame
            GameManager.gameCompleted = true;
            getActivity().invalidateOptionsMenu();
//            endingsGuide.setVisible(true);
            return;
        }
        String cmd = GameManager.timeline.get(0);
        Log.e(TAG, "command: " + cmd);
        // timeline is saved by shared preferences in MainActivity
        GameManager.timeline.remove(0);
        if (cmd.substring(0, 4).equals("open")) {
            int conversationId = cmd.charAt(4) - '0';
            Log.e(TAG, "opened next conversation of id: " + conversationId);
            conversationViewModel.loadConversation(conversationId);
        } else if (cmd.substring(0, 4).equals("incr")) {
            String[] incrementInstructions = cmd.split("_");
            int conversationIdToIncrement = 0;
            if (incrementInstructions.length == 1) {
                conversationIdToIncrement = cmd.charAt(4) - '0';
            } else {
                String[] conditionParts = incrementInstructions[1].split("=");
                String keyBlockName = conditionParts[0];
                int blockCondition = Integer.parseInt(conditionParts[1]);
                if (GameManager.getKeyDecision(keyBlockName) == blockCondition) {
                    // value if true
                    conversationIdToIncrement = Integer.parseInt(incrementInstructions[2]);
                } else {
                    // value if false
                    // only read the first character since the last instruction has a /r terminator character
                    conversationIdToIncrement = Integer.parseInt(incrementInstructions[3].substring(0, 1));
                }
            }
            Log.e(TAG, "incremented conversation with id: " + conversationIdToIncrement);
            conversationViewModel.incrementConversationWithID(conversationIdToIncrement);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_conversation_fragment, menu);
        MenuItem endingsGuide = menu.findItem(R.id.menu_endings_guide);
        Log.e(TAG, "Is game completed? " + GameManager.gameCompleted);
        endingsGuide.setVisible(GameManager.gameCompleted);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        switch (item.getItemId()) {
            case R.id.menu_open_settings:
                navController.navigate(ConversationFragmentDirections.actionConversationFragmentToSettingsFragment());
                return true;
            case R.id.menu_endings_guide:
                navController.navigate(ConversationFragmentDirections.actionConversationFragmentToEndgameFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
