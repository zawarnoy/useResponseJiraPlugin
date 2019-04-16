package useresponse.atlassian.plugins.jira.model;

import net.java.ao.Entity;

public interface CommentLink extends Entity {
    int getJiraCommentId();

    void setJiraCommentId(int jiraCommentId);

    int getUseResponseCommentId();

    void setUseResponseCommentId(int useResponseCommentId);

    int getIssueId();

    void setIssueId(int issueId);
}
