package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;

import java.util.HashMap;
import java.util.Map;

public class CommentRequestBuilder {

    private CommentRequestParametersBuilder builder;
    private CommentLinkManager commentManager;

    public CommentRequestBuilder(CommentRequestParametersBuilder builder, CommentLinkManager commentLinkManager) {
        this.builder = builder;
        this.commentManager = commentLinkManager;
    }

    public Map<Object, Object> build(Comment comment) {
        if (commentManager.findByJiraId(comment.getId().intValue()) == null) {
            return buildNewCommentMap(comment);
        } else {
            return buildUpdateCommentMap(comment);
        }
    }

    private Map<Object, Object> buildNewCommentMap(Comment comment) {
        builder.setRequestMap(new HashMap<>());
        builder.
                addStandardParametersForRequest(comment).
                addCreatedAt(comment).
                addAuthorToRequest(comment).
                addAddAction();
        return builder.getRequestMap();

    }

    private Map<Object, Object> buildUpdateCommentMap(Comment comment) {
        builder.setRequestMap(new HashMap<>());
        builder.addStandardParametersForRequest(comment).
                addUseResponseObjectIdToMap(comment).
                addUseResponseCommentIdToMap(comment).
                addEditAction();
        return builder.getRequestMap();
    }
}
