package useresponse.atlassian.plugins.jira.model;

import net.java.ao.Entity;

public interface URObject extends Entity {


    int getUseResponseId();
    void setUseResponseId(int useResponseId);

    int getJiraId();
    void setJiraId(int jiraId);
}
