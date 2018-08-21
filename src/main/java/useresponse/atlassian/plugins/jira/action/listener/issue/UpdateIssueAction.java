package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.ActionType;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateIssueAction extends AbstractIssueAction {

    private final StatusesLinkManager statusesLinkManager;

    public UpdateIssueAction(Issue issue,
                             UseResponseObjectManager useResponseObjectManager,
                             RendererManager rendererManager,
                             PriorityLinkManager priorityLinkManager,
                             PluginSettingsFactory pluginSettingsFactory,
                             AttachmentManager attachmentManager,
                             IssueFileLinkManager issueFileLinkManager,
                             StatusesLinkManager statusesLinkManager, IssueRequestBuilder issueRequestBuilder) {
        this.issue = issue;
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.attachmentManager = attachmentManager;
        this.issueFileLinkManager = issueFileLinkManager;
        this.statusesLinkManager = statusesLinkManager;
        this.builder = issueRequestBuilder;

        this.request = new PutRequest();
        this.actionType = ActionType.UPDATE_ISSUE_ID;
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
