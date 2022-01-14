package kevin.android.texts.Conversations;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kevin.android.texts.Message.Message;
import kevin.android.texts.Message.MessageRepository;

public class ConversationViewModel extends AndroidViewModel {
    private static final String TAG = "ConversationViewModel";
    private ConversationRepository repository;
    private List<Conversation> inactiveConversations = new ArrayList<>();
    private List<Conversation> activeConversations = new ArrayList<>();

    public ConversationViewModel(@NonNull Application application) {
        super(application);
        repository = new ConversationRepository(application);
    }

    public void update(Conversation conversation) {
        repository.update(conversation);
    }

    public LiveData<List<Conversation>> getActiveConversations() {
        return repository.getActiveConversations();
    }

    public void setActiveConversations(List<Conversation> activeConversations) {
        this.activeConversations = activeConversations;
    }

    public LiveData<List<Conversation>> getInactiveConversations() {
        return repository.getInactiveConversations();
    }

    public LiveData<Conversation> getConversationById(int id) {
        return repository.getConversationById(id);
    }

    public void setInactiveConversations(List<Conversation> inactiveConversations) {
        this.inactiveConversations = inactiveConversations;
    }

    public void loadNextConversation() {
        Conversation next = inactiveConversations.get(0);
        Log.e(TAG, "Requested next conversation. name: " + next.getFullName() + ", id: " + next.getId());
        next.setActive(true);
        update(next);
    }

    public LiveData<List<Conversation>> getAllConversations() {
        return repository.getAllConversations();
    }

    public void incrementConversationWithID(int id) {
        for (Conversation c : activeConversations) {
            if (c.getId() == id) {
                c.setGroup(c.getGroup() + 1);
                Log.e(TAG, "conversation " + c.getFullName() + " is now on group " + c.getGroup());
                update(c);
                return;
            }
        }
    }

//    public void bubbleToTop(Conversation conversation) {
//        List<Conversation> list = activeConversations.getValue();
////        Log.e(TAG, "size: " + list.size());
//        for (int i = 0; i < list.size(); ++i) {
//            if (list.get(i).getId() == conversation.getId()) {
//                for (int k = i; k > 0; k--) {
//                    //Log.e(TAG, "k: " + k);
//                    int temp = list.get(k).getRecentValue();
//                    list.get(k).setRecentValue(list.get(k-1).getRecentValue());
//                    list.get(k-1).setRecentValue(temp);
//                    repository.update(list.get(k));
//                }
//                repository.update(list.get(0));
////                for (Conversation c : list) {
////                    Log.e(TAG, c.getFullName() + " has recentValue: " + c.getRecentValue());
////                }
//                break;
//            }
//        }
//    }

}
