package useresponse.atlassian.plugins.jira.service.handler.servlet.comment;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.service.IssueService;
import useresponse.atlassian.plugins.jira.service.handler.Handler;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("commentServletRequestHandler")
public class CommentServletRequestHandler implements Handler<String, String> {

    private ApplicationUser loggedUser;

    @Inject
    @Named("commentLinkManager")
    private CommentLinkManager commentLinkManager;

    public CommentServletRequestHandler() {
        this.loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
    }

    @Override
    public String handle(String jsonData) throws IOException, ParseException {
        Gson gson = new Gson();
        List<Map<String, String>> commentsData = gson.fromJson(jsonData, List.class);
        List<Map> result = new ArrayList<>();
        for (int i = 0; i < commentsData.size(); i++) {
            result.add(handleOneComment(commentsData.get(i)));
        }
        return gson.toJson(result);
    }

    private Map<String, String> handleOneComment(Map<String, String> commentData) {
        Comment comment;
        Map<String, String> response = new HashMap<>();
        CommentLink link = commentLinkManager.findByUseResponseId(Integer.parseInt(commentData.get("useresponse_comment_id")));
        if (commentData.get("is_deleted") != null && link != null) {
            boolean status = deleteComment(commentData, link);
        } else {
            if (link == null) {
                comment = createComment(commentData);
            } else {
                comment = updateComment(commentData, link);
            }
            commentLinkManager.findOrAdd(Integer.valueOf(commentData.get("useresponse_comment_id")), comment.getId().intValue(), comment.getIssue().getId().intValue());
            response.put("useresponse_comment_id", commentData.get("useresponse_comment_id"));
            response.put("jira_comment_id", String.valueOf(comment.getId()));
        }
        return response;
    }

    private boolean deleteComment(Map<String, String> commentData, CommentLink link) {
        CommentManager manager = ComponentAccessor.getCommentManager();
        Comment comment = manager.getCommentById((long) link.getJiraCommentId());
        try {
            if (comment != null) {
                manager.delete(comment, false, ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser());
                commentLinkManager.delete(link);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private Comment createComment(Map<String, String> commentData) {
        CommentManager commentManager = ComponentAccessor.getCommentManager();

        String issueKey = commentData.get("issue_key");

        MutableIssue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey(issueKey);
        ApplicationUser creator = handleCreatorEmail(commentData.get("email"));

        Comment comment = null;
        Date date = new Date();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = format.parse(commentData.get("created_at"));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Date commentTime = ((new Date()).getTime() - date.getTime()) > 10000 ? date : new Date();
        String content = handleContent(issue, commentData.get("content"));
        comment = commentManager.create(issue, creator, content, null, null, commentTime, false);
        return comment;
    }

    private Comment updateComment(Map<String, String> commentData, CommentLink link) {
        CommentManager commentManager = ComponentAccessor.getCommentManager();
        MutableComment comment = commentManager.getMutableComment((long) link.getJiraCommentId());
        comment.setBody(commentData.get("content"));
        commentManager.update(comment, false);
        return comment;
    }

    private ApplicationUser handleCreatorEmail(String email) {
        try {
            return IssueService.findOrCreateUser(email);
        } catch (PermissionException | CreateException e) {
            e.printStackTrace();
        }
        return this.loggedUser;
    }

    private String handleContent(MutableIssue issue, String content) {
        return content;
    }
}
