package kevin.android.texts.Message;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kevin.android.texts.ChatInfoFragment;
import kevin.android.texts.Conversations.Conversation;
import kevin.android.texts.Conversations.ConversationFragmentDirections;
import kevin.android.texts.Dialog;
import kevin.android.texts.GameManager;
import kevin.android.texts.R;
import kevin.android.texts.SharedViewModel;

public class ChatFragment extends Fragment implements View.OnClickListener, Dialog.DialogListener {
    private MessageViewModel messageViewModel;
    private SharedViewModel sharedViewModel;
    private Button sendButton;
    private EditText chatBox;
    private PlayRunnable playRunnable;

    private String state = "npc";
    private int lastPlayerChoice = 0;
    private Message nextMessage;
    private Conversation conversation;

    private static final String TAG = "ChatFragment";

    // required empty constructor
    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        sendButton = view.findViewById(R.id.chat_send_button);
        chatBox = view.findViewById(R.id.chat_edittext);
        sendButton.setOnClickListener(this);
        chatBox.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        final MessageAdapter adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        // for updating the shared ViewModel
        sharedViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(SharedViewModel.class);
        // get the conversation object
        conversation = ChatFragmentArgs.fromBundle(getArguments()).getConversation();
        sharedViewModel.setCurrentRunning(conversation);
        getActivity().setTitle(conversation.getFullName());
        Log.e(TAG, "Conversation Owner: " + conversation.getFullName());


