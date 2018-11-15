package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.action.ActionType;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UpdateIssueLinkAction extends AbstractIssueAction {

    Logger log = LoggerFactory.getLogger(UpdateIssueLinkAction.class);

    public UpdateIssueLinkAction(Issue issue, PluginSettingsFactory pluginSettingsFactory, IssueRequestBuilder issueRequestBuilder) {
        this.issue = issue;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.builder = issueRequestBuilder;

        this.request = new PostRequest();
        this.actionType = ActionType.UPDATE_ISSUE_LINK_ID;
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
        // silence
    }
}
