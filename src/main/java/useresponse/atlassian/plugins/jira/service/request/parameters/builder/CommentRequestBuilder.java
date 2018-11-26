package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.service.CommentsService;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRequestBuilder {

    private CommentRequestParametersBuilder builder;
    public CommentLinkManager commentManager;

    public CommentRequestBuilder(CommentRequestParametersBuilder builder, CommentLinkManager commentLinkManager) {
        this.builder = builder;
        this.commentManager = commentLinkManager;
    }

    public Map<Object, Object> build(Comment comment) {
        return build(comment, true);
    }

    public Map<Object, Object> build(Comment comment, boolean notify) {
        if (commentManager.findByJiraId(comment.getId().intValue()) == null) {
            return buildNewCommentMap(comment, notify);
        } else {
            return buildUpdateCommentMap(comment, notify);
        }
    }

    private Map<Object, Object> buildNewCommentMap(Comment comment, boolean notify) {
        builder.setRequestMap(new HashMap<>());
        builder.
                addStandardParametersForRequest(comment).
                addCreatedAt(comment).
                addAuthorToRequest(comment).
                addAddAction().
                addNotifyFlag(notify);
        return builder.getRequestMap();

    }

    private Map<Object, Object> buildUpdateCommentMap(Comment comment, boolean notify) {
        builder.setRequestMap(new HashMap<>());
        builder.addStandardParametersForRequest(comment).
                addUseResponseObjectIdToMap(comment).
                addUseResponseCommentIdToMap(comment).
                addAuthorToRequest(comment).
                addEditAction().
                addNotifyFlag(notify);
        return builder.getRequestMap();
    }

    public Map<Object, Object> buildDeleteCommentMap(int useResponseCommentId) {
        builder.setRequestMap(new HashMap<>());
        builder.addParametersForDelete(useResponseCommentId);
        builder.addDeleteAction();
        return builder.getRequestMap();
    }

    public List<Map<Object, Object>> getDeletedComments(Issue issue) {

        List<Integer> deletedCommentsIds = CommentsService.getDeletedCommentsId(issue, commentManager);

        List<Map<Object, Object>> result = new ArrayList<>();

        if (deletedCommentsIds.size() > 0) {
            for (int id : deletedCommentsIds) {
                result.add(buildDeleteCommentMap(id));
            }
        }
        return result;
    }
}
