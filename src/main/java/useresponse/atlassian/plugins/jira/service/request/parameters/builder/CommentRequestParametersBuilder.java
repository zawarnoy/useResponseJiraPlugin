package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;

import java.text.SimpleDateFormat;
import java.util.Map;

public class CommentRequestParametersBuilder extends RequestParametersBuilder {

    protected CommentLinkManager commentLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;

    public CommentRequestParametersBuilder(CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager) {
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
    }

    public CommentRequestParametersBuilder addStandardParametersForRequest(Comment comment) {
        requestMap = addContent(requestMap, comment);
        requestMap = addHtmlTreat(requestMap);

        return this;
    }

    public Map<Object, Object> addContent(Map<Object, Object> map, Comment comment) {
        map.put("content", comment.getBody());
        return map;
    }

    public CommentRequestParametersBuilder addUseResponseIdToMap(WithId entity) {
        CommentLink object = commentLinkManager.findByJiraId(entity.getId().intValue());
        if (object != null) {
            requestMap.put("useresponse_comment_id", String.valueOf(object.getUseResponseCommentId()));
        }
        return this;
    }

    public CommentRequestParametersBuilder addObjectIdToMap(Comment comment) {
        UseResponseObject object = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue());
        requestMap.put("object_id", object.getUseResponseId());
        return this;
    }

    public CommentRequestParametersBuilder addCreatedAt(Comment comment) {
        requestMap.put("created_at", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(comment.getCreated()));
        return this;
    }

    public CommentRequestParametersBuilder addJiraObjectIdToMap(Comment comment) {
        requestMap.put("jira_object_id", comment.getIssue().getId().intValue());
        return this;
    }

    @Override
    public <T extends WithId> CommentRequestParametersBuilder addAuthorToRequest(T entity) {
        requestMap.put("force_author", ((Comment) entity).getAuthorApplicationUser().getEmailAddress());
        return this;
    }
}
