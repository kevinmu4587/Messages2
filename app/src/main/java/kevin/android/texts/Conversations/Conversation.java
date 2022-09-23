package kevin.android.texts.Conversations;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import kevin.android.texts.Converters;
import kevin.android.texts.R;

@Entity(tableName = "conversation_table")
@TypeConverters(Converters.class)
public class Conversation implements Parcelable  {
    public static final int STATE_RUNNING = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_DONE = 2;
    public static final int[] profilePictures = { R.drawable.rose,R.drawable.ferris_wheel, R.drawable.cat, R.drawable.police, R.drawable.moon };

    private String firstName;
    private String lastName;
    private String nickname;
    private String description;
    private String lastMessage = "New message!";
    private String lastTime;

    private boolean editable;
    private String conversationDialogTitle;

    private int group = 1;
    private int conversationState;
    private int lastPlayerChoice;
    // private String bgState;

    private boolean active = false; // whether the conversation is displayed on the ConversationFragment
//    private int recentValue;
    private boolean unread = true;
    private boolean initialized = false;
    private List<Integer> currentBlocks = new ArrayList<>();

    @PrimaryKey(autoGenerate = true)
    private int id;

    public Conversation(String firstName, String lastName, String nickname, String description,
                        boolean editable, String conversationDialogTitle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.description = description;
        this.editable = editable;
        this.conversationDialogTitle = conversationDialogTitle;
        this.conversationState = Conversation.STATE_RUNNING;
        // this.bgState = "";
        this.currentBlocks.add(0);
    }

    protected Conversation(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        description = in.readString();
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

    public void setDescription(String description) { this.description = description; }

    public String getDescription() { return description; }

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

    public int getLastPlayerChoice() {
        return lastPlayerChoice;
    }

    public void setLastPlayerChoice(int lastPlayerChoice) {
        this.lastPlayerChoice = lastPlayerChoice;
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

//    public int getRecentValue() {
//        return recentValue;
//    }
//
//    public void setRecentValue(int recentValue) {
//        this.recentValue = recentValue;
//    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isUnread() {
        return unread;
    }

    public List<Integer> getCurrentBlocks() {
        return currentBlocks;
    }

    public void setCurrentBlocks(List<Integer> currentBlocks) {
        this.currentBlocks = currentBlocks;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getCurrentBlock() {
        return currentBlocks.get(currentBlocks.size() - 1);
    }

    public int popTopBlock() {
        int top = currentBlocks.get(currentBlocks.size() - 1);
        currentBlocks.remove(currentBlocks.size() - 1);
        return top;
    }

    public void pushBlock(int block) {
        currentBlocks.add(block);
    }

    public int getProfilePictureID() {
        return profilePictures[this.id - 1];
    }

//    public void setProfilePictureID(int profilePictureID) {
//        this.profilePictureID = profilePictureID;
//    }

//    public String getBgState() {
//        return bgState;
//    }

//    public void setBgState(String bgState) {
//        this.bgState = bgState;
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

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getConversationDialogTitle() {
        return conversationDialogTitle;
    }

    public boolean isEditable() {
        return editable;
    }
}
