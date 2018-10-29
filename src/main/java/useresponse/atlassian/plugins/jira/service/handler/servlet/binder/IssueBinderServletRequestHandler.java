package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import com.atlassian.jira.component.ComponentAccessor;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import java.util.HashMap;

public class IssueBinderServletRequestHandler implements Handler<String, String> {

    private final UseResponseObjectManager useResponseObjectManager;
    private final CommentLinkManager commentLinkManager;

    public IssueBinderServletRequestHandler(UseResponseObjectManager useResponseObjectManager, CommentLinkManager commentLinkManager) {
        this.useResponseObjectManager   = useResponseObjectManager;
        this.commentLinkManager         = commentLinkManager;
    }

    @Override
    public String handle(String response) {

        String responseForUser;

        try {

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(response);
            JSONObject data = (JSONObject) object.get("success");

            if (useResponseObjectManager != null) {
                JSONObject issueData = (JSONObject) data.get("issue");
                if (issueData != null) {
                    handleIssueData(issueData);
                }
            }

            if (commentLinkManager != null) {
                JSONArray commentsData = (JSONArray) data.get("comments");
                if (commentsData != null) {
                    handleCommentsData(commentsData);
                }
            }


            responseForUser = generateResponseForUser();
        } catch (ParseException e) {
            responseForUser = generateExceptionResponse();
            e.printStackTrace();
        }

        return responseForUser;
    }

    private void handleIssueData(JSONObject issueData) {
        int use_response_id = Integer.valueOf((String.valueOf(issueData.get("use_response_id"))));
        String jiraKey = String.valueOf(issueData.get("jira_key"));
        String objectType = (String) issueData.get("object_type");
        boolean sync = "1".equals((String) issueData.get("sync"));
        useResponseObjectManager.findOrAdd(use_response_id, ComponentAccessor.getIssueManager().getIssueObject(jiraKey).getId().intValue(), objectType, sync);
    }

    private void handleCommentsData(JSONArray commentsData) {
        for (int i = 0; i < commentsData.size(); i++) {
            JSONObject object =  (JSONObject) commentsData.get(i);
            if(object != null){
                handleOneCommentData(object);
            }
        }
    }

    private void handleOneCommentData(JSONObject commentData) {
        int use_response_comment_id = Integer.valueOf((String.valueOf(commentData.get("use_response_comment_id"))));
        int jira_comment_id = Integer.valueOf((String.valueOf(commentData.get("jira_comment_id"))));
        int issueId = ComponentAccessor.getCommentManager().getCommentById(Long.parseLong(String.valueOf(jira_comment_id))).getIssue().getId().intValue();
        commentLinkManager.findOrAdd(use_response_comment_id, jira_comment_id, issueId);
    }

    private String generateResponseForUser() {
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("message", "Successfully synchronized with UseResponse");
        return (new Gson()).toJson(responseMap);
    }

    private String generateExceptionResponse() {
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "error");
        responseMap.put("message", "Bad response from UseResponse server");
        return (new Gson()).toJson(responseMap);
    }
}
