package kevin.android.texts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG="SettingsFragment";

    EditTextPreference firstNameEntry, lastNameEntry, nicknameEntry;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Settings");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        firstNameEntry = findPreference("playerFirstName");
        lastNameEntry = findPreference("playerLastName");
        nicknameEntry = findPreference("playerNickname");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        firstNameEntry.setText(sharedPref.getString("playerFirstName", GameManager.playerFirstName));
        lastNameEntry.setText(sharedPref.getString("playerLastName", GameManager.playerLastName));
        nicknameEntry.setText(sharedPref.getString("playerNickname", GameManager.playerNickname));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        ListPreference chapterSelect = findPreference("chapterSelect");
        int numChapters = GameManager.timeline.size();
        String[] entries = new String[numChapters];
        String[] values = new String[numChapters];
        for (int i = 0; i < numChapters; ++i) {
            String line = GameManager.timeline.get(i);
            // this gives the chapter name (ex. Chapter 2: Acquaintance)
            entries[i] = line.substring(line.indexOf(' ') + 1);
            // this is an integer telling us how many chapters to skip relative to our current chapter
            values[i] = i + 1 + "";
        }
        Log.e(TAG, "I found " + numChapters + " possible chapters");
        chapterSelect.setEntries(entries);
        chapterSelect.setEntryValues(values);
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