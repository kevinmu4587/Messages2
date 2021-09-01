package kevin.android.texts.Message;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository repository;
    private LiveData<List<Message>> liveUpcomingMessages;
    private List<Message> upcomingMessages = new ArrayList<>();
    private LiveData<List<Message>> allMessages;

    private static final String TAG = "MessageViewModel";

    public MessageViewModel(@NonNull Application application) {
        super(application);
        repository = new MessageRepository(application);
        //sentMessages = repository.getSentMessages(1, 1);
        allMessages = repository.getAllMessages();
    }

    public void update(Message message) {
        repository.update(message);
        Log.e("MessageViewModel", "Updated the Database");
    }

    public LiveData<List<Message>> getUpcomingMessages(int owner, int group, int block) {
        Log.e(TAG, "loading messages from block " + block);
        return repository.getUpcomingMessages(owner, group, block);
    }

    public LiveData<List<Message>> getSentMessages(int owner, int group, int block) {
        //return sentMessages;
//        upcomingMessages = repository.getUpcomingMessages(owner, group);
        return repository.getSentMessages(owner, group, block);
    }

    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    public void setUpcomingMessages(List<Message> upcomingMessages) {
        this.upcomingMessages = upcomingMessages;
    }

    public Message getNextMessage() {
        if (upcomingMessages.size() == 0) {
            Log.e("MessageViewModel", "No more upcoming messages");
            return null;
        }
        Message next = upcomingMessages.get(0);
        return next;
    }

    public void submitMessage(Message next) {
        next.setSent(true);
        upcomingMessages.remove(0);
        update(next);
    }

    public void removeNextMessage() {
        upcomingMessages.remove(0);
    }
}
