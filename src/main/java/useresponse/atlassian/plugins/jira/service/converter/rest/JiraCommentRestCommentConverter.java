package useresponse.atlassian.plugins.jira.service.converter.rest;

import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.rest.comment.CommentRestResourceModel;

import java.text.SimpleDateFormat;

public class JiraCommentRestCommentConverter {

    public static CommentRestResourceModel convert(Comment comment) {

        CommentRestResourceModel model = new CommentRestResourceModel();

        model.setId(comment.getId().intValue());
        model.setIssueId(comment.getIssue().getId().intValue());
        model.setAuthorEmaill(comment.getAuthorApplicationUser().getEmailAddress());
        model.setContent(comment.getBody());
        model.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comment.getCreated()));

        return model;
    }
}
