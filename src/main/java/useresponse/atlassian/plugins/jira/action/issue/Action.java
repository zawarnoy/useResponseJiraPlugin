package useresponse.atlassian.plugins.jira.action.issue;

import com.atlassian.jira.event.issue.IssueEvent;

public interface Action {
    void execute(IssueEvent event) throws Exception;
}
