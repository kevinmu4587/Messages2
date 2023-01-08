package kevin.android.texts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EndgameFragment extends Fragment {
    TextView endingAGuide;
    TextView endingBGuide;
    TextView endingCGuide;
    TextView endingDGuide;

    public EndgameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Endings Guide");
        View view = inflater.inflate(R.layout.fragment_endgame, container, false);
        endingAGuide = view.findViewById(R.id.endingAguide);
        endingBGuide = view.findViewById(R.id.endingBguide);
        endingCGuide = view.findViewById(R.id.endingCguide);
        endingDGuide = view.findViewById(R.id.endingDguide);

        String old = (String) endingAGuide.getText();
        endingAGuide.setText(Utils.replaceName(old));
        old = (String) endingBGuide.getText();
        endingBGuide.setText(Utils.replaceName(old));
        old = (String) endingCGuide.getText();
        endingCGuide.setText(Utils.replaceName(old));
        old = (String) endingDGuide.getText();
        endingDGuide.setText(Utils.replaceName(old));
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}