package org.a5calls.android.a5calls.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.model.Contact;

/**
 * iOS-style contact list item that matches ContactListItem from AnimalPolicyDetail.swift
 * Shows contact photo, name, title, and completion status
 */
public class ContactListItemView extends LinearLayout {

    private ImageView contactPhoto;
    private TextView contactInitials;
    private ImageView completionCheckmark;
    private TextView contactName;
    private TextView contactTitle;
    private TextView contactNote;

    public ContactListItemView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ContactListItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContactListItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.contact_list_item_ios, this, true);

        contactPhoto = findViewById(R.id.contact_photo);
        contactInitials = findViewById(R.id.contact_initials);
        completionCheckmark = findViewById(R.id.completion_checkmark);
        contactName = findViewById(R.id.contact_name);
        contactTitle = findViewById(R.id.contact_title);
        contactNote = findViewById(R.id.contact_note);

        // Make sure this view can receive clicks
        setClickable(true);
        setFocusable(true);
    }

    /**
     * Populate the view with contact data
     * @param contact The contact to display
     * @param showComplete Whether to show the completion checkmark
     * @param noteText Optional note text (for irrelevant/vacant contacts)
     */
    public void setContact(Contact contact, boolean showComplete, @Nullable String noteText) {
        // Set contact name
        contactName.setText(contact.name);

        // Set contact title/position
        String title = getContactTitle(contact);
        if (!TextUtils.isEmpty(title)) {
            contactTitle.setText(title);
            contactTitle.setVisibility(VISIBLE);
        } else {
            contactTitle.setVisibility(GONE);
        }

        // Set optional note text
        if (!TextUtils.isEmpty(noteText)) {
            contactNote.setText(noteText);
            contactNote.setVisibility(VISIBLE);
        } else {
            contactNote.setVisibility(GONE);
        }

        // Load contact photo or show initials
        loadContactPhoto(contact);

        // Show/hide completion checkmark
        completionCheckmark.setVisibility(showComplete ? VISIBLE : GONE);

        // Set up accessibility
        String completedText = showComplete ? getContext().getString(R.string.contact_completed) : "";
        setContentDescription(getContext().getString(R.string.contact_accessibility, contact.name, completedText));
    }

    /**
     * Set contact without completion status or notes
     */
    public void setContact(Contact contact) {
        setContact(contact, false, null);
    }

    /**
     * Set contact with completion status
     */
    public void setContact(Contact contact, boolean showComplete) {
        setContact(contact, showComplete, null);
    }

    /**
     * Get appropriate title text for contact - matches iOS officeDescription()
     */
    private String getContactTitle(Contact contact) {
        // This should match iOS Contact.officeDescription() method
        // For now, show area like "US House" or "US Senate"
        if (!TextUtils.isEmpty(contact.area)) {
            return contact.area;
        }
        return "";
    }

    /**
     * Load contact photo or show initials as fallback
     */
    private void loadContactPhoto(Contact contact) {
        if (!TextUtils.isEmpty(contact.photoURL)) {
            // Load photo from URL
            contactInitials.setVisibility(GONE);

            Glide.with(getContext())
                .load(contact.photoURL)
                .transform(new CircleCrop())
                .placeholder(R.color.text_secondary_modern)
                .error(R.color.text_secondary_modern)
                .into(contactPhoto);
        } else {
            // Show initials fallback
            String initials = getContactInitials(contact.name);
            if (!TextUtils.isEmpty(initials)) {
                contactInitials.setText(initials);
                contactInitials.setVisibility(VISIBLE);

                // Hide the photo ImageView's content
                contactPhoto.setImageDrawable(null);
            }
        }
    }

    /**
     * Extract initials from contact name
     */
    private String getContactInitials(String name) {
        if (TextUtils.isEmpty(name)) {
            return "?";
        }

        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
        } else {
            String initials = "";
            for (int i = 0; i < Math.min(2, words.length); i++) {
                if (!words[i].isEmpty()) {
                    initials += words[i].charAt(0);
                }
            }
            return initials.toUpperCase();
        }
    }

    /**
     * Set opacity for irrelevant contacts (matches iOS .opacity(0.4))
     */
    public void setIrrelevant(boolean irrelevant) {
        float alpha = irrelevant ? 0.4f : 1.0f;
        setAlpha(alpha);
    }
}