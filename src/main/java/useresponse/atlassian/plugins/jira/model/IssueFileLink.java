package useresponse.atlassian.plugins.jira.model;
import net.java.ao.Entity;

public interface IssueFileLink extends Entity{

    int getJiraIssueId();
    void setJiraIssueId(int jiraIssueId);

    String getSentFilename();
    void setSentFilename(String sentFilename);
}
