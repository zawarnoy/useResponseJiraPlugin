package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IssueBinderServletHandler implements Handler<String, String> {

    private final UseResponseObjectManager useResponseObjectManager;
    private final CommentLinkManager commentLinkManager;

    public IssueBinderServletHandler(UseResponseObjectManager useResponseObjectManager, CommentLinkManager commentLinkManager) {
        this.useResponseObjectManager = useResponseObjectManager;
        this.commentLinkManager = commentLinkManager;
    }

    @Override
    public String handle(String response) {

        String responseForUser;


        try {

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(response);
            JSONObject data = (JSONObject) object.get("success");

            JSONObject issueData = (JSONObject) data.get("issue");
            if (issueData != null) {
                handleIssueData(issueData);
            }

            JSONArray commentsData = (JSONArray) data.get("comments");
            if (commentsData != null) {
                handleCommentsData(commentsData);
            }

            responseForUser = generateResponseForUser();
        } catch (ParseException e) {
            responseForUser = generateExceptionResponse();
            e.printStackTrace();
        }


        return response;//responseForUser;
    }

    private void handleIssueData(JSONObject issueData) {
        int use_response_id = Integer.valueOf((String.valueOf( issueData.get("use_response_id"))));
        int jira_id = Integer.valueOf((String.valueOf (issueData.get("jira_id"))));
        useResponseObjectManager.findOrAdd(use_response_id, jira_id);
    }

    private void handleCommentsData(JSONArray commentsData) {
        for(int i=0; i < commentsData.size(); i++) {
            handleOneCommentData( (JSONObject)commentsData.get(i));
        }
    }

    private void handleOneCommentData(JSONObject commentData) {
        int use_response_comment_id = Integer.valueOf((String.valueOf (commentData.get("use_response_comment_id"))));
        int jira_comment_id = Integer.valueOf((String.valueOf (commentData.get("jira_comment_id"))));
        commentLinkManager.findOrAdd(use_response_comment_id, jira_comment_id);
    }

    private String generateResponseForUser() {
        return (new Gson()).toJson(new HashMap<String, String>() {{
            put("status", "success");
            put("message", "Successfully synchronized with UseResponse");
        }});
    }

    private String generateExceptionResponse() {
        return (new Gson()).toJson(new HashMap<String, String>() {{
            put("status", "error");
            put("message", "Bad response from UseResponse server");
        }});
    }
}
