package useresponse.atlassian.plugins.jira.model;
import net.java.ao.Entity;

public interface StatusesLink extends Entity {

    String getJiraStatusName();
    void setJiraStatusName(String jiraStatusName);

    String getUseResponseStatusSlug();
    void setUseResponseStatusSlug(String statusSlug);

}
