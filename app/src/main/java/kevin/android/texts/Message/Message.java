package kevin.android.texts.Message;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import kevin.android.texts.Converters;

@Entity(tableName = "messages_table")
@TypeConverters(Converters.class)
public class Message {
    // fields of a message
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int owner;
    private String type; // npc, my, action trigger
    private String[] content; // message content
    private int choice; // 1, 2, 3
    private int block;
    private boolean sent; // if it was sent in the chat
    @ColumnInfo(name = "msg_group")
    private int group;
    private String time;

    public Message(int owner, String type, String[] content, int group, int block, String time) {
        this.owner = owner;
        this.type = type;
        this.content = content;
        this.choice = 0;
        this.sent = false;
        this.group = group;
        this.block = block;
        this.time = time;
    }

    public int getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String[] getContent() {
        return content;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public int getGroup() {
        return group;
    }

    public String getTime() {
        return time;
    }
}
