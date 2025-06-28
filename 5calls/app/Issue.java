package org.a5calls.android.a5calls;  // Match your actual package name

import java.util.List;

public class Issue {
    public String title;
    public String summary;
    public List<Contact> contacts;
    public String script;

    public Issue() {
        // Needed for Firebase deserialization
    }
}
