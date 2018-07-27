package useresponse.atlassian.plugins.jira.service.handler.request.api.issue;

import com.atlassian.jira.issue.Issue;
import useresponse.atlassian.plugins.jira.service.handler.request.api.RequestHandler;
import useresponse.atlassian.plugins.jira.request.Request;

public class CreateIssueRequestHandler extends IssueRequestHandler {

    public CreateIssueRequestHandler(RequestHandler requestHandler, Issue issue) {
        this.requestHandler = requestHandler;
        this.issue = issue;
    }

    @Override
    protected Request currentAddition(Request request) {

        return request;
    }
}
