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

import kevin.android.texts.GameManager;
import kevin.android.texts.Utils;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository repository;
    private LiveData<List<Message>> liveUpcomingMessages;
    private LiveData<List<Message>> liveSentMessages;
    private List<Message> upcomingMessages = new ArrayList<>();
    private LiveData<List<Message>> allMessages;
    private MutableLiveData<List<Integer>> liveCurrentBlocks = new MutableLiveData<>();

    private static final String TAG = "MessageViewModel";

    public MessageViewModel(@NonNull Application application) {
        super(application);
        repository = new MessageRepository(application);
        //sentMessages = repository.getSentMessages(1, 1);
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
                        int block = blocks.get(blocks.size() - 1);
                        Log.e(TAG, "loading upcoming messages from block " + block + ". all current blocks: " +
                                Arrays.toString(Utils.listToIntArray(blocks)));
                        return repository.getUpcomingMessages(owner, group, block);
                    }
                });
    }

    public void setCurrentBlocks(List<Integer> blocks) {
        liveCurrentBlocks.postValue(blocks);
        Log.e(TAG, "posted current blocks: " + Arrays.toString(Utils.listToIntArray(blocks)));
//        liveCurrentBlock.postValue(blocks.get(blocks.size() - 1));
    }

    public LiveData<List<Message>> getUpcomingMessages() {
        return liveUpcomingMessages;
//        return repository.getUpcomingMessages(owner, group, block);
    }

    public void loadSentMessages(final int owner, final int group) {
        liveSentMessages = repository.getSentMessages(owner, group);
//        liveSentMessages = Transformations.switchMap(
//                liveCurrentBlocks,
//                new Function<List<Integer>, LiveData<List<Message>>>() {
//                    @Override
//                    public LiveData<List<Message>> apply(List<Integer> blocks) {
//                        int[] blocksArray = Utils.listToIntArray(blocks);
//                        return repository.getSentMessages(owner, group, blocksArray);
//                    }
//                });
    }

    public LiveData<List<Message>> getSentMessages() {
        if (liveCurrentBlocks.getValue() != null) {
            Log.e(TAG, "getting sent messages from blocks " + Arrays.toString(Utils.listToIntArray(liveCurrentBlocks.getValue())));
        }
        return liveSentMessages;
    }

    public int getNumSentMessages() {
        return liveSentMessages.getValue().size();
    }

//    public LiveData<List<Message>> getSentMessages(int owner, int group, int block) {
//        //return sentMessages;
////        upcomingMessages = repository.getUpcomingMessages(owner, group);
//        return repository.getSentMessages(owner, group, block);
//    }

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
