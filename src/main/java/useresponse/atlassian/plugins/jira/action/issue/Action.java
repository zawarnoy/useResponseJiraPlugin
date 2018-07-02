package useresponse.atlassian.plugins.jira.action.issue;

import com.atlassian.jira.issue.Issue;

public interface Action {
    void execute(Issue issue) throws Exception;
}
