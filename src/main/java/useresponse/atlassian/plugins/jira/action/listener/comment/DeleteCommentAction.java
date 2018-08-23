package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.action.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class DeleteCommentAction extends AbstractCommentAction {

    Logger log = LoggerFactory.getLogger(DeleteCommentAction.class);

    public DeleteCommentAction(WithId comment, CommentLinkManager commentLinkManager, PluginSettingsFactory pluginSettingsFactory) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new GetRequest();
        this.actionType = ActionType.DELETE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        return request;
    }

    @Override
    protected String createUrl() {
        return collectUrl("comments/" + comment.getId() + "/trash.json");
    }

    @Override
    protected void handleResponse(String response) {
    }
}
