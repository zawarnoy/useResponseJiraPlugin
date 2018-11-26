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
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.GetRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteCommentAction extends AbstractCommentAction {

    Logger log = LoggerFactory.getLogger(DeleteCommentAction.class);

    public DeleteCommentAction(WithId comment, CommentLinkManager commentLinkManager, PluginSettingsFactory pluginSettingsFactory) {
        this.comment = comment;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.request = new PostRequest();
        this.actionType = ActionType.DELETE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        Map<Object, Object> requestMap = new HashMap<>();
        Map<Object, Object> commentMap = parametersBuilder.buildDeleteCommentMap(comment.getId().intValue());
        ArrayList<Map<Object, Object>> comments = new ArrayList<>();
        comments.add(commentMap);
        requestMap.put("comments", comments);
        request.addParameter(requestMap);
        return request;
    }

    @Override
    protected String createUrl() {
        return getSpecialApiPath();
    }

    @Override
    protected void handleResponse(String response) {
        StringMap data = (new Gson()).fromJson(response, StringMap.class);
        StringMap result = (StringMap) data.get("success");
        List<Map<String, String>> commentsList = (List<Map<String, String>>) result.get("comments");
        for (Map<String, String> commentMap : commentsList) {
            commentLinkManager.deleteByUseResponseId(Integer.parseInt(commentMap.get("use_response_comment_id")));
        }
    }
}
