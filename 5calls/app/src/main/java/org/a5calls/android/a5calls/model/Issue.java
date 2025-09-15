package org.a5calls.android.a5calls.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an issue.
 */
public class Issue implements Parcelable {
    public String id;
    public String name;
    public String slug;
    public String reason;
    public Actions actions;
    public String status;
    public String link;
    public String linkTitle;
    public String createdAt;

    public List<Contact> contacts;
    public List<String> contactAreas;
    public String contactType;
    public List<Outcome> outcomeModels;
    public Category[] categories;
    public boolean isSplit;
    public IssueStats stats;

    protected Issue(Parcel in) {
        id = in.readString();
        name = in.readString();
        slug = in.readString();
        reason = in.readString();
        actions = in.readParcelable(Actions.class.getClassLoader());
        link = in.readString();
        linkTitle = in.readString();
        status = in.readString();
        isSplit = in.readInt() != 0;
        createdAt = in.readString();
        contacts = in.createTypedArrayList(Contact.CREATOR);
        contactAreas = in.createStringArrayList();
        contactType = in.readString();
        outcomeModels = in.createTypedArrayList(Outcome.CREATOR);
        categories = in.createTypedArray(Category.CREATOR);
        stats = IssueStats.CREATOR.createFromParcel(in);
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(slug);
        dest.writeString(reason);
        dest.writeParcelable(actions, flags);
        dest.writeString(link);
        dest.writeString(linkTitle);
        dest.writeString(status);
        dest.writeInt(isSplit ? 1 : 0);
        dest.writeString(createdAt);
        dest.writeTypedList(contacts);
        dest.writeStringList(contactAreas);
        dest.writeString(contactType);
        dest.writeTypedList(outcomeModels);
        dest.writeTypedArray(categories, PARCELABLE_WRITE_RETURN_VALUE);
        stats.writeToParcel(dest, flags);
    }

    /**
     * Get contacts that should be actively contacted for this issue
     * This replicates iOS contactsForIssue() method
     */
    public List<Contact> getTargetedContacts() {
        if (contacts == null || contacts.isEmpty()) {
            return new ArrayList<>();
        }

        // For now, return all contacts since we don't have contactAreas logic yet
        // In the future, this should filter based on contactAreas like iOS
        return new ArrayList<>(contacts);
    }

    /**
     * Get contacts that exist but aren't relevant for this issue
     * For congressional issues: if targeting only House, show Senate as irrelevant (and vice versa)
     * This replicates iOS irrelevantContacts() method
     */
    public List<Contact> getIrrelevantContacts() {
        if (contacts == null || contactAreas == null) {
            return new ArrayList<>();
        }

        List<Contact> irrelevantContacts = new ArrayList<>();
        boolean hasHouse = contactAreas.contains("US House");
        boolean hasSenate = contactAreas.contains("US Senate");

        if (hasHouse && !hasSenate) {
            // Issue targets House only, Senate reps are irrelevant
            for (Contact contact : contacts) {
                if ("US Senate".equals(contact.area)) {
                    irrelevantContacts.add(contact);
                }
            }
        }

        if (hasSenate && !hasHouse) {
            // Issue targets Senate only, House reps are irrelevant
            for (Contact contact : contacts) {
                if ("US House".equals(contact.area)) {
                    irrelevantContacts.add(contact);
                }
            }
        }

        return irrelevantContacts;
    }

    /**
     * Get vacant areas (areas without representatives)
     * This would need to be populated from server data
     */
    public List<String> getVacantAreas() {
        // For now, return empty list
        // In the future, this should be populated from server data about missing reps
        return new ArrayList<>();
    }

    /**
     * Check if this issue has House representatives
     */
    public boolean hasHouse() {
        return contactAreas != null && contactAreas.contains("US House");
    }

    /**
     * Check if this issue has Senate representatives
     */
    public boolean hasSenate() {
        return contactAreas != null && contactAreas.contains("US Senate");
    }

    /**
     * Check if this is a congressional issue (House or Senate)
     */
    public boolean isCongressionalIssue() {
        return hasHouse() || hasSenate();
    }

    /**
     * Check if this issue is active
     */
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }

    /**
     * Check if this issue has been completed successfully
     */
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}
