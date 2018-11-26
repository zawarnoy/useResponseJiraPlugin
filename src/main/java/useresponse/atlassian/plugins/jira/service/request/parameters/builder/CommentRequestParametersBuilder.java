package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentConverter;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

public class CommentRequestParametersBuilder extends RequestParametersBuilder {

    protected CommentLinkManager commentLinkManager;
    protected UseResponseObjectManager useResponseObjectManager;

    public CommentRequestParametersBuilder(CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager) {
        this.commentLinkManager = commentLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
    }

    public CommentRequestParametersBuilder addStandardParametersForRequest(Comment comment) {
        requestMap = addContent             (requestMap, comment);
        requestMap = addJiraCommentIdToMap  (requestMap, comment.getId().intValue());
        requestMap = addJiraIssueIdToMap    (requestMap, comment);
        requestMap = addHtmlTreat           (requestMap);
        return this;
    }

    private Map<Object, Object> addContent(Map<Object, Object> map, Comment comment) {
        map.put("content", ContentConverter.convert(comment));
        return map;
    }

    public Map<Object, Object> addJiraCommentIdToMap(Map<Object, Object> map, int commentId) {
        map.put("jira_comment_id", commentId);
        return map;
    }

    private Map<Object, Object> addJiraIssueIdToMap(Map<Object, Object> map, Comment comment) {
        map.put("jira_issue_id", comment.getIssue().getKey());
        return map;
    }

    public CommentRequestParametersBuilder addUseResponseCommentIdToMap(WithId entity) {
        CommentLink object = commentLinkManager.findByJiraId(entity.getId().intValue());
        if (object != null) {
            requestMap.put("useresponse_comment_id", String.valueOf(object.getUseResponseCommentId()));
        }
        return this;
    }

    public CommentRequestParametersBuilder addUseResponseObjectIdToMap(Comment comment) {
        UseResponseObject object = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue());
        requestMap.put("useresponse_object_id", object.getUseResponseId());
        return this;
    }

    public CommentRequestParametersBuilder addCreatedAt(Comment comment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        requestMap.put("created_at", dateFormat.format(comment.getCreated()));
        return this;
    }

    public CommentRequestParametersBuilder addParametersForDelete(int useResponseCommentId) {
        CommentLink link = commentLinkManager.findByUseResponseId(useResponseCommentId);
        this.requestMap.put("useresponse_comment_id", link.getUseResponseCommentId());
        this.requestMap.put("jira_comment_id", link.getJiraCommentId());
        if (!Storage.userWhoPerformedAction.equals("")) {
            this.requestMap.put("force_author", Storage.userWhoPerformedAction);
        }
        return this;
    }

    @Override
    public <T extends WithId> CommentRequestParametersBuilder addAuthorToRequest(T entity) {
        requestMap.put("force_author", ((Comment) entity).getAuthorApplicationUser().getEmailAddress());
        return this;
    }
}
