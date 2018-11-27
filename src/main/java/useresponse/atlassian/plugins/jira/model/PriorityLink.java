package useresponse.atlassian.plugins.jira.model;

import net.java.ao.Entity;

public interface PriorityLink extends Entity {

    String getJiraPriorityName();
    void setJiraPriorityName(String jiraPriorityName);

    URPriority getUseResponsePriority();
    void setUseResponsePriority(URPriority useResponsePriority);
}
