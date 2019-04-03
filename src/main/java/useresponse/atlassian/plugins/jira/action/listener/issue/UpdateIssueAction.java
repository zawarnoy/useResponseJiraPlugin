package useresponse.atlassian.plugins.jira.action.listener.issue;

import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component("updateIssueAction")
public class UpdateIssueAction extends AbstractIssueAction {

    public UpdateIssueAction() {
        this.request = new PutRequest();
        this.actionType = ListenerActionType.UPDATE_ISSUE_ID;
    }

    @Override
    public String createUrl() {
        return getSpecialApiPath();
    }

    @Override
    public void handleResponse(String response) {

    }

    @Override
    public Request addParameters(Request request) throws IOException {
        HashMap<String, Map> params = new HashMap<>();
        params.put("issue", builder.build(issue));
        request.addParameter(params);
        return request;
    }
}
