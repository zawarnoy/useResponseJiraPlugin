package useresponse.atlassian.plugins.jira.service.request.parameters.builder;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.service.converter.content.ContentForSendingConverter;

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
        requestMap = addContent             (requestMap, comment);
        requestMap = addJiraCommentIdToMap  (requestMap, comment);
        requestMap = addJiraIssueIdToMap    (requestMap, comment);
        requestMap = addHtmlTreat           (requestMap);
        return this;
    }

    private Map<Object, Object> addContent(Map<Object, Object> map, Comment comment) {
        map.put("content", ContentForSendingConverter.convert(comment));
        return map;
    }

    private Map<Object, Object> addJiraCommentIdToMap(Map<Object, Object> map, Comment comment) {
        map.put("jira_comment_id", comment.getId().intValue());
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
        requestMap.put("created_at", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(comment.getCreated()));
        return this;
    }

    @Override
    public <T extends WithId> CommentRequestParametersBuilder addAuthorToRequest(T entity) {
        requestMap.put("force_author", ((Comment) entity).getAuthorApplicationUser().getEmailAddress());
        return this;
    }
}
