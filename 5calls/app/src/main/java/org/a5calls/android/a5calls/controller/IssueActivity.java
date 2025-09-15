package org.a5calls.android.a5calls.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.a5calls.android.a5calls.AppSingleton;
import org.a5calls.android.a5calls.FiveCallsApplication;
import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.databinding.ActivityIssueBinding;
import org.a5calls.android.a5calls.model.AccountManager;
import org.a5calls.android.a5calls.model.Contact;
import org.a5calls.android.a5calls.model.DatabaseHelper;
import org.a5calls.android.a5calls.model.Issue;
import org.a5calls.android.a5calls.util.MarkdownUtil;
import org.a5calls.android.a5calls.view.ContactListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * iOS-style issue detail screen that matches AnimalPolicyDetail.swift
 * Simplified version for political campaigns only (calling representatives)
 */
public class IssueActivity extends AppCompatActivity {
    private static final String TAG = "IssueActivity";
    public static final String KEY_ISSUE = "key_issue";
    public static final String KEY_IS_LOW_ACCURACY = "key_is_low_accuracy";
    public static final String KEY_DONATE_IS_ON = "key_donate_is_on";
    public static final int RESULT_OK = 1;
    public static final int RESULT_SERVER_ERROR = 2;

    private Issue mIssue;
    private boolean mIsLowAccuracy = false;
    private boolean mDonateIsOn = false;
    private final AccountManager accountManager = AccountManager.Instance;

    private ActivityIssueBinding binding;
    private List<Contact> mContacts = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssueBinding.inflate(getLayoutInflater());

        mIssue = getIntent().getParcelableExtra(KEY_ISSUE);
        if (mIssue == null) {
            finish();
            return;
        }
        mIsLowAccuracy = getIntent().getBooleanExtra(KEY_IS_LOW_ACCURACY, false);
        mDonateIsOn = getIntent().getBooleanExtra(KEY_DONATE_IS_ON, false);

        setContentView(binding.getRoot());

        setupActionBar();
        setupContent();
        setupLocationSection();
        setupContactsSection();
        setupActionButtons();

