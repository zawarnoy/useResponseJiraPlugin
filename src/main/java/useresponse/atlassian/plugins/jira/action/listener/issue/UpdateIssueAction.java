package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class UpdateIssueAction extends AbstractIssueAction {

    private final StatusesLinkManager statusesLinkManager;

    public UpdateIssueAction(Issue issue,
                             UseResponseObjectManager useResponseObjectManager,
                             RendererManager rendererManager,
                             PriorityLinkManager priorityLinkManager,
                             PluginSettingsFactory pluginSettingsFactory,
                             AttachmentManager attachmentManager,
                             IssueFileLinkManager issueFileLinkManager,
                             StatusesLinkManager statusesLinkManager) {
        this.issue = issue;
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.attachmentManager = attachmentManager;
        this.issueFileLinkManager = issueFileLinkManager;
        this.statusesLinkManager = statusesLinkManager;

        this.request = new PutRequest();
        this.actionType = ActionType.UPDATE_ISSUE_ID;
    }

    @Override
    public String createUrl() {
        int useResponseId = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        return collectUrl("objects/" + useResponseId + ".json");
    }

    @Override
    public void handleResponse(String response) {

    }

    @Override
    public Request addParameters(Request request) throws Exception {
        addStandardParametersToRequest(request, issue);
        request = prepareRequest(request, issue.getId().intValue());
        request.addParameter("status", findUseResponseStatusFromJiraStatus(issue.getStatus().getName()));
        return request;
    }

    private String findUseResponseStatusFromJiraStatus(String jiraStatus) {
        return statusesLinkManager.findByJiraStatusName(jiraStatus).getUseResponseStatusSlug();
    }
}
