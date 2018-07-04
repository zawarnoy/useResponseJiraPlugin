package useresponse.atlassian.plugins.jira.settings;

public interface PluginSettings {
    String getUseResponseDomain();
    void setUseResponseDomain(String domain);

    String getUseResponseApiKey();
    void setUseResponseApiKey(String apiKey);

    String getUseResponseOpenStatus();
    void setUseResponseOpenStatus(String openStatus);

    String getUseResponseInProgressStatus();
    void setUseResponseInProgressStatus(String inProgressStatus);

    String getUseResponseReopenedStatus();
    void setUseResponseReopenedStatus(String reopenedStatus);

    String getUseResponseResolvedStatus();
    void setUseResponseResolvedStatus(String resolvedStatus);

    String getUseResponseClosedStatus();
    void setUseResponseClosedStatus(String closedStatus);

    String getUseResponseToDoStatus();
    void setUseResponseToDoStatus(String toDoStatus);

    String getUseResponseDoneStatus();
    void setUseResponseDoneStatus(String doneStatus);
}
