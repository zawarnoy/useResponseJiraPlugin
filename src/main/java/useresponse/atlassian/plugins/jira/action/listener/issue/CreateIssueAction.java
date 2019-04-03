package useresponse.atlassian.plugins.jira.action.listener.issue;

import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.handler.servlet.binder.IssueBinderServletRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;

@Component("createIssueAction")
public class CreateIssueAction extends AbstractIssueAction {

    @Inject
    @Named("issueBinderRequestHandler")
    private IssueBinderServletRequestHandler issueBinderServletRequestHandler;

    public CreateIssueAction() {
        this.request = new PostRequest();
        this.actionType = ListenerActionType.CREATE_ISSUE_ID;
    }

    @Override
    public String createUrl() {
        return getSpecialApiPath();
    }

    @Override
    public void handleResponse(String response) {
        issueBinderServletRequestHandler.handle(response);
    }

    @Override
    public Request addParameters(Request request) throws IOException {
        request.addParameter(new HashMap<Object, Object>() {{
            put("issue", builder.build(issue));
        }});
        return request;
    }

}
