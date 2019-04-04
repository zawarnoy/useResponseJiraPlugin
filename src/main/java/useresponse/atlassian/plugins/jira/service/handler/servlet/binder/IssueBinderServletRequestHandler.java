package useresponse.atlassian.plugins.jira.service.handler.servlet.binder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.google.gson.Gson;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scanned
@Named("issueBinderRequestHandler")
public class IssueBinderServletRequestHandler implements Handler<String, String> {

    @Inject
    @Named("useResponseObjectManager")
    private UseResponseObjectManager useResponseObjectManager;

    @Inject
    @Named("commentLinkManager")
    private CommentLinkManager commentLinkManager;

    @Override
    public String handle(String response) {
        String responseForUser;

        try {
            Map decodedResponse = (new Gson()).fromJson(response, Map.class);

            Map data = (Map) decodedResponse.get("success");

            if (useResponseObjectManager != null) {
                Map issueData = (Map) data.get("issue");
                if (issueData != null) {
                    handleIssueData(issueData);
                }
            }

            if (commentLinkManager != null) {
                List<Map> commentsData = (List) data.get("comments");
                if (commentsData != null) {
                    handleCommentsData(commentsData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseForUser = generateResponseForUser();

        return responseForUser;
    }

    private void handleIssueData(Map issueData) {
        int use_response_id = Integer.valueOf((String.valueOf(issueData.get("use_response_id"))));
        String jiraKey = String.valueOf(issueData.get("jira_key"));
        String objectType = (String) issueData.get("object_type");
        boolean sync = "1".equals((String) issueData.get("sync"));
        useResponseObjectManager.findOrAdd(use_response_id, ComponentAccessor.getIssueManager().getIssueObject(jiraKey).getId().intValue(), objectType, sync);
    }

    private void handleCommentsData(List<Map> commentsData) {
        for(Map commentData : commentsData) {
            handleOneCommentData(commentData);
        }
    }

    private void handleOneCommentData(Map commentData) {
        int use_response_comment_id = Double.valueOf((String) commentData.get("use_response_comment_id")).intValue();
        int jira_comment_id = Double.valueOf((Double) commentData.get("jira_comment_id")).intValue();
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
