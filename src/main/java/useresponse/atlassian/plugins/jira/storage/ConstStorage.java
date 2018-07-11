package useresponse.atlassian.plugins.jira.storage;

import java.util.HashMap;
import java.util.Map;

public class ConstStorage {

    public static final String API_STRING = "api/4.0/";

    public static final Map<String, String> UR_PRIORITIES = new HashMap<String, String>() {
        {
            put("low", "Low");
            put("normal", "Normal");
            put("high", "High");
            put("urgent", "Urgent");
        }
    };

}
