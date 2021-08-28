package kevin.android.texts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import kevin.android.texts.Conversations.Conversation;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<CharSequence> lastMessage = new MutableLiveData<>();
    private MutableLiveData<Conversation> currentRunning = new MutableLiveData<>();

    public void setLastMessage(CharSequence text) {
        lastMessage.postValue(text);
    }

    public LiveData<CharSequence> getLastMessage() {
        return lastMessage;
    }

    public void setCurrentRunning(Conversation conversation) {
        currentRunning.postValue(conversation);
    }

    public LiveData<Conversation> getCurrentRunning() {
        return currentRunning;
    }

    public Conversation getConversationObject() {
        return currentRunning.getValue();
    }
}
