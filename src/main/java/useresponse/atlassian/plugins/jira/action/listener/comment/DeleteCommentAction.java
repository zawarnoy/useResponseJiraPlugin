package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.action.ActionType;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.Request;

import java.util.HashMap;
import java.util.List;

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
        try{
            StringMap data = (new Gson()).fromJson(response, StringMap.class);
            StringMap result = (StringMap) data.get("success");
            if (result != null) {
                commentLinkManager.deleteByUseResponseId(((Double) result.get("id")).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