        // observe the sent messages
        messageViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(MessageViewModel.class);
        Log.e(TAG, "id: " + conversation.getId() + " group: " + conversation.getGroup());
        messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
        messageViewModel.loadSentMessages(conversation.getId(), conversation.getGroup());
        messageViewModel.getSentMessages().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> notes) {
                // update recycler view
                adapter.setSentMessages(notes);
                Log.e(TAG, "Chat updated");
            }
        });

        // observe the upcoming messages
        messageViewModel.loadUpcomingMessages(conversation.getId(), conversation.getGroup());
        messageViewModel.getUpcomingMessages().
                observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                Log.e(TAG, "loaded " + messages.size() + " upcoming messages from block " + conversation.getCurrentBlock());
                messageViewModel.setUpcomingMessages(messages);
                if (messages.size() == 0 && playRunnable != null) {
                    if (conversation.getCurrentBlock() == 0) {
                        // we already finished this chat
                        conversation.setConversationState(Conversation.STATE_DONE);
                        sharedViewModel.setCurrentRunning(conversation);
                        playRunnable.finished = true;
                        Log.e(TAG, "Chat is finished, playRunnable killed");
                    } else {
                        int top = conversation.popTopBlock();
                        messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
                        Log.e(TAG, "popped block " + top);
                    }
                } else if (playRunnable == null) {
                    conversation.setConversationState(Conversation.STATE_RUNNING);
                    playRunnable = new PlayRunnable();
                    new Thread(playRunnable).start();
                }
            }
        });

        // get data back from ChatInfoFragment
                NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<Conversation> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData(ChatInfoFragment.CHAT_INFO_KEY);
        liveData.observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                Log.e(TAG, "Received updated conversation from ChatInfoFragment");
                ChatFragment.this.conversation = conversation;
                getActivity().setTitle(conversation.getFullName());
            }
        });
    }

    @Override
    public void onDestroyView() {
        playRunnable.finished = true;
        playRunnable = null;
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_send_button:
                if (state.equals("sending")) {
                    Log.e(TAG, "Sending my message now.");
                    state = "sent";
                    submitMessage();
                    chatBox.setText("");
                }
                return;
            case R.id.chat_edittext:
                if (state.equals("choose")) {
                    openDialog();
                }
        }
    }

    /*
        - open the options dialog
     */
    public void openDialog() {
        Dialog dialog = new Dialog(nextMessage.getContent());
        dialog.show(getChildFragmentManager(), "options");
    }

    /*
        - set the player choice
        - fill the chat box with the selected choice of text
     */
    @Override
    public void applyText(int choice) {
        lastPlayerChoice = choice;
        nextMessage.setChoice(choice);
        chatBox.setText(nextMessage.getContent()[choice]);
        // if it was a key decision, add it to the gameManager
        state = "sending";
        String type = nextMessage.getType();
        if (type.length() >= 3 && type.substring(0, 3).equals("key")) {
            String blockName = type.substring(4);
            GameManager.addKeyDecision(blockName, choice);
            Log.e(TAG, "Added key decision " + blockName + " with choice " + choice);
            nextMessage.setType("my");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open_chat_info:
                // navigate to ChatInfoFragment
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(ChatFragmentDirections.actionChatFragmentToChatInfoFragment(conversation));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submitMessage() {
        if (nextMessage.getContent().length > 1) {
            nextMessage.setChoice(lastPlayerChoice);
        }
        Log.e(TAG, "Submitted message " + nextMessage.getContent()[nextMessage.getChoice()]);
        messageViewModel.submitMessage(nextMessage);

        String lastMessage = nextMessage.getContent()[nextMessage.getChoice()];
        if (nextMessage.getType().equals("npc")) {
            lastMessage = conversation.getFirstName() + ": " + lastMessage;
            conversation.setUnread(true);
        } else {
            lastMessage = "You: " + lastMessage;
            conversation.setUnread(false);
        }
        conversation.setLastMessage(lastMessage);
        sharedViewModel.setCurrentRunning(conversation);
    }

    /*
        - read messages in upcoming messages and add them to the screen
     */
    class PlayRunnable implements Runnable {
        private static final String TAG = "PlayRunnable";
        public boolean finished = false;

        @Override
        public void run() {
            while (!finished) {
                if (state.equals("sending") || state.equals("choose")) {
                    // wait, we are waiting for user input
                    Log.e(TAG, "Awaiting user input. state: " + state);
                    sleep(1000);
                    continue;
                } else if (state.equals("sent")) {
                    Log.e(TAG, "message sent, waiting some time");
                    sleep(2000);
                    state = "npc";
                }
                nextMessage = messageViewModel.getNextMessage();
                if (nextMessage == null) {
                    if (finished) {
                        break;
                    }
                    sleep(2000);
                } else {
                    if (nextMessage.getBlock() != conversation.getCurrentBlock()) {
                        sleep(2000);
                        continue;
                    }
                    String type = nextMessage.getType();
                    if (type.equals("npc") && !state.equals("choose")) {
//                        Log.e(TAG, "npc message: " + nextMessage.getContent()[0] + " , adding now.");
//                        if (nextMessage.getContent().length > 1) {
//                            nextMessage.setChoice(lastPlayerChoice);
//                        }
                        submitMessage();
                        sleep(2000);
                    } else if (type.equals("my") || type.substring(0, 3).equals("key")) {
                        // or if it is a key decision
                        Log.e(TAG, "Found a my message, " + nextMessage.getContent()[0] + " awaiting input");
                        // get the choice by opening the dialog
                        state = "choose";
                    } else if (type.equals("block")) {
                        String blockName = nextMessage.getContent()[0];
                        int blockChoice = GameManager.getKeyDecision(blockName) + 1;
                        conversation.pushBlock(blockChoice);
//                        conversation.setCurrentBlock(blockChoice);
                        messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
                        Log.e(TAG, "found block " + blockName + ", choice " + blockChoice + ". return to: " + nextMessage.getBlock());
//                        GameManager.setReturnToBlock(nextMessage.getBlock());
//                        GameManager.addPrecedingBlock(nextMessage.getBlock());
                        messageViewModel.removeNextMessage();
                        messageViewModel.loadUpcomingMessages(conversation.getId(), conversation.getGroup());
                    }
                    // if it is a block
                    // get the decision of the block from game manager
                    // set the conversation's current block to the decision
                }
            }
        }

        private void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
