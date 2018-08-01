package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import java.util.HashMap;
import java.util.Iterator;

public class IssueBinderServletHandler implements Handler<String, String> {

    private final UseResponseObjectManager useResponseObjectManager;
    private final CommentLinkManager commentLinkManager;

    public IssueBinderServletHandler(UseResponseObjectManager useResponseObjectManager, CommentLinkManager commentLinkManager) {
        this.useResponseObjectManager = useResponseObjectManager;
        this.commentLinkManager = commentLinkManager;
    }

    @Override
    public String handle(String response) {

        Gson gson = new Gson();
        String responseForUser = null;

        JsonObject data = gson.fromJson(response, JsonObject.class);

        JsonObject issueData = data.getAsJsonObject("issue");
        if (issueData != null) {
            handleIssueData(issueData);
        }

        JsonArray commentsData = data.getAsJsonArray("comments");
        if (commentsData != null) {
            handleCommentsData(commentsData);
        }

        // Todo generate response for user
        responseForUser = generateResponseForUser();
        return responseForUser;
    }

    private void handleIssueData(JsonObject issueData) {
        int use_response_id = issueData.get("use_response_id").getAsInt();
        int jira_id = issueData.get("jira_id").getAsInt();
        useResponseObjectManager.findOrAdd(use_response_id, jira_id);
    }

    private void handleCommentsData(JsonArray commentsData) {
        for (JsonElement commentData : commentsData) {
            handleOneCommentData(commentData.getAsJsonObject());
        }
    }

    private void handleOneCommentData(JsonObject commentData) {
        int use_response_comment_id = commentData.get("use_response_comment_id").getAsInt();
        int jira_comment_id = commentData.get("jira_comment_id").getAsInt();
        commentLinkManager.findOrAdd(use_response_comment_id, jira_comment_id);
    }

    private String generateResponseForUser() {
        return  (new Gson()).toJson(new HashMap<String, String>(){{
            put("status", "success");
            put("message", "Successfully synchronized with UseResponse");
        }});
    }
}
