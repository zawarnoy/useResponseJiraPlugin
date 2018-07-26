package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;

import java.io.IOException;

public class CreateIssueAction extends AbstractIssueAction {

    public CreateIssueAction(Issue issue,
                             UseResponseObjectManager useResponseObjectManager,
                             RendererManager rendererManager,
                             PriorityLinkManager priorityLinkManager,
                             PluginSettingsFactory pluginSettingsFactory,
                             AttachmentManager attachmentManager,
                             IssueFileLinkManager issueFileLinkManager) {
        this.issue = issue;
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.attachmentManager = attachmentManager;
        this.issueFileLinkManager = issueFileLinkManager;

        this.request = new PostRequest();
        this.actionType = ActionType.CREATE_ISSUE_ID;
    }

    @Override
    public String createUrl() {
        return collectUrl("objects.json");
    }

    @Override
    public void handleResponse(String response) throws ParseException {
        useResponseObjectManager.add(getIdFromResponse(response), issue.getId().intValue());
    }

    @Override
    public Request addParameters(Request request) throws IOException {
        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request = prepareRequest(request, issue.getId().intValue());
        return addStandardParametersToRequest(request, issue);
    }

}
