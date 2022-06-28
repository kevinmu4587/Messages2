package kevin.android.texts.Message;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kevin.android.texts.ChatInfoFragment;
import kevin.android.texts.Conversations.Conversation;
import kevin.android.texts.Dialog;
import kevin.android.texts.GameManager;
import kevin.android.texts.R;
import kevin.android.texts.SharedViewModel;
import kevin.android.texts.Utils;

public class ChatFragment extends Fragment implements View.OnClickListener, Dialog.DialogListener {
    private MessageViewModel messageViewModel;
    private SharedViewModel sharedViewModel;
    private MessageAdapter adapter = null;
    private RecyclerView recyclerView;
    private Handler mainHandler = new Handler();

    private Button sendButton;
    private EditText chatBox;
    private PlayRunnable playRunnable;
    private FloatingActionButton jumpDownButton;
    private FloatingActionButton testFinishGroup;
    private LinearLayout noteContainer;
    private TextView noteContent;
    private TextView noteNext;
    private ImageView background;

    private String state = "npc";
    private int lastPlayerChoice = 0;
    private Message nextMessage;
    private Conversation conversation;

    // for playing sounds
    private SoundPool soundPool;
    private int npcTypingSound, sendMessageSound, receivedMessageSound;

    private Animation fadeIn;
    private Animation fadeOut;

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
        jumpDownButton = view.findViewById(R.id.chat_jump_button);
        testFinishGroup = view.findViewById(R.id.test_finish_group);
        noteContainer = view.findViewById(R.id.note_message_container);
        noteContent = view.findViewById(R.id.note_content);
        noteNext = view.findViewById(R.id.note_await_next);
        background = view.findViewById(R.id.chat_background);

        sendButton.setOnClickListener(this);
        chatBox.setOnClickListener(this);
        jumpDownButton.setOnClickListener(this);
        testFinishGroup.setOnClickListener(this);
        noteContainer.setOnClickListener(this);

        // manage sound effects
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        npcTypingSound = soundPool.load(getContext(), R.raw.typing, 1);
        sendMessageSound = soundPool.load(getContext(), R.raw.send_message, 1);
        receivedMessageSound = soundPool.load(getContext(), R.raw.receive_message, 1);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // for updating the shared ViewModel
        sharedViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(SharedViewModel.class);
        // get the conversation object
        conversation = ChatFragmentArgs.fromBundle(getArguments()).getConversation();
        sharedViewModel.setCurrentRunning(conversation);
        getActivity().setTitle(conversation.getFullName());
        // Log.e(TAG, "Conversation Owner: " + conversation.getFullName());

