package useresponse.atlassian.plugins.jira.storage;

import java.util.HashMap;
import java.util.Map;

public class Storage {

    public static final String API_STRING = "api/4.0/";
    public static final String JIRA_DATA_HANDLER_ROUTE = "jira-tickets/entry/add.json";
    public static final String JIRA_SETTINGS_ROUTE = "jira-settings.json";

    public static String userWhoPerformedAction = "";

    public static final Map<String, String> UR_PRIORITIES = new HashMap<String, String>() {
        {
            put("low", "Low");
            put("normal", "Normal");
            put("high", "High");
            put("urgent", "Urgent");
        }
    };

    public static boolean needToExecuteAction = true;
    public static boolean isFromBinder = false;
}
