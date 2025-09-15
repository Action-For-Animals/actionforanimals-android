package org.a5calls.android.a5calls.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.onesignal.Continue;
import com.onesignal.OneSignal;

import org.a5calls.android.a5calls.FiveCallsApplication;
import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.model.AccountManager;
import org.a5calls.android.a5calls.model.NotificationUtils;
import org.a5calls.android.a5calls.util.AnalyticsManager;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Settings for the app
 */
// TODO: Analytics and Notification settings need a way to retry if connection was not available.
public class SettingsActivity extends AppCompatActivity {
    public static final String EXTRA_FROM_NOTIFICATION = "fromNotification";
    static String TAG = "SettingsActivity";

    private final AccountManager accountManager = AccountManager.Instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }

        if (getIntent().getBooleanExtra(EXTRA_FROM_NOTIFICATION, false)) {
            FiveCallsApplication.analyticsManager().trackPageview("/settings", this);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SettingsFragment())
                .commit();

        // Apply slide-in animation from left to right
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent upIntent = getParentActivityIntent();
            if (shouldUpRecreateTask(upIntent) || isTaskRoot()) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                // This is probably because we opened settings from the notification.
                if (upIntent != null) {
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                }
            } else {
                navigateUpTo(upIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        // Apply slide-out animation from right to left when closing
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void turnOnReminders(Context context, AccountManager manager) {
        // Set up the notification firing logic when the settings activity ends, so as not
        // to do the work too frequently.
        if (manager.getAllowReminders(context)) {
            NotificationUtils.setReminderTime(context, manager.getReminderMinutes(context));
        } else {
            NotificationUtils.cancelFutureReminders(context);
        }
    }

    public static ActivityResultLauncher<String> createNotificationPermissionRequest(
            Fragment fragment, Consumer<Boolean> isGranted) {
        // Only needed on SDK 33 (Tiramisu) and newer
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return null;
        }
        if (ContextCompat.checkSelfPermission(fragment.getContext(),
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted::accept
        );
    }

    public static void updateNotificationsPreference(FiveCallsApplication application,
                                                     AccountManager accountManager,
                                                     String result) {
        accountManager.setNotificationPreference(application, result);
        if (TextUtils.equals("0", result)) {
            OneSignal.getNotifications().requestPermission(true, Continue.none());
            OneSignal.getUser().getPushSubscription().optIn();
            // TODO(#139): Wait for permission request result before opting in
        } else if (TextUtils.equals("1", result)) {
            OneSignal.getUser().getPushSubscription().optOut();
        }
        // If the user changes the settings there's no need to show the dialog in the future.
        accountManager.setNotificationDialogShown(application, true);
        // Log this to Analytics
        if (accountManager.allowAnalytics(application)) {
//            Tracker tracker = application.getDefaultTracker();
//            tracker.send(new HitBuilders.EventBuilder()
//                    .setCategory("Notifications")
//                    .setAction("NotificationSettingsChange")
//                    .setLabel(application.getApplicationContext().getResources()
//                            .getStringArray(R.array.notification_options)[Integer.valueOf(result)])
//                    .setValue(1)
//                    .build());
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private final AccountManager accountManager = AccountManager.Instance;

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            addPreferencesFromResource(R.xml.settings);

            // Set up click listeners for all preference items
            setupPreferenceClickListeners();
        }

        private void setupPreferenceClickListeners() {
            // Notifications settings
            Preference notificationsPref = findPreference("notifications_settings");
            if (notificationsPref != null) {
                notificationsPref.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(getActivity(), NotificationSettingsActivity.class);
                    startActivity(intent);
                    return true;
                });
            }

            // Reminders settings
            Preference remindersPref = findPreference("reminders_settings");
            if (remindersPref != null) {
                remindersPref.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(getActivity(), ReminderSettingsActivity.class);
                    startActivity(intent);
                    return true;
                });
            }

            // Feedback
            Preference feedbackPref = findPreference("feedback_action");
            if (feedbackPref != null) {
                feedbackPref.setOnPreferenceClickListener(preference -> {
                    openFeedback();
                    return true;
                });
            }

            // Instagram
            Preference instagramPref = findPreference("instagram_action");
            if (instagramPref != null) {
                instagramPref.setOnPreferenceClickListener(preference -> {
                    openInstagram();
                    return true;
                });
            }

            // Share app
            Preference sharePref = findPreference("share_app_action");
            if (sharePref != null) {
                sharePref.setOnPreferenceClickListener(preference -> {
                    shareApp();
                    return true;
                });
            }

            // Rate app
            Preference ratePref = findPreference("rate_app_action");
            if (ratePref != null) {
                ratePref.setOnPreferenceClickListener(preference -> {
                    rateApp();
                    return true;
                });
            }

            // 5calls credit
            Preference fivecallsPref = findPreference("fivecalls_credit");
            if (fivecallsPref != null) {
                fivecallsPref.setOnPreferenceClickListener(preference -> {
                    open5CallsLink();
                    return true;
                });
            }

            // Open source
            Preference openSourcePref = findPreference("open_source_action");
            if (openSourcePref != null) {
                openSourcePref.setOnPreferenceClickListener(preference -> {
                    showOpenSourceLicenses();
                    return true;
                });
            }
        }

        private void openFeedback() {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(android.net.Uri.parse("mailto:howdyxfa@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Action for Animals Feedback");
            try {
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            } catch (android.content.ActivityNotFoundException ex) {
                // No email app available
            }
        }

        private void openInstagram() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://www.instagram.com/xfaorg/"));
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                // No browser available
            }
        }

        private void shareApp() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Action for Animals app: https://play.google.com/store/apps/details?id=" + requireContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Action for Animals"));
        }

        private void rateApp() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("market://details?id=" + requireContext().getPackageName()));
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                // Fallback to web version
                intent.setData(android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName()));
                startActivity(intent);
            }
        }

        private void open5CallsLink() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://github.com/5calls"));
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                // No browser available
            }
        }

        // Inspired by https://www.bignerdranch.com/blog/open-source-licenses-and-android/
        private void showOpenSourceLicenses() {
            WebView view = (WebView) LayoutInflater.from(getContext()).inflate(R.layout.licence_view, null);
            view.loadUrl("file:///android_asset/licenses.html");
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.license_btn))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

    }
}
