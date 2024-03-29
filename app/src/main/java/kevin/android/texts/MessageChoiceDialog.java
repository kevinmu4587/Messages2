package kevin.android.texts;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MessageChoiceDialog extends AppCompatDialogFragment {
    private String[] choices;
    private int choice;
    private MessageChoiceDialogListener listener;

    public MessageChoiceDialog(String[] choices) {
        this.choices = choices;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.options_dialog, null);

        builder.setView(view)
                .setTitle("Choose an option")
                .setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.applyText(choice);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MessageChoiceDialogListener) getParentFragment();
        } catch (ClassCastException x) {
            Log.e("Dialog", "Parent does not implement DialogListener interface");
        }
    }

    public interface MessageChoiceDialogListener {
        void applyText(int choice);
    }
}