        recyclerView = view.findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MessageAdapter(conversation.getProfilePictureID());
        recyclerView.setAdapter(adapter);

        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.slow_fade_in);
        fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.slow_fade_out);

        // observe the sent messages
        messageViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(MessageViewModel.class);
        Log.e(TAG, "id: " + conversation.getId() + " group: " + conversation.getGroup());
        messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
        messageViewModel.getSentMessages(conversation.getId(), conversation.getGroup()).observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> notes) {
                // update recycler view
                if (notes.size() > 0) {
                    adapter.setSentMessages(notes);
                    checkScroll();
                }
            }
        });

        Log.e(TAG, "Conversation state stars as " + conversation.getConversationState());
        if (conversation.getConversationState() == Conversation.STATE_RUNNING) {
            // observe the upcoming messages
            messageViewModel.loadUpcomingMessages(conversation.getId(), conversation.getGroup());
            messageViewModel.getUpcomingMessages().
                    observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
                        @Override
                        public void onChanged(List<Message> messages) {
                            // Log.e(TAG, "loaded " + messages.size() + " upcoming messages");
                            messageViewModel.setUpcomingMessages(messages);
                            if (messages.size() == 0 && playRunnable != null && !state.equals("waiting")) {
                                if (conversation.getCurrentBlock() == 0) {
                                    // we already finished this chat
                                    conversation.setConversationState(Conversation.STATE_DONE);
                                    conversation.setUnread(false);
                                    sharedViewModel.setCurrentRunning(conversation);
                                    playRunnable.finished = true;
                                    Log.e(TAG, "Chat is finished, playRunnable killed. Set conversation state to DONE");
                                } else {
                                    // when we finish the current block
                                    int top = conversation.popTopBlock();
                                    messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
                                    Log.e(TAG, "popped block " + top);
                                }
                            }
                        }
                    });
            // start the background thread
            playRunnable = new PlayRunnable();
            new Thread(playRunnable).start();
        }

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

        //scroll to bottom if keyboard appears
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter.getItemCount() != 0) {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (playRunnable != null) playRunnable.finished = true;
        playRunnable = null;
//        Log.e(TAG, "Leaving chat fragment with blocks " + Arrays.toString(Utils.listToIntArray(conversation.getCurrentBlocks())));
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_send_button:
                if (state.equals("sending")) {
                    state = "sent";
                    submitMessage();
                    chatBox.setText("");
                    soundPool.play(sendMessageSound, 0.5f, 0.5f, 0, 0, 1);
                }
                return;
            case R.id.chat_edittext:
                if (state.equals("choose")) {
                    openDialog();
                }
                return;
            case R.id.chat_jump_button:
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                jumpDownButton.setVisibility(View.INVISIBLE);
                return;
            case R.id.test_finish_group:
                conversation.setConversationState(Conversation.STATE_DONE);
                playRunnable.finished = true;
                Log.e(TAG, "playrunnable killed, group finished early.");
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
        // add the message
        if (nextMessage.getContent().length > 1) {
            nextMessage.setChoice(lastPlayerChoice);
        }
        // replace any name placeholders
        nextMessage.replaceNamePlaceholders();
        // Log.e(TAG, "Submitted message " + nextMessage.getContent()[nextMessage.getChoice()]);
        messageViewModel.submitMessage(nextMessage);

        // update sharedViewModel for the last message
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

    private void checkScroll() {
        // scroll down to bottom if needed
        if (adapter.getItemCount() != 0) {
            if (!recyclerView.canScrollVertically(1)) {
                // jump to bottom
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
                jumpDownButton.setVisibility(View.INVISIBLE);
                // Log.e(TAG, "cannot scroll vertically. num sent messages: " + adapter.getItemCount());
            } else {
                // make jump button appear
                jumpDownButton.setVisibility(View.VISIBLE);
            }
        }
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
                if (Utils.contains(state, new String[]{"sending", "choose", "setting background"})) {
//                if (state.equals("sending") || state.equals("choose") || state.equals("setting background")) {
                    // wait, we are waiting for user input
//                    Log.e(TAG, "Runnable paused. state: " + state);
                    sleep(1000);
                    continue;
                } else if (state.equals("sent")) {
                    // Log.e(TAG, "message sent, waiting some time");
                    sleep(GameManager.isFastMode ? 500 : 2000);
                    state = "can load next message";
                    continue;
                } else if (state.equals("note")) {
                    // A note is currently running. we shouldn't load more messages until it is done.
                    sleep(2000);
                    continue;
                } else if (state.equals("note finished")) {
                    messageViewModel.submitMessage(nextMessage);
                    state = "note submitted";
                }
                nextMessage = messageViewModel.getNextMessage();
                if (nextMessage == null) {
                    // the thread is over, conversation ended
                    if (finished) {
                        Log.e(TAG, "No more upcoming messages since Thread is over");
                        break;
                    }
                    // at the start, maybe the upcoming messages haven't loaded yet
                    Log.e(TAG, "Waiting for the first upcoming messages to arrive");
                    state = "waiting";
                    sleep(2000);
                    continue;
                }
                if (nextMessage.getBlock() != conversation.getCurrentBlock()) {
                    sleep(2000);
                    Log.e(TAG, "current message does not match block; waiting for correct ones to load in");
                    continue;
                }
                String type = nextMessage.getType();
                if (type.equals("npc") && !state.equals("choose")) {
                    state = "npc";
                    typingAnim();   // an npc message is next, so do a typing animation
                    submitMessage();    // submit the npc messages
                    soundPool.play(receivedMessageSound, 1, 1, 0, 0, 1);
                    sleep(1500);
                } else if (type.equals("my") || type.substring(0, 3).equals("key")) {
                    // or if it is a key decision
                    // get the choice by opening the dialog
                    state = "choose";
                } else if (type.equals("block")) {
                    String blockName = nextMessage.getContent()[0];
                    int blockChoice = GameManager.getKeyDecision(blockName) + 1;
                    conversation.pushBlock(blockChoice);
                    messageViewModel.setCurrentBlocks(conversation.getCurrentBlocks());
                    if (!finished) {
                        messageViewModel.submitMessage(nextMessage);  // get rid of the message of type 'block'
                        messageViewModel.loadUpcomingMessages(conversation.getId(), conversation.getGroup());
                        Log.e(TAG, "found block " + blockName + ", choice " + blockChoice);
                    }
                } else if (type.equals("action")) {
                    submitMessage();
                    sleep(1500);
                } else if (type.equals("note")) {
                    state = "note";
                    displayNote();
                }
//                    } else if (type.equals("background")) {
//                        manageBackgrounds();
//                        state = "setting background";
//                    }
            }
        }

        private void typingAnim() {
            // Log.e(TAG, "started typing anim");
            // add typing gif
            adapter.getSentMessages().add(null);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                    checkScroll();
                }
            });
            soundPool.play(npcTypingSound, 1, 1, 0, 0, 1);
            // sleep as NPC is typing
            sleep(GameManager.isFastMode ? 750 : 3000);
            adapter.getSentMessages().remove(adapter.getItemCount() - 1);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRemoved(adapter.getItemCount());
                }
            });
            // Log.e(TAG, "finished typing anim");
        }

        private void displayNote() {
            final int TEXT_DISPLAYED = 0;
            final int AWAIT_NEXT = 1;

            // hide the keyboard and action bar
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            noteContainer.setAnimation(fadeIn);
            fadeIn.start();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    final String [] notes = nextMessage.getContent();
                    noteContainer.setVisibility(View.VISIBLE);
                    noteContent.setText(notes[0]);
                    noteContainer.setOnClickListener(new View.OnClickListener() {
                        private int noteState = TEXT_DISPLAYED;
                        private int curNote = 1;

                        @Override
                        public void onClick(View view) {
                            if (state.equals("note finished")) return;
                            if (curNote >= notes.length) {
                                // exit the note display
                                noteContainer.setVisibility(View.INVISIBLE);
                                // messageViewModel.submitMessage(nextMessage);
                                noteContainer.setAnimation(fadeOut);
                                fadeOut.start();
                                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                                state = "note finished";
                            } else if (noteState == TEXT_DISPLAYED) {
                                noteState = AWAIT_NEXT;
                                noteNext.setVisibility(View.VISIBLE);
                            } else if (noteState == AWAIT_NEXT) {
                                noteNext.setVisibility(View.INVISIBLE);
                                noteState = TEXT_DISPLAYED;
                                noteContent.setText(notes[curNote]);
                                curNote++;
                            }
                        }
                    });
                }
            });
        }

        /*
        private void manageBackgrounds() {
            if (nextMessage == null) return;
            String cmd = nextMessage.getContent()[0];
            if (!conversation.getBgState().equals("")) {
                // render old background from previous load
                cmd = conversation.getBgState();
                Log.e(TAG, "loading previous BG");
            }
            if (cmd.equals("fadeout")) {
                // fadeout existing background
                Log.e(TAG, "fading background away");
                background.setAnimation(fadeOut);
                fadeOut.start();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        background.setImageResource(0);
                    }
                });
                return;
            }
            // load a new background
            Log.e(TAG, "loading new background");
            final int bgID = GameManager.getBackgroundIDByName(cmd);
            conversation.setBgState(cmd);
            sharedViewModel.setCurrentRunning(conversation);
            messageViewModel.submitMessage(nextMessage);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    background.setImageResource(bgID);
                }
            });
            state = "";
        }
         */

        private void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
