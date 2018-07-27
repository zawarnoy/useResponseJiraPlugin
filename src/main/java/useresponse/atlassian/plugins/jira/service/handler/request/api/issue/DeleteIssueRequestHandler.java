package useresponse.atlassian.plugins.jira.service.handler.request.api.issue;

import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.request.api.RequestHandler;

public class DeleteIssueRequestHandler extends IssueRequestHandler{

    public DeleteIssueRequestHandler(Issue issue, RequestHandler requestHandler) {
        this.issue = issue;
        this.requestHandler = requestHandler;
    }

    @Override
    protected Request currentAddition(Request request) {
        return null;
    }
}