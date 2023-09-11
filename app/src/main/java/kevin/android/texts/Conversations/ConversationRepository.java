package kevin.android.texts.Conversations;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;


public class ConversationRepository {
    private static final String TAG = "ConversationRepository";
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

    public void resetGame() {
        Log.e(TAG, "Calling reset game in ConversationRepository");
        conversationDao.resetGame();
    }

//    public LiveData<List<Conversation>> getAllConversations() {
//        return conversationDao.getAllConversations();
//    }

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
