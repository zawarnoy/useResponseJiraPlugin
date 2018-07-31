package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.listener.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import java.io.IOException;

public class CreateIssueAction extends AbstractIssueAction {

    public CreateIssueAction(Issue issue,
                             UseResponseObjectManager useResponseObjectManager,
                             RendererManager rendererManager,
                             PriorityLinkManager priorityLinkManager,
                             PluginSettingsFactory pluginSettingsFactory,
                             AttachmentManager attachmentManager,
                             IssueFileLinkManager issueFileLinkManager, IssueRequestBuilder issueRequestBuilder) {
        this.issue = issue;
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.attachmentManager = attachmentManager;
        this.issueFileLinkManager = issueFileLinkManager;
        this.builder = issueRequestBuilder;

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
        request.addParameter(builder.build(issue));
        return request;
    }

}
