package useresponse.atlassian.plugins.jira.settings;

public interface PluginSettings {
    String getUseResponseDomain();
    void setUseResponseDomain(String domain);

    String getUseResponseApiKey();
    void setUseResponseApiKey(String apiKey);

    String getAutosendingFlag();
    void setAutosendingFlag(String autosendingFlag);

    boolean getSyncStatuses();
    void setSyncStatuses(boolean syncStatuses);

    boolean getSyncComments();
    void setSyncComments(boolean syncComments);

    boolean getBasicFields();
    void setBasicFields(boolean basicFields);
}
