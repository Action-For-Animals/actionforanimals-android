package org.a5calls.android.a5calls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FakeJSONData {
    // A snapshot of real issue data, used for testing - converted to new actions format.
    public static final String ISSUE_DATA = "[{\"id\":1000,\"createdAt\":\"2025-08-22T15:00:00Z\",\"name\":\"Protect Millions of Farmed Animals\",\"reason\":\"The Food Security and Farm Protection Act (S. 1326) and related bills would strip states of their authority to pass animal welfare laws like California's Proposition 12, which bans cruel confinement of hens, sows, and veal calves. Passing these bills would undo years of progress and put millions of farmed animals at risk.\",\"actions\":{\"call\":{\"enabled\":true,\"script\":\"Hi, my name is **[NAME]** and I'm a constituent from **[CITY, ZIP]**. I'm calling to ask you to oppose the Food Security and Farm Protection Act and any Farm Bill language that would invalidate state-level animal welfare laws like California's Proposition 12. Protecting these laws is crucial to preventing cruelty and safeguarding millions of farmed animals. Thank you for your time.\"},\"email\":{\"enabled\":false,\"subject\":\"\",\"template\":\"\"}},\"categories\":[{\"name\":\"Farmed\"}],\"contactType\":\"REPS\",\"contacts\":null,\"contactAreas\":[\"US House\",\"US Senate\"],\"outcomeModels\":[{\"label\":\"unavailable\",\"status\":\"unavailable\"},{\"label\":\"voicemail\",\"status\":\"voicemail\"},{\"label\":\"contact\",\"status\":\"contact\"},{\"label\":\"skip\",\"status\":\"skip\"}],\"stats\":{\"total_actions\":0},\"slug\":\"protect-state-animal-laws\",\"active\":true,\"hidden\":false,\"label\":\"\",\"meta\":\"\",\"status\":\"active\"},{\"id\":851,\"createdAt\":\"2025-02-06T20:14:04Z\",\"name\":\"Protect Medicaid Funding\",\"reason\":\"Medicaid is a health insurance program serving 72 million low-income and disabled Americans. Republicans are considering multiple approaches to reduce Medicaid spending, including capping federal contributions and adding work requirements.\",\"actions\":{\"call\":{\"enabled\":true,\"script\":\"Hello, my name is [NAME] and I'm a constituent from [City/Zip]. I'm calling to urge [REP/SEN NAME] to oppose attempts to reduce Medicaid funding and coverage. Medicaid provides essential health care access to millions of Americans, especially children. Thank you for your time.\"},\"email\":{\"enabled\":false,\"subject\":\"\",\"template\":\"\"}},\"categories\":[{\"name\":\"Healthcare\"}],\"contactType\":\"REPS\",\"contacts\":null,\"contactAreas\":[\"US House\",\"US Senate\"],\"outcomeModels\":[{\"label\":\"unavailable\",\"status\":\"unavailable\"},{\"label\":\"voicemail\",\"status\":\"voicemail\"},{\"label\":\"contact\",\"status\":\"contact\"},{\"label\":\"skip\",\"status\":\"skip\"}],\"stats\":{\"total_actions\":1136},\"slug\":\"protect-medicaid-funding-cuts\",\"active\":true,\"hidden\":false,\"label\":\"\",\"meta\":\"\",\"status\":\"active\"},{\"id\":836,\"createdAt\":\"2025-01-13T16:42:01Z\",\"name\":\"Oppose Robert F. Kennedy Jr. for HHS Secretary\",\"reason\":\"Kennedy is a notorious opponent of life-saving vaccinations and has promoted dangerous health products. He would do untold damage to our health care infrastructure.\",\"actions\":{\"call\":{\"enabled\":true,\"script\":\"Hi, my name is **[NAME]** and I'm a constituent from [CITY, ZIP]. I'm calling to urge [REP/SEN NAME] oppose the nomination of Robert F. Kennedy Jr. for Secretary of Health and Human Services. Kennedy has no scientific background and has promoted dangerous lies about vaccine safety. Thank you for your time.\"},\"email\":{\"enabled\":false,\"subject\":\"\",\"template\":\"\"}},\"categories\":[{\"name\":\"Nominations\"}],\"contactType\":\"REPS\",\"contacts\":null,\"contactAreas\":[\"US Senate\"],\"outcomeModels\":[{\"label\":\"unavailable\",\"status\":\"unavailable\"},{\"label\":\"voicemail\",\"status\":\"voicemail\"},{\"label\":\"contact\",\"status\":\"contact\"},{\"label\":\"skip\",\"status\":\"skip\"}],\"stats\":{\"total_actions\":53321},\"slug\":\"robert-kennedy-rfk-hhs\",\"active\":true,\"hidden\":false,\"label\":\"\",\"meta\":\"\",\"status\":\"active\"}]";

    // A snapshot of real reps data, used for testing.
    public static final String REPS_DATA = "{\"location\":\"BOWLING GREEN\",\"lowAccuracy\":true,\"isSplit\":true,\"state\":\"NY\",\"district\":\"10\",\"representatives\":[{\"id\":\"G000599\",\"name\":\"Dan Goldman\",\"phone\":\"202-225-7944\",\"url\":\"https://goldman.house.gov\",\"photoURL\":\"https://images.5calls.org/house/256/G000599.jpg\",\"party\":\"Democrat\",\"state\":\"NY\",\"district\":\"10\",\"reason\":\"This is your representative in the House.\",\"area\":\"US House\",\"field_offices\":[{\"phone\":\"212-822-7878\",\"city\":\"New York\"},{\"phone\":\"718-312-7575\",\"city\":\"Brooklyn\"}]},{\"id\":\"G000555\",\"name\":\"Kirsten Gillibrand\",\"phone\":\"202-224-4451\",\"url\":\"https://www.gillibrand.senate.gov\",\"photoURL\":\"https://images.5calls.org/senate/256/G000555.jpg\",\"party\":\"Democrat\",\"state\":\"NY\",\"reason\":\"This is one of your two Senators.\",\"area\":\"US Senate\",\"field_offices\":[{\"phone\":\"518-431-0120\",\"city\":\"Albany\"},{\"phone\":\"716-854-9725\",\"city\":\"Buffalo\"},{\"phone\":\"315-376-6118\",\"city\":\"Lowville\"},{\"phone\":\"212-688-6262\",\"city\":\"New York\"},{\"phone\":\"631-249-2825\",\"city\":\"Melville\"},{\"phone\":\"585-263-6250\",\"city\":\"Rochester\"},{\"phone\":\"315-448-0470\",\"city\":\"Syracuse\"},{\"phone\":\"845-875-4585\",\"city\":\"Yonkers\"}]},{\"id\":\"S000148\",\"name\":\"Chuck Schumer\",\"phone\":\"202-224-6542\",\"url\":\"https://www.schumer.senate.gov\",\"photoURL\":\"https://images.5calls.org/senate/256/S000148.jpg\",\"party\":\"Democrat\",\"state\":\"NY\",\"reason\":\"This is one of your two Senators.\",\"area\":\"US Senate\",\"field_offices\":[{\"phone\":\"585-263-5866\",\"city\":\"Rochester\"},{\"phone\":\"607-772-6792\",\"city\":\"Binghamton\"},{\"phone\":\"716-846-4111\",\"city\":\"Buffalo\"},{\"phone\":\"212-486-4430\",\"city\":\"New York\"},{\"phone\":\"914-734-1532\",\"city\":\"Peekskill\"},{\"phone\":\"315-423-5471\",\"city\":\"Syracuse\"},{\"phone\":\"518-431-4070\",\"city\":\"Albany\"},{\"phone\":\"631-753-0978\",\"city\":\"Melville\"}]},{\"id\":\"ny-james\",\"name\":\"Letitia A. James\",\"phone\":\"518-776-2000\",\"url\":\"\",\"photoURL\":\"https://images.5calls.org/ag/256/ny-james.jpg\",\"party\":\"\",\"state\":\"NY\",\"reason\":\"This is the Attorney General of your state\",\"area\":\"AttorneysGeneral\",\"field_offices\":[]},{\"id\":\"ny-hochul\",\"name\":\"Kathy Hochul\",\"phone\":\"518-474-8390\",\"url\":\"https://www.governor.ny.gov\",\"photoURL\":\"https://images.5calls.org/governor/256/ny-hochul.jpg\",\"party\":\"Democrat\",\"state\":\"NY\",\"reason\":\"This is the Governor of your state\",\"area\":\"Governor\",\"field_offices\":[]},{\"id\":\"ny-mosley\",\"name\":\"Walter T. Mosley\",\"phone\":\"212-417-5800\",\"url\":\"\",\"photoURL\":\"https://images.5calls.org/secstate/256/ny-mosley.jpg\",\"party\":\"\",\"state\":\"NY\",\"reason\":\"This is the Secretary of State of your state\",\"area\":\"SecState\",\"field_offices\":[]}]}";

    // A snapshot of real report data, used for testing.
    public static final String REPORT_DATA = "{\"count\":4627301,\"donateOn\":true}";

    public static JSONArray GetIssueJSON() {
        try {
            return new JSONArray(ISSUE_DATA);
        } catch (JSONException e) {
            // Shouldn't happen since all the JSON data is static strings above.
            return new JSONArray();
        }
    }

    public static JSONObject GetRepsJSON() {
        try {
            return new JSONObject(REPS_DATA);
        } catch (JSONException e) {
            // Shouldn't happen since all the JSON data is static strings above.
            return new JSONObject();
        }
    }

    public static JSONObject GetReportJSON() {
        try {
            return new JSONObject(REPORT_DATA);
        } catch (JSONException e) {
            // Shouldn't happen since all the JSON data is static strings above.
            return new JSONObject();
        }
    }
}