package kevin.android.texts.Conversations;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

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

    public Conversation getConversationById(int id) {
        for (Conversation inactive : inactiveConversations) {
            if (inactive.getId() == id) return inactive;
        }
        for (Conversation active : activeConversations) {
            if (active.getId() == id) return active;
        }
        return null;
    }

    public void setInactiveConversations(List<Conversation> inactiveConversations) {
        this.inactiveConversations = inactiveConversations;
    }

    public void loadConversation(int id, boolean isSkip) {
//        Conversation next = inactiveConversations.get(0);
        for (int i = 0; i < inactiveConversations.size(); ++i) {
            Conversation c = inactiveConversations.get(i);
            if (c.getId() == id) {
                inactiveConversations.remove(i);
                Log.e(TAG, "Requested next conversation (SET TO RUNNING). name: " + c.getFullName() + ", id: " + c.getId());
                c.setActive(true);
                activeConversations.add(c);
                c.setConversationState(isSkip ? Conversation.STATE_PAUSED : Conversation.STATE_RUNNING);
                c.setLastMessage(isSkip ? "(Chapter skipped)" : "New message!");
                update(c);
            }
        }
    }

//    public LiveData<List<Conversation>> getAllConversations() {
//        return repository.getAllConversations();
//    }

    public void pauseAllActiveConversations() {
        for (Conversation c : activeConversations) {
            c.setConversationState(Conversation.STATE_PAUSED);
            update(c);
        }
    }

    public void incrementConversationWithID(int id, boolean isSkip) {
        for (Conversation c : activeConversations) {
            if (c.getId() == id) {
                c.setGroup(c.getGroup() + 1);
                c.setConversationState(isSkip ? Conversation.STATE_PAUSED : Conversation.STATE_RUNNING);
                c.setLastMessage(isSkip ? "(Chapter Skipped)" : "New message!");
                c.setUnread(true);
                update(c);
                Log.e(TAG, "conversation " + c.getFullName() + " is now on group " + c.getGroup());
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
