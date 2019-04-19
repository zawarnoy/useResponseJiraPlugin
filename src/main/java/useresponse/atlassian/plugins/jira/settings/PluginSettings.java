package useresponse.atlassian.plugins.jira.settings;

import java.util.ArrayList;

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

    Boolean getSyncTicketsData();
    void setSyncTicketsData(boolean syncTicketsData);

    Boolean getNeedExecute();
    void setNeedExecute(boolean needExecute);

    ArrayList getAvailableProjectsIds();
    void setAvailableProjectsIds(ArrayList<Long> projectsIds);

}
