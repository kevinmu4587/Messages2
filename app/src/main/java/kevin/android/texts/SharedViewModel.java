package kevin.android.texts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import kevin.android.texts.Conversations.Conversation;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Conversation> currentRunning = new MutableLiveData<>();

    public void setCurrentRunning(Conversation conversation) {
        currentRunning.postValue(conversation);
    }

    public LiveData<Conversation> getCurrentRunning() {
        return currentRunning;
    }
}
