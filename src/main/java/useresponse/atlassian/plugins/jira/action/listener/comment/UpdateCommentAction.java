package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;

public class UpdateCommentAction extends AbstractCommentAction {

    public UpdateCommentAction(Comment comment, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory, CommentRequestBuilder commentRequestBuilder) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.parametersBuilder = commentRequestBuilder;
        this.request = new PostRequest();
        this.actionType = ActionType.UPDATE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        request.addParameter(parametersBuilder.build((Comment) comment));
        return request;
    }

    @Override
    protected String createUrl() {
        int useResponseId = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        return collectUrl("comments/" + useResponseId + "/edit.json");
    }

    @Override
    protected void handleResponse(String response) {

    }
}
