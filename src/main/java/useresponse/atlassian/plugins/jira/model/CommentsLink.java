package useresponse.atlassian.plugins.jira.model;
import net.java.ao.Entity;


public interface CommentsLink extends Entity{
    int getJiraCommentId();
    void setJiraCommentId(int jiraCommentId);

    int getUseResponseCommentId();
    void setUseResponseCommentId(int useResponseCommentId);
}
