package kevin.android.texts.Message;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message);

    @Update
    void update(Message message);

    // load all upcoming messages
    @Query("SELECT * FROM messages_table WHERE owner = :owner AND msg_group = :msg_group AND sent = 0 AND block IN (:block)")
    LiveData<List<Message>> getUpcomingMessages(int owner, int msg_group, int... block);

    // load ones that were previously sent, but don't load blocks, etc
    @Query("SELECT * FROM messages_table WHERE owner = :owner AND msg_group = :msg_group AND sent = 1 AND type NOT IN ('block', 'background', 'note', 'ending', 'terminator') ORDER BY insertNum")
    LiveData<List<Message>>  getSentMessages(int owner, int msg_group);

    @Query("SELECT * FROM messages_table")
    LiveData<List<Message>> getAllMessages();
}
