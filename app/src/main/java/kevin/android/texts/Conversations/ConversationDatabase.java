package kevin.android.texts.Conversations;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = Conversation.class, version = 1)
public abstract class ConversationDatabase extends RoomDatabase {
    private static ConversationDatabase instance;  // database instance (singleton)
    public abstract ConversationDao conversationDao();  // our DAO

    // used to get access to the instance of the database
    public static synchronized ConversationDatabase getInstance(Context context) {
        // only make an instance if there isn't one already
        if (instance == null) {
            // make a new instance using a builder
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ConversationDatabase.class, "conversation_database")   // name is the file name for the database
                    .fallbackToDestructiveMigration()             // used for version numbers
                    .addCallback(roomCallback)                    // add a callback to prepopulate the database
                    .build();                                     // return the database
        }
        return instance;
    }

    /*
    - this is a callback which is called after the database is created
    - used to prepopulate the database
 */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        // called after the database is created
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new ConversationDatabase.PopulateDBAsyncTask(instance).execute();   // execute the pre-populating task
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private ConversationDao conversationDao;

        private PopulateDBAsyncTask(ConversationDatabase database) {
            // get the dao
            conversationDao = database.conversationDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // prepopulate
            conversationDao.insert(new Conversation("Spike", ":)",
                    1, true, 1));
            conversationDao.insert(new Conversation("Jello", "Bello",
                    1, true,2));
            conversationDao.insert(new Conversation("Ricardo", "Diaz",
                    1,  false,3));
            return null;
        }
    }

}
