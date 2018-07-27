package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.action.listener.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class CreateCommentAction extends AbstractCommentAction {

    public  CreateCommentAction(Comment comment, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new PostRequest();
        this.actionType = ActionType.CREATE_COMMENT_ID;
    }

    @Override
    public String createUrl() {
        return collectUrl("comments.json");
    }

    @Override
    public void handleResponse(String response) throws ParseException {
        commentLinkManager.findOrAdd(getIdFromResponse(response), comment.getId().intValue());
    }

    @Override
    public Request addParameters(Request request) {
        request = prepareRequest(request, comment.getId().intValue());
        int id = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("content", comment.getBody());
        request.addParameter("object_id", String.valueOf(id));
        return request;
    }
}