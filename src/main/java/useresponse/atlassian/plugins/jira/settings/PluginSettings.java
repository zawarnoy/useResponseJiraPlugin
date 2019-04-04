package useresponse.atlassian.plugins.jira.settings;

public interface PluginSettings {
    String getUseResponseDomain();
    void setUseResponseDomain(String domain);

    String getUseResponseApiKey();
    void setUseResponseApiKey(String apiKey);

    String getAutosendingFlag();
    void setAutosendingFlag(String autosendingFlag);

    Boolean getSyncStatuses();
    void setSyncStatuses(boolean syncStatuses);

    Boolean getSyncComments();
    void setSyncComments(boolean syncComments);

    Boolean getSyncBasicFields();
    void setSyncBasicFields(boolean basicFields);
}
