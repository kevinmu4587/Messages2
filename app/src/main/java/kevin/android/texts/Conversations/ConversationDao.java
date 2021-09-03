package kevin.android.texts.Conversations;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
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
}
