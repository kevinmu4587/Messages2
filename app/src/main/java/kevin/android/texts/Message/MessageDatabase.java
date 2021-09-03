package kevin.android.texts.Message;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import kevin.android.texts.Utils;

@Database(entities = Message.class, version = 1)
public abstract class MessageDatabase extends RoomDatabase {
    private static MessageDatabase instance;  // database instance (singleton)
    public abstract MessageDao messageDao();  // our DAO
    public static Context activity;
    private static final String TAG = "MessageDatabase";

    // used to get access to the instance of the database
    public static synchronized MessageDatabase getInstance(Context context) {
        // only make an instance if there isn't one already
        activity = context.getApplicationContext();
        if (instance == null) {
            // make a new instance using a builder
            instance = Room.databaseBuilder(activity,
                    MessageDatabase.class, "message_database")   // name is the file name for the database
                    .fallbackToDestructiveMigration()             // used for version numbers
                    .addCallback(roomCallback)                    // add a callback to prepopulate the database
                    .build();                                     // return the database
        }
        //instance.populateDB();
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
            //populateDB();
            new PopulateDBAsyncTask(instance).execute();   // execute the pre-populating task
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private MessageDao messageDao;

        private PopulateDBAsyncTask(MessageDatabase database) {
            // get the dao
            messageDao = database.messageDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // prepopulate
            String json = Utils.loadJSONFromAssets(activity, "messages.jsonc");
            int count = 0;
            try {
                JSONArray players = new JSONArray(json);
                // for each player
                for (int i = 0; i < players.length(); ++i) {
                    JSONArray player = players.getJSONArray(i);
                    // for each group
                    for (int a = 0; a < player.length(); ++a) {
                        JSONArray group = player.getJSONArray(a);
                        Log.e(TAG, "There are " + group.length() + " messages in group " + (a + 1) + " for player " + (i + 1));
                        // for each of the player's messages
                        for (int j = 0; j < group.length(); ++j) {
                            JSONObject msg = group.getJSONObject(j);
                            String type = msg.getString("type");
                            JSONArray contentJson = msg.getJSONArray("content");
                            String[] content = new String[contentJson.length()];
                            for (int k = 0; k < content.length; k++) {
//                            Log.e("MessageDatabase", contentJson.getString(k));
                                content[k] = contentJson.getString(k);
                            }
                            int block = msg.getInt("block");
                            String time = msg.getString("time");
                            messageDao.insert(new Message(i + 1, type, content, a + 1, block, time));
                            count++;
//                        Log.e("MessageDatabase", "Read " + count + " so far.");
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("MessageDatabase", "JSONException.");
            }
//            messageDao.insert(new Message(1, "npc", new String[] {"Message 1", "Message 2", "Message 3"},
//                      1, 1, "12:30pm" ));
//            messageDao.insert(new Message(1, "my", new String[] {"Response 1", "Response 2", "Response 3"},
//                    1, 1, "12:31pm" ));
//            messageDao.insert(new Message(1, "npc", new String[] {"Message 4", "Message 5", "Message 6"},
//                      1, 1, "12:32pm" ));
//            messageDao.insert(new Message(1, "my", new String[] {"Response 4", "Response 5", "Response 6"},
//                     1, 1, "12:33pm" ));
//            messageDao.insert(new Message(1, "npc", new String[] {"Message 7", "Message 8", "Message 9"},
//                      1, 1, "12:30pm" ));
//            messageDao.insert(new Message(1, "my", new String[] {"Response 7", "Response 8", "Response 9"},
//                      1, 1, "12:31pm" ));
//            messageDao.insert(new Message(1, "npc", new String[] {"Message 10", "Message 11", "Message 12"},
//                    1, 1, "12:32pm" ));
//            messageDao.insert(new Message(1, "my", new String[] {"Response 10", "Response 11", "Response 12"},
//                     1, 1, "12:33pm" ));
            Log.e("MessageDatabase", "Added " + count + " messages");
            return null;
        }
    }
}