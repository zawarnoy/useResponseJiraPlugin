package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionFactory;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.listener.comment.CommentActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.comment.CreateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.DeleteCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.UpdateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.*;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import com.atlassian.activeobjects.external.ActiveObjects;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.service.CommentsService;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestParametersBuilder;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Component
public class IssueEventListener implements InitializingBean, DisposableBean {

    @JiraImport
    private final EventPublisher eventPublisher;

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private ListenerActionFactory issueActionFactory;

    @Autowired
    private CommentActionFactory commentActionFactory;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    public IssueEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Called when the plugin has been enabled.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {

        UseResponseObject object = useResponseObjectManager.findByJiraId(issueEvent.getIssue().getId().intValue());

        if (object == null) {
            return;
        }

        boolean needOfSync = object.getNeedOfSync();
        boolean isTicket = "ticket".equals(object.getObjectType());

        if (!needOfSync || !isTicket || !Storage.needToExecuteAction) {
            Storage.needToExecuteAction = true;
            return;
        }

        Storage.isFromBinder = false;

        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) {
        Long typeId = issueEvent.getEventTypeId();

        Issue issue = issueEvent.getIssue();

        issueActionFactory.setEntity(issue);
        commentActionFactory.setEntity(issueEvent.getComment());

        // actions
        List<Action> actions = new ArrayList<>();

        if (issueEvent.getUser() != null) {
            Storage.userWhoPerformedAction = issueEvent.getUser().getEmailAddress();
        }

        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            actions.add(issueActionFactory.createAction(CreateIssueAction.class));
        } else if (typeId.equals(EventType.ISSUE_MOVED_ID)) {
            actions.add(issueActionFactory.createAction(UpdateIssueLinkAction.class));
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            actions.add(commentActionFactory.createAction(CreateCommentAction.class));
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            actions.add(commentActionFactory.createAction(UpdateCommentAction.class));
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
            actions.add(issueActionFactory.createAction(DeleteIssueAction.class));
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            Integer deletedCommentId = commentsService.getDeletedCommentId(issueEvent.getIssue());
            if (deletedCommentId != null) {
                commentActionFactory.setEntity(() -> (long) deletedCommentId);
                actions.add(commentActionFactory.createAction(DeleteCommentAction.class));
            }
        } else {

            actions.add(issueActionFactory.createAction(UpdateIssueAction.class));

            if (typeId.equals(EventType.ISSUE_ASSIGNED_ID)) {
                Comment comment = getCommentIfNeedSend(issue);
                commentActionFactory.setEntity(comment);

                if (comment != null) {
                    actions.add(commentActionFactory.createAction(CreateCommentAction.class));
                }
            }
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        Future<String> future = null;

        for (Action action : actions) {
            if (action != null) {
                future = executor.submit(action);
            }
        }
    }

    private Comment getCommentIfNeedSend(Issue issue) {
        Comment comment = null;
        if (commentLinkManager.findByIssueId(issue.getId().intValue()).size() != ComponentAccessor.getCommentManager().getComments(issue).size()) {
            comment = ComponentAccessor.getCommentManager().getLastComment(issue);
        }

        return comment;
    }
}
