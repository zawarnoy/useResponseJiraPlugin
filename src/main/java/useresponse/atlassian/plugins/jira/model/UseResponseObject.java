package useresponse.atlassian.plugins.jira.model;

import net.java.ao.Entity;

public interface UseResponseObject extends Entity {

    int getUseResponseId();

    void setUseResponseId(int useResponseId);

    int getJiraId();

    void setJiraId(int jiraId);

    boolean getNeedOfSync();

    void setNeedOfSync(boolean needOfSync);

    String getObjectType();

    void setObjectType(String type);
}
