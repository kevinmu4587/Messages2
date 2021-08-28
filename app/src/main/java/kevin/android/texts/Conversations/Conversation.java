package kevin.android.texts.Conversations;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversation_table")
public class Conversation implements Parcelable  {
    public static final int STATE_RUNNING = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_DONE = 2;

    private String firstName;
    private String lastName;
    private String lastMessage;
    private int group;
    private int conversationState;
//    private String chatState;
    private boolean active;
    private int recentValue;
    private boolean unread;

    @PrimaryKey(autoGenerate = true)
    private int id;

    public Conversation(String firstName, String lastName, int group, boolean active, int recentValue) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastMessage = "New message!";
        this.group = group;
        this.conversationState = Conversation.STATE_PAUSED;
//        this.chatState = "";
        this.active = active;
        this.recentValue = recentValue;
        this.unread = true;
    }

    protected Conversation(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        lastMessage = in.readString();
        group = in.readInt();
        conversationState = in.readInt();
        active = in.readByte() != 0;
        id = in.readInt();
        unread = in.readByte() != 0;
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getConversationState() {
        return conversationState;
    }

    public void setConversationState(int conversationState) {
        this.conversationState = conversationState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getRecentValue() {
        return recentValue;
    }

    public void setRecentValue(int recentValue) {
        this.recentValue = recentValue;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isUnread() {
        return unread;
    }

//    public String getChatState() {
//        return chatState;
//    }
//
//    public void setChatState(String chatState) {
//        this.chatState = chatState;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(lastMessage);
        parcel.writeInt(group);
        parcel.writeInt(conversationState);
        parcel.writeByte((byte) (active ? 1 : 0));
        parcel.writeInt(id);
        parcel.writeByte((byte) (unread ? 1 : 0));
    }
}
