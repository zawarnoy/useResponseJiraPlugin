package useresponse.atlassian.plugins.jira.model;
import net.java.ao.Entity;

public interface URPriority extends Entity {

    String getUseResponsePrioritySlug();
    void setUseResponsePrioritySlug(String useResponsePrioritySlug);

    String getUseResponsePriorityValue();
    void setUseResponsePriorityValue(String useResponsePriorityValue);

}
