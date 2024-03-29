package kevin.android.texts.Message;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.List;

import kevin.android.texts.GameManager;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> sentMessages;
    private LiveData<List<Message>> allMessages;
    //private List<Message> upcomingMessages;
    private static final String TAG = "MessageRepository";

    public MessageRepository(Application application) {
        MessageDatabase database = MessageDatabase.getInstance(application);
        messageDao = database.messageDao();
        //sentMessages = messageDao.getSentMessages(1, 1);
        allMessages = messageDao.getAllMessages();
    }

    // API for the ViewModel
    public void update(Message message) {
        // need to perform the update on a background thread
        new UpdateMessageAsyncTask(messageDao).execute(message);
    }

    public LiveData<List<Message>> getUpcomingMessages(int owner, int group, int... blocks) {
        // idk if this should really be live but idk how to make an AsyncTask return the List
        return messageDao.getUpcomingMessages(owner, group, blocks);
    }

    public LiveData<List<Message>> getSentMessages(int owner, int group) {
        // livedata is returned on a background thread, so no need for AsyncTask
//        GameManager.addPrecedingBlock(block);
//        int[] blocks = GameManager.getPrecedingBlocks();
        return messageDao.getSentMessages(owner, group);
        //return sentMessages;
    }

    public LiveData<List<Message>> getAllMessages() {
        return allMessages;
    }

    private static class UpdateMessageAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao messageDao; // passed to this class

        private UpdateMessageAsyncTask(MessageDao messageDao) {
            this.messageDao = messageDao;
        }

        @Override
        protected Void doInBackground(Message... messages) {
            messageDao.update(messages[0]);
            return null;
        }
    }
}
