package useresponse.atlassian.plugins.jira.model;


import net.java.ao.Entity;
import net.java.ao.ManyToMany;

public interface PriorityLink extends Entity {

    String getJiraPriorityName();
    void setJiraPriorityName(String jiraPriorityName);

    URPriority getUseResponsePriorityName();
    void setUseResponsePriority(URPriority useResponsePriority);
}
