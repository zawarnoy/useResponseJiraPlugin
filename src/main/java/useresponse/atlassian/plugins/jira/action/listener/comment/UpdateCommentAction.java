package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class UpdateCommentAction extends AbstractCommentAction {

    public UpdateCommentAction(IssueEvent issueEvent, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory) {
        this.issueEvent = issueEvent;
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new PostRequest();
    }

    @Override
    protected Request addParameters(Request request) {
        request = prepareRequest(request, issueEvent.getComment().getId().intValue());
        request.addParameter("content", issueEvent.getComment().getBody());
        return request;
    }

    @Override
    protected String createUrl() {
        int useResponseId = commentLinkManager.findByJiraId(issueEvent.getComment().getId().intValue()).getUseResponseCommentId();
        return collectUrl("comments/" + useResponseId + "/edit.json");
    }

    @Override
    protected void handleResponse(String response) throws Exception {

    }
}
