package kevin.android.texts.Conversations;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import kevin.android.texts.R;

public class EditTextDialog  extends AppCompatDialogFragment {
    private String title;
    private String firstName, lastName, nickname;
    private int id;
    private EditText firstName_editText, lastName_editText, nickname_editText;

    private EditTextDialogListener listener;
    private static final String TAG = "EditTextDialog";

    public EditTextDialog(String title, String firstName, String lastName, String nickname, int id) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.id = id;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_text_dialog, null);

        builder.setView(view)
                .setTitle(title)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.applyNames(
                                firstName_editText.getText().toString(),
                                lastName_editText.getText().toString(),
                                nickname_editText.getText().toString(),
                                id);
                    }
                });

        firstName_editText = view.findViewById(R.id.edit_text_dialog_firstName);
        lastName_editText = view.findViewById(R.id.edit_text_dialog_lastName);
        nickname_editText = view.findViewById(R.id.edit_text_dialog_nickname);

        firstName_editText.setText(firstName);
        lastName_editText.setText(lastName);
        nickname_editText.setText(nickname);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditTextDialogListener) getParentFragment();
        } catch (ClassCastException x) {
            Log.e(TAG, "Parent does not implement EditTextDialogListener interface");
        }
    }

    public interface EditTextDialogListener {
        void applyNames(String firstName, String lastName, String nickname, int id);
    }
}
