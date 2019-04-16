package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.component.ComponentAccessor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("updateIssueLinkAction")
public class UpdateIssueLinkAction extends AbstractIssueAction {

    public UpdateIssueLinkAction() {
        this.request = new PostRequest();
        this.actionType = ListenerActionType.UPDATE_ISSUE_LINK_ID;
    }

    @Override
    protected Request addParameters(Request request) throws IOException {
        Set<String> issueKeys = ComponentAccessor.getIssueManager().getAllIssueKeys(this.issue.getId());
        Map<Object, Object> parameters = new HashMap<>();
        parameters.put("issue", builder.build(issue, issueKeys));
        request.addParameter(parameters);
        return request;
    }

    @Override
    protected String createUrl() {
        return this.getSpecialApiPath();
    }

    @Override
    protected void handleResponse(String response) throws ParseException {

    }
}
