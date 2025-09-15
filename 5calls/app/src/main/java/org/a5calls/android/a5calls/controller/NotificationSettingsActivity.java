package org.a5calls.android.a5calls.controller;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

import org.a5calls.android.a5calls.FiveCallsApplication;
import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.model.AccountManager;

/**
 * Notifications settings screen
 */
public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Notifications");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new NotificationSettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class NotificationSettingsFragment extends PreferenceFragmentCompat {
        private final AccountManager accountManager = AccountManager.Instance;

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.notification_settings);

            String notificationSetting = accountManager.getNotificationPreference(getActivity());
            SwitchPreference notificationPref = findPreference("notifications_switch");
            if (notificationPref != null) {
                // Ensure the preference is enabled
                notificationPref.setEnabled(true);

                // Convert string setting to boolean (0 = enabled, 1 = disabled)
                boolean isEnabled = "0".equals(notificationSetting);
                notificationPref.setChecked(isEnabled);

                notificationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    String result = enabled ? "0" : "1";
                    SettingsActivity.updateNotificationsPreference(
                            (FiveCallsApplication) getActivity().getApplication(),
                            accountManager,
                            result
                    );
                    return true;
                });
            }
        }
    }
}