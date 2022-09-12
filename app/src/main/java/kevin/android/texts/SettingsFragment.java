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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import kevin.android.texts.Message.ChatFragmentDirections;

public class SettingsFragment extends PreferenceFragmentCompat {
    EditTextPreference firstNameEntry, lastNameEntry, nicknameEntry;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Settings");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        firstNameEntry = (EditTextPreference)findPreference("playerFirstName");
        lastNameEntry = (EditTextPreference)findPreference("playerLastName");
        nicknameEntry = (EditTextPreference)findPreference("playerNickname");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        firstNameEntry.setText(sharedPref.getString("playerFirstName", GameManager.playerFirstName));
        lastNameEntry.setText(sharedPref.getString("playerLastName", GameManager.playerLastName));
        nicknameEntry.setText(sharedPref.getString("playerNickname", GameManager.playerNickname));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
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