package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.event.issue.IssueEvent;
import useresponse.atlassian.plugins.jira.request.Request;

public interface Action extends Runnable {
    void execute(IssueEvent event);

}
