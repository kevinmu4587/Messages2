package kevin.android.texts.Conversations;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import kevin.android.texts.Message.Message;
import kevin.android.texts.Message.MessageDao;
import kevin.android.texts.Message.MessageDatabase;
import kevin.android.texts.Message.MessageRepository;

public class ConversationRepository {
    private ConversationDao conversationDao;
    private LiveData<List<Conversation>> activeConversations;

    public ConversationRepository(Application application) {
        ConversationDatabase database = ConversationDatabase.getInstance(application);
        conversationDao = database.conversationDao();
        activeConversations = conversationDao.getActiveConversations();
    }

    // API for the ViewModel
    public void update(Conversation conversation) {
        // need to perform the update on a background thread
//        new MessageRepository.UpdateMessageAsyncTask(messageDao).execute(message);
        new ConversationRepository.UpdateConversationAsyncTask(conversationDao).execute(conversation);
    }

    public LiveData<List<Conversation>> getActiveConversations() {
        // idk if this should really be live but idk how to make an AsyncTask return the List
        return conversationDao.getActiveConversations();
    }

    public LiveData<Conversation> getConversationById(int id) {
        // livedata is returned on a background thread, so no need for AsyncTask
        return conversationDao.getConversationById(id);
    }

    public LiveData<List<Conversation>> getInactiveConversations() {
        return conversationDao.getInactiveConversations();
    }

    private static class UpdateConversationAsyncTask extends AsyncTask<Conversation, Void, Void> {
        private ConversationDao conversationDao; // passed to this class

        private UpdateConversationAsyncTask(ConversationDao conversationDao) {
            this.conversationDao = conversationDao;
        }

        @Override
        protected Void doInBackground(Conversation... conversations) {
            conversationDao.update(conversations[0]);
            return null;
        }
    }
}
