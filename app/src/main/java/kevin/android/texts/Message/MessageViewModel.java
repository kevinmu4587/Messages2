package kevin.android.texts.Message;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kevin.android.texts.Utils;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository repository;
    private LiveData<List<Message>> liveUpcomingMessages;
    private List<Message> upcomingMessages = new ArrayList<>();
    private LiveData<List<Message>> allMessages;
    private MutableLiveData<List<Integer>> liveCurrentBlocks = new MutableLiveData<>();

    private static final String TAG = "MessageViewModel";

    public MessageViewModel(@NonNull Application application) {
        super(application);
        repository = new MessageRepository(application);
        allMessages = repository.getAllMessages();
    }

    public void update(Message message) {
        repository.update(message);
//        Log.e("MessageViewModel", "Updated the Database");
    }

    public void loadUpcomingMessages(final int owner, final int group) {
        liveUpcomingMessages = Transformations.switchMap(
                liveCurrentBlocks,
                new Function<List<Integer>, LiveData<List<Message>>>() {
                    @Override
                    public LiveData<List<Message>> apply(List<Integer> blocks) {
                        // load from the last block number on the blocks stack
                        int block = blocks.get(blocks.size() - 1);
                        Log.e(TAG, "loading upcoming messages from block " + block + ", group: " + group + ". all current blocks: " +
                                Arrays.toString(Utils.listToIntArray(blocks)));
                        return repository.getUpcomingMessages(owner, group, block);
                    }
                });
    }

    public void setCurrentBlocks(List<Integer> blocks) {
        liveCurrentBlocks.postValue(blocks);
        // Log.e(TAG, "posted current blocks: " + Arrays.toString(Utils.listToIntArray(blocks)));
    }

    public LiveData<List<Message>> getUpcomingMessages() {
        return liveUpcomingMessages;
    }

    public LiveData<List<Message>> getSentMessages(int owner, int group) {
        if (liveCurrentBlocks.getValue() != null) {
            Log.e(TAG, "getting sent messages from blocks " + Arrays.toString(Utils.listToIntArray(liveCurrentBlocks.getValue())));
        }
        return repository.getSentMessages(owner, group);
    }

//    public LiveData<List<Message>> getAllMessages() {
//        return allMessages;
//    }

    public void setUpcomingMessages(List<Message> upcomingMessages) {
        this.upcomingMessages = upcomingMessages;
    }

    public Message getNextMessage() {
        if (upcomingMessages.size() == 0) return null;
        return upcomingMessages.get(0);
    }

    public void submitMessage(Message next) {
        if (next == null || upcomingMessages.size() == 0) return;
        next.setSent(true);
        upcomingMessages.remove(0);
        update(next);
    }
}
