package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class CreateCommentAction extends AbstractCommentAction {

    public CreateCommentAction(IssueEvent issueEvent, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory) {
        this.issueEvent = issueEvent;
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new PostRequest();
    }

    @Override
    public String createUrl() {
        return collectUrl("comments.json");
    }

    @Override
    public void handleResponse(String response) throws Exception {
        commentLinkManager.findOrAdd(getIdFromResponse(response), issueEvent.getComment().getId().intValue());
    }

    @Override
    public Request addParameters(Request request) {
        request = prepareRequest(request, issueEvent.getComment().getId().intValue());
        int id = useResponseObjectManager.findByJiraId(issueEvent.getComment().getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("content", issueEvent.getComment().getBody());
        request.addParameter("object_id", String.valueOf(id));
        return request;
    }
}
