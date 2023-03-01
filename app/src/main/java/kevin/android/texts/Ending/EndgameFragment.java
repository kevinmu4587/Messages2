package kevin.android.texts.Ending;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import kevin.android.texts.Conversations.ConversationAdapter;
import kevin.android.texts.GameManager;
import kevin.android.texts.R;
import kevin.android.texts.Utils;

public class EndgameFragment extends Fragment {
    private static final String TAG = "EndgameFragment";

    public EndgameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        Log.e(TAG, "These are the endings I found: " + Arrays.toString(GameManager.endingsFound));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Endings Guide");
        View view = inflater.inflate(R.layout.fragment_endgame, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.endings_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        EndingAdapter adapter = new EndingAdapter();
        recyclerView.setAdapter(adapter);

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