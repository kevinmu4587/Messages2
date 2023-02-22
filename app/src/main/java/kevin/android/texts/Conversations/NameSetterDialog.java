package kevin.android.texts.Conversations;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.List;

import kevin.android.texts.R;

public class NameSetterDialog extends AppCompatDialogFragment {
    private List<Conversation> conversations;
    private int currentIndex = -1;
    private TextView titleTextView;
    private EditText firstName_editText, lastName_editText, nickname_editText;
    private Button postiiveButton;
    private Dialog dialog;

    private NameSetterDialogListener listener;
    private static final String TAG = "EditTextDialog";

    public NameSetterDialog(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().setTitle("Enter your player information");
        View view = inflater.inflate(R.layout.name_setter_dialog, null);

//        titleTextView = view.findViewById(R.id.name_setter_dialog_title);
        firstName_editText = view.findViewById(R.id.name_setter_dialog_firstName);
        lastName_editText = view.findViewById(R.id.name_setter_dialog_lastName);
        nickname_editText = view.findViewById(R.id.name_setter_dialog_nickname);
        postiiveButton = view.findViewById(R.id.name_setter_dialog_positive_button);

//        titleTextView.setText("Enter your player information");
        firstName_editText.setText("Oliver");
        lastName_editText.setText("Green");
        nickname_editText.setText("Oli");

//        dialog = new Dialog(getActivity(), R.style.NameSetterDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.name_setter_dialog);
//        dialog.setTitle("WHY THOUGH");
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        postiiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex < conversations.size() - 1) {
                    listener.applyNames(
                            firstName_editText.getText().toString(),
                            lastName_editText.getText().toString(),
                            nickname_editText.getText().toString(),
                            currentIndex == -1 ? -1 : conversations.get(currentIndex).getId());
                    currentIndex++;
//                    titleTextView.setText(conversations.get(currentIndex).getConversationDialogTitle());
                    getDialog().setTitle(conversations.get(currentIndex).getConversationDialogTitle());
                    firstName_editText.setText(conversations.get(currentIndex).getFirstName());
                    lastName_editText.setText(conversations.get(currentIndex).getLastName());
                    nickname_editText.setText(conversations.get(currentIndex).getNickname());
                } else {
//                    dialog.dismiss();
                    dismiss();
                }
            }
        });
//        dialog.show();
        return view;
    }

//    @NonNull
//    @Override
//    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//
//        return dialog;
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NameSetterDialogListener) getParentFragment();
        } catch (ClassCastException x) {
            Log.e(TAG, "Parent does not implement EditTextDialogListener interface");
        }
    }

    public interface NameSetterDialogListener {
        void applyNames(String firstName, String lastName, String nickname, int id);
    }
}
