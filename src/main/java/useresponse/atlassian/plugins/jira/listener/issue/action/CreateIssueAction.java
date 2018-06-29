package useresponse.atlassian.plugins.jira.listener.issue.action;

import useresponse.atlassian.plugins.jira.request.AbstractRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;

public class CreateIssueAction implements Action {
    @Override
    public void execute() throws Exception {
        PostRequest request = new PostRequest();

        request.addParameter("object_type", "ticket");
        request.addParameter("ownership", "helpdesk");
        request.addParameter("title", "title");
        request.addParameter("content", "hello");

        request.sendRequest();
    }
}
