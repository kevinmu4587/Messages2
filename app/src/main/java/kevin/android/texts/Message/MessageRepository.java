package kevin.android.texts.Message;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MessageRepository {
    private MessageDao messageDao;
    private LiveData<List<Message>> sentMessages;
    private LiveData<List<Message>> allMessages;
    //private List<Message> upcomingMessages;

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

    public LiveData<List<Message>> getUpcomingMessages(int owner, int group, int block) {
        // idk if this should really be live but idk how to make an AsyncTask return the List
        return messageDao.getUpcomingMessages(owner, group, block);
    }

    public LiveData<List<Message>> getSentMessages(int owner, int group, int block) {
        // livedata is returned on a background thread, so no need for AsyncTask
        return messageDao.getSentMessages(owner, group, block);
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
    /*private static class GetUpcomingMessagesAsyncTask extends AsyncTask<Integer, Void, List<Message>> {
        private MessageDao messageDao; // passed to this class

        private GetUpcomingMessagesAsyncTask() {
            this.messageDao = messageDao;
        }

        @Override
        protected List<Message> doInBackground(Integer... integers) {
            return messageDao.getUpcomingMessages(integers[0]);
        }
    }*/
}
