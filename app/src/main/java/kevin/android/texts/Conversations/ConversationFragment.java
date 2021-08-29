package kevin.android.texts.Conversations;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import kevin.android.texts.R;
import kevin.android.texts.SharedViewModel;

public class ConversationFragment extends Fragment {
    private static final String TAG = "ConversationFragment";
    private ConversationViewModel conversationViewModel;
    private SharedViewModel sharedViewModel;

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
        // start observing the conversation list
        conversationViewModel.getActiveConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            // when an update is changed
            public void onChanged(List<Conversation> activeConversations) {
                // update recycler view
                adapter.setActiveConversations(activeConversations);
//                Log.e(TAG, "conversations updated");
            }
        });

        // ViewModel for the last message
        sharedViewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.
                getInstance(getActivity().getApplication())).get(SharedViewModel.class);
        sharedViewModel.getCurrentRunning().observe(getViewLifecycleOwner(), new Observer<Conversation>() {
            @Override
            public void onChanged(Conversation conversation) {
                if (conversation.getConversationState() == Conversation.STATE_DONE) {
                    conversation.setConversationState(Conversation.STATE_RUNNING);
                    conversation.setGroup(conversation.getGroup() + 1);
                }
                conversationViewModel.update(conversation);
            }
        });
    }
}