        // Track analytics
        FiveCallsApplication.analyticsManager().trackPageview("/issue/" + mIssue.slug + "/", this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh all sections when returning to this activity
        setupLocationSection();
        setupContactsSection();
        setupActionButtons();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Remove title to match iOS
        }
    }

    private void setupContent() {
        // Set issue title and description
        binding.issueName.setText(mIssue.name);
        MarkdownUtil.setUpScript(binding.issueDescription, mIssue.reason, getApplicationContext());
    }

    private void setupLocationSection() {
        // Show location setup section if no location is set
        boolean hasLocation = accountManager.hasLocation(this);

        if (!hasLocation) {
            binding.locationSetupSection.setVisibility(View.VISIBLE);
            binding.setLocationButton.setOnClickListener(v -> showLocationBottomSheet());
        } else {
            binding.locationSetupSection.setVisibility(View.GONE);
        }
    }

    private void setupContactsSection() {
        if (!accountManager.hasLocation(this)) {
            // Hide contacts section when location is not set
            binding.contactsSection.setVisibility(View.GONE);
            return;
        }

        binding.contactsSection.setVisibility(View.VISIBLE);

        // Set section header - matches iOS contactsSectionHeader logic
        String headerText = getContactsSectionHeader();
        binding.contactsHeader.setText(headerText);

        // Populate contacts with proper sectioning
        populateContacts();
    }

    private String getContactsSectionHeader() {
        // This matches the iOS contactsSectionHeader computed property
        // For now, we only support political campaigns (representatives)
        // In the future, this could support corporate campaigns too
        return getString(R.string.reps_list_header);
    }

    private void setupActionButtons() {
        boolean hasLocation = accountManager.hasLocation(this);
        String address = accountManager.getAddress(this);
        String lat = accountManager.getLat(this);
        String lng = accountManager.getLng(this);

        android.util.Log.d("IssueActivity", "setupActionButtons: hasLocation = " + hasLocation);
        android.util.Log.d("IssueActivity", "address = '" + address + "'");
        android.util.Log.d("IssueActivity", "lat = '" + lat + "'");
        android.util.Log.d("IssueActivity", "lng = '" + lng + "'");

        if (!hasLocation) {
            // Hide action buttons when location is not set
            binding.actionButtonsSection.setVisibility(View.GONE);
            android.util.Log.d("IssueActivity", "Hiding action buttons - no location");
            return;
        }

        binding.actionButtonsSection.setVisibility(View.VISIBLE);
        android.util.Log.d("IssueActivity", "Showing action buttons - location available");

        // Debug the views
        android.util.Log.d("IssueActivity", "actionButtonsSection exists: " + (binding.actionButtonsSection != null));
        android.util.Log.d("IssueActivity", "primaryActionButton exists: " + (binding.primaryActionButton != null));

        if (binding.actionButtonsSection != null) {
            android.util.Log.d("IssueActivity", "actionButtonsSection visibility: " + binding.actionButtonsSection.getVisibility());
        }

        if (binding.primaryActionButton != null) {
            android.util.Log.d("IssueActivity", "primaryActionButton visibility before: " + binding.primaryActionButton.getVisibility());
        }

        // Setup primary action button for calling representatives
        binding.primaryActionButton.setText(R.string.make_calls_button);
        binding.primaryActionButton.setVisibility(View.VISIBLE);
        android.util.Log.d("IssueActivity", "Button text set and visibility set to VISIBLE");

        if (binding.primaryActionButton != null) {
            android.util.Log.d("IssueActivity", "primaryActionButton visibility after: " + binding.primaryActionButton.getVisibility());
            android.util.Log.d("IssueActivity", "primaryActionButton text: " + binding.primaryActionButton.getText());
        }

        binding.primaryActionButton.setOnClickListener(v -> {
            // Launch contact detail screen for calling
            android.util.Log.d("IssueActivity", "Make Calls button clicked!");
            launchContactDetail();
        });

        // Hide secondary buttons for now (no corporate campaigns yet)
        binding.secondaryButtonsContainer.setVisibility(View.GONE);
        android.util.Log.d("IssueActivity", "setupActionButtons completed");
    }

    private void populateContacts() {
        LinearLayout contactsContainer = binding.contactsContainer;
        contactsContainer.removeAllViews();

        // Debug logging to see what data we have
        android.util.Log.d("IssueActivity", "*** Issue contactAreas: " +
            (mIssue.contactAreas != null ? mIssue.contactAreas.toString() : "null"));
        if (mIssue.contacts != null) {
            for (Contact contact : mIssue.contacts) {
                android.util.Log.d("IssueActivity", "*** Contact: " + contact.name +
                    " - Area: " + contact.area);
            }
        }

        if (mIssue.contacts == null || mIssue.contacts.isEmpty()) {
            // Show message when no contacts available
            TextView noContacts = new TextView(this);
            noContacts.setText("No representatives found for your location.");
            noContacts.setTextColor(getResources().getColor(R.color.text_secondary_modern, null));
            noContacts.setPadding(16, 16, 16, 16);
            contactsContainer.addView(noContacts);
            return;
        }

        // Get categorized contacts using new Issue methods
        List<Contact> targetedContacts = mIssue.getTargetedContacts();
        List<Contact> irrelevantContacts = mIssue.getIrrelevantContacts();
        List<String> vacantAreas = mIssue.getVacantAreas();


        // Add targeted contacts section
        if (!targetedContacts.isEmpty()) {
            addContactsSection(contactsContainer, targetedContacts, null, false);
        }

        // Add irrelevant contacts section with divider
        if (!irrelevantContacts.isEmpty()) {
            addDivider(contactsContainer);
            addContactsSection(contactsContainer, irrelevantContacts, "Not targeted by this campaign", true);
        }

        // Add vacant areas section with divider
        if (!vacantAreas.isEmpty()) {
            addDivider(contactsContainer);
            addVacantAreasSection(contactsContainer, vacantAreas);
        }
    }

    private void addContactsSection(LinearLayout container, List<Contact> contacts, String noteText, boolean isIrrelevant) {
        for (int i = 0; i < contacts.size(); i++) {
            final Contact contact = contacts.get(i);
            // Find the original index in the full contacts list for navigation
            final int originalIndex = mIssue.contacts.indexOf(contact);

            ContactListItemView contactItem = new ContactListItemView(this);
            boolean hasBeenCalled = hasContactBeenCalled(contact);

            // Set contact with note text for irrelevant contacts
            if (noteText != null) {
                contactItem.setContact(contact, hasBeenCalled, noteText);
            } else {
                contactItem.setContact(contact, hasBeenCalled);
            }

            // Set opacity for irrelevant contacts (matches iOS .opacity(0.4))
            if (isIrrelevant) {
                contactItem.setIrrelevant(true);
            }

            // Set click listener to navigate to call screen
            contactItem.setOnClickListener(v -> {
                launchContactDetailForContact(contact, originalIndex);
            });

            container.addView(contactItem);
        }
    }

    private void addVacantAreasSection(LinearLayout container, List<String> vacantAreas) {
        for (String area : vacantAreas) {
            // Create a placeholder contact for vacant areas
            Contact vacantContact = new Contact();
            vacantContact.name = "Vacant Seat";
            vacantContact.area = area;

            ContactListItemView contactItem = new ContactListItemView(this);
            contactItem.setContact(vacantContact, false, "This position is currently vacant");
            contactItem.setIrrelevant(true); // Show dimmed like iOS

            // Make vacant contacts non-clickable
            contactItem.setOnClickListener(null);
            contactItem.setClickable(false);

            container.addView(contactItem);
        }
    }

    private void addDivider(LinearLayout container) {
        // Create a divider view to separate sections
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
        ));
        divider.setBackgroundColor(getResources().getColor(R.color.text_secondary_modern, null));
        divider.getLayoutParams().height = (int) (0.5 * getResources().getDisplayMetrics().density);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) divider.getLayoutParams();
        params.setMargins(16, 0, 16, 0);

        container.addView(divider);
    }

    private boolean hasContactBeenCalled(Contact contact) {
        DatabaseHelper dbHelper = AppSingleton.getInstance(this).getDatabaseHelper();
        return dbHelper.hasCalledToday(mIssue.id, contact.id);
    }

    private void launchContactDetailForContact(Contact contact, int contactIndex) {
        Intent intent = new Intent(this, RepCallActivity.class);
        intent.putExtra(KEY_ISSUE, mIssue);
        intent.putExtra(RepCallActivity.KEY_ACTIVE_CONTACT_INDEX, contactIndex);
        intent.putExtra(RepCallActivity.KEY_ADDRESS, getIntent().getStringExtra(RepCallActivity.KEY_ADDRESS));
        intent.putExtra(RepCallActivity.KEY_LOCATION_NAME, getIntent().getStringExtra(RepCallActivity.KEY_LOCATION_NAME));
        intent.putExtra(KEY_IS_LOW_ACCURACY, mIsLowAccuracy);
        intent.putExtra(KEY_DONATE_IS_ON, mDonateIsOn);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showLocationBottomSheet() {
        LocationBottomSheetFragment locationBottomSheet = LocationBottomSheetFragment.newInstance();
        locationBottomSheet.setLocationSetListener(location -> {
            // Refresh the screen when location is set
            recreate();
        });
        locationBottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
    }

    private void launchContactDetail() {
        Intent intent = new Intent(this, RepCallActivity.class);
        intent.putExtra(KEY_ISSUE, mIssue);
        // Pass other required extras from the original activity
        intent.putExtra("KEY_ADDRESS", getIntent().getStringExtra("KEY_ADDRESS"));
        intent.putExtra("KEY_LOCATION_NAME", getIntent().getStringExtra("KEY_LOCATION_NAME"));
        intent.putExtra(KEY_IS_LOW_ACCURACY, mIsLowAccuracy);
        intent.putExtra(KEY_DONATE_IS_ON, mDonateIsOn);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_issue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.menu_share) {
            sendShare();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void sendShare() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(
                R.string.issue_share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                String.format(getResources().getString(R.string.issue_share_content), mIssue.name,
                        mIssue.slug));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(
                R.string.share_chooser_title)));
    }
}