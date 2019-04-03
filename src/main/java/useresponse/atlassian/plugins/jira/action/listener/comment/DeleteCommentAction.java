package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("deleteCommentAction")
public class DeleteCommentAction extends AbstractCommentAction {

    public DeleteCommentAction() {
        this.request = new PostRequest();
        this.actionType = ListenerActionType.DELETE_COMMENT_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        Map<Object, Object> requestMap = new HashMap<>();
        List<Map> commentsList = new ArrayList<>();
        Map<Object, Object> deletedCommentMap = new HashMap<>();
        CommentLink link = commentLinkManager.findByUseResponseId(comment.getId().intValue());
        deletedCommentMap.put("useresponse_comment_id", link.getUseResponseCommentId());
        deletedCommentMap.put("jira_comment_id", link.getJiraCommentId());
        deletedCommentMap.put("force_author", Storage.userWhoPerformedAction);
        deletedCommentMap.put("action", "delete");
        commentsList.add(deletedCommentMap);
        requestMap.put("comments", commentsList);
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
