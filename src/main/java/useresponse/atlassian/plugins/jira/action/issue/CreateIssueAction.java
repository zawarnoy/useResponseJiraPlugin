package useresponse.atlassian.plugins.jira.action.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.PostRequest;

public class CreateIssueAction extends AbstractAction {
    @Override
    public void execute(Issue issue) throws Exception {
        UseResponseObject object = this.useResponseObjectManager.add(123, issue.getId().intValue() );
    }
}
