package kevin.android.texts.Conversations;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConversationDao {
    @Insert
    void insert(Conversation conversation);

    @Update
    void update(Conversation conversation);

    @Query("SELECT * FROM conversation_table WHERE active = 1")
    LiveData<List<Conversation>> getActiveConversations();

    @Query("SELECT * FROM conversation_table WHERE active = 0")
    LiveData<List<Conversation>> getInactiveConversations();

    @Query("SELECT * FROM conversation_table WHERE id = :id")
    LiveData<Conversation> getConversationById(int id);

    @Query("SELECT * FROM conversation_table")
    LiveData<List<Conversation>> getAllConversations();

    @Query("UPDATE conversation_table SET active = 0, conversationState = 0, lastMessage = 'New message!', " + "lastTime = '', lastPlayerChoice = 0")
    void resetConversations();

    @Query("UPDATE messages_table SET sent = 0, insertNum = -1, choice = 0 WHERE sent = 0")
    void resetMessages();

    @Transaction
    abstract void resetGame() {
        resetConversations();
        resetMessages();
    }
}
