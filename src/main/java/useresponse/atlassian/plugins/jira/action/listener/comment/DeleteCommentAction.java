package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.listener.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class DeleteCommentAction extends AbstractCommentAction {

    public DeleteCommentAction(Comment comment, CommentLinkManager commentLinkManager, PluginSettingsFactory pluginSettingsFactory) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new DeleteRequest();
        this.actionType = ActionType.DELETE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        return null;
    }

    @Override
    protected String createUrl() {
//         TODO How to get deleted comment id?
        return null;
    }

    @Override
    protected void handleResponse(String response) {
    }

    @Override
    public String call() {
        return null;// TODO delete empty run method
    }
}
