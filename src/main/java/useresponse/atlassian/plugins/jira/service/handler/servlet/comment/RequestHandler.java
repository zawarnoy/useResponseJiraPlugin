package useresponse.atlassian.plugins.jira.service.handler.servlet.comment;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler implements Handler<String, String> {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);


    private ApplicationUser loggedUser;
    private CommentLinkManager commentLinkManager;

    public RequestHandler(ApplicationUser loggedUser, CommentLinkManager commentLinkManager) {
        this.loggedUser = loggedUser;
        this.commentLinkManager = commentLinkManager;
    }

    @Override
    public String handle(String jsonData) throws IOException, ParseException {

        List<Map<String, String>> commentsData = (new Gson()).fromJson(jsonData, List.class);
        List<Map> result = new ArrayList<>();
        for (int i = 0; i < commentsData.size(); i++) {
            result.add(handleOneComment(commentsData.get(i)));
        }
        return (new Gson()).toJson(result);
    }

    private Map<String, String> handleOneComment(Map<String, String> commentData) {
        Comment comment;
        CommentLink link = commentLinkManager.findByUseResponseId(Integer.parseInt(commentData.get("useresponse_comment_id")));
        if (link == null) {
            comment = createComment(commentData);
        } else {
            comment = updateComment(commentData, link);
        }
        commentLinkManager.findOrAdd(Integer.valueOf(commentData.get("useresponse_comment_id")), comment.getId().intValue());
        Map<String, String> response = new HashMap<>();
        response.put("useresponse_comment_id", commentData.get("useresponse_comment_id"));
        response.put("jira_comment_id", String.valueOf(comment.getId()));
        return response;
    }

    private Comment createComment(Map<String, String> commentData) {
        CommentManager commentManager = ComponentAccessor.getCommentManager();

        String issueKey = commentData.get("issue_key");

        Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
        ApplicationUser creator = handleCreatorName(commentData.get("username"));

        Comment comment = null;
        try {
            comment = commentManager.create(issue, creator, commentData.get("content"),null, null, (new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(commentData.get("created_at"))), false);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return comment;
    }

    private Comment updateComment(Map<String, String> commentData, CommentLink link) {
        CommentManager commentManager = ComponentAccessor.getCommentManager();
        MutableComment comment = commentManager.getMutableComment((long) link.getJiraCommentId());
        comment.setBody(commentData.get("content"));
        commentManager.update(comment, false);
        return comment;
    }

    private ApplicationUser handleCreatorName(String username) {
        UserManager userManager = ComponentAccessor.getUserManager();

        if (userManager.getUserByName(username) == null) {
            return this.loggedUser;
        }
        return userManager.getUserByName(username);
    }
}
