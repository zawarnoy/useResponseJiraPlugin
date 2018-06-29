package useresponse.atlassian.plugins.jira.action.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import useresponse.atlassian.plugins.jira.request.PostRequest;

public class CreateIssueAction implements Action {
    @Override
    public void execute(IssueEvent event) throws Exception {
        PostRequest request = new PostRequest();

        request.addParameter("object_type", "ticket");
        request.addParameter("ownership", "helpdesk");
        request.addParameter("title", "title");
        request.addParameter("content", "hello");

        request.sendRequest();
    }
}