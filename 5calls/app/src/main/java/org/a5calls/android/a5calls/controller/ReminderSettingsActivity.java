package org.a5calls.android.a5calls.controller;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.model.AccountManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Reminders settings screen
 */
public class ReminderSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Scheduled Reminders");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new ReminderSettingsFragment())
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

    public static class ReminderSettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        private final AccountManager accountManager = AccountManager.Instance;
        private ActivityResultLauncher<String> mNotificationPermissionRequest;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNotificationPermissionRequest = SettingsActivity.createNotificationPermissionRequest(this, (isGranted) -> {
                accountManager.setAllowReminders(getActivity(), isGranted);
                if (!isGranted) {
                    SwitchPreference remindersPref = findPreference(AccountManager.KEY_ALLOW_REMINDERS);
                    if (remindersPref != null) {
                        remindersPref.setChecked(false);
                        remindersPref.setSummary(R.string.reminders_disabled_summary);
                    }
                }
            });
        }

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.reminder_settings);

            boolean hasReminders = accountManager.getAllowReminders(getActivity());
            SwitchPreference remindersPref = findPreference(AccountManager.KEY_ALLOW_REMINDERS);
            if (remindersPref != null) {
                // Ensure the preference is enabled
                remindersPref.setEnabled(true);
                remindersPref.setChecked(hasReminders);

                remindersPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    accountManager.setAllowReminders(getActivity(), enabled);

                    // Handle notification permission if needed
                    if (enabled &&
                            !NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                        if (mNotificationPermissionRequest != null
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            mNotificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS);
                        }
                    }
                    return true;
                });
            }

            // Set up days selection
            Set<String> reminderDays = accountManager.getReminderDays(getActivity());
            MultiSelectListPreference daysPreference = findPreference(AccountManager.KEY_REMINDER_DAYS);
            if (daysPreference != null) {
                daysPreference.setValues(reminderDays);
                updateReminderDaysSummary(daysPreference, reminderDays);

                daysPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    Set<String> selectedDays = (Set<String>) newValue;
                    accountManager.setReminderDays(getActivity(), selectedDays);
                    updateReminderDaysSummary(daysPreference, selectedDays);
                    return true;
                });
            }

            // Set up time picker
            Preference timePreference = findPreference("reminder_time");
            if (timePreference != null) {
                timePreference.setOnPreferenceClickListener(preference -> {
                    final TimePickerFragment dialog = new TimePickerFragment();
                    dialog.setCallback((hourOfDay, minute) -> {
                        final int reminderMinutes = hourOfDay * 60 + minute;
                        accountManager.setReminderMinutes(requireContext(), reminderMinutes);
                        updateReminderTimeSummary(timePreference);
                    });
                    dialog.show(getParentFragmentManager(), "timePicker");
                    return true;
                });
                updateReminderTimeSummary(timePreference);
            }
        }

        private void updateReminderDaysSummary(MultiSelectListPreference daysPreference,
                                               Set<String> savedValues) {
            if (savedValues == null || savedValues.size() == 0) {
                daysPreference.setSummary("No days selected");
                return;
            }
            List<String> daysEntries = Arrays.asList(getActivity().getResources()
                    .getStringArray(R.array.reminder_days_titles));
            List<String> daysEntriesValues = Arrays.asList(getActivity().getResources()
                    .getStringArray(R.array.reminder_days_values));
            String summary = "";
            for (int i = 0; i < daysEntriesValues.size(); i++) {
                if (savedValues.contains(daysEntriesValues.get(i))) {
                    if (!TextUtils.isEmpty(summary)) {
                        summary += ", ";
                    }
                    summary += daysEntries.get(i);
                }
            }
            daysPreference.setSummary(summary);
        }

        private void updateReminderTimeSummary(Preference timePreference) {
            Calendar c = Calendar.getInstance();
            int storedMinutes = accountManager.getReminderMinutes(requireContext());
            int hour = storedMinutes / 60;
            int minutes = storedMinutes % 60;

            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minutes);

            final SimpleDateFormat dateFormat;
            if (DateFormat.is24HourFormat(requireContext())) {
                dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            } else {
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            }
            timePreference.setSummary(dateFormat.format(c.getTime()));
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            SettingsActivity.turnOnReminders(getActivity(), accountManager);
            super.onStop();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (TextUtils.equals(key, AccountManager.KEY_REMINDER_DAYS)) {
                Set<String> result = sharedPreferences.getStringSet(key,
                        AccountManager.DEFAULT_REMINDER_DAYS);
                MultiSelectListPreference daysPreference = findPreference(AccountManager.KEY_REMINDER_DAYS);
                if (daysPreference != null) {
                    updateReminderDaysSummary(daysPreference, result);
                }
            }
        }

    }
}