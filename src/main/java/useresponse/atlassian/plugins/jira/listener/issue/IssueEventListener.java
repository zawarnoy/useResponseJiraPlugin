package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import useresponse.atlassian.plugins.jira.action.listener.issue.CreateIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.DeleteIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.IssueActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.issue.UpdateIssueAction;
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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Component
public class IssueEventListener implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(IssueEvent.class);

    @JiraImport
    private final EventPublisher eventPublisher;

    @ComponentImport
    private final ActiveObjects ao;

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private StatusesLinkManagerImpl statusesLinkManager;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;

    @Autowired
    private RendererManager rendererManager;

    @Autowired
    private IssueFileLinkManagerImpl issueFileLinkManager;

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @ComponentImport
    private final AttachmentManager attachmentManager;

    private PluginSettings pluginSettings;

    @Autowired
    public IssueEventListener(EventPublisher eventPublisher, PluginSettingsFactory pluginSettingsFactory, ActiveObjects ao, AttachmentManager attachmentManager) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.ao = ao;
        this.pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        this.attachmentManager = attachmentManager;
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

        if(object == null) {
            return;
        }

        boolean needOfSync = object.getNeedOfSync();
        boolean isTicket = "ticket".equals(object.getObjectType());

        if (!(Storage.needToExecuteAction && needOfSync && isTicket)) {
            Storage.needToExecuteAction = true;
            return;
        }

        // various content processing for different sources
        Storage.isFromBinder = false;

        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) {
        Long typeId = issueEvent.getEventTypeId();

        IssueRequestBuilder issueRequestBuilder = new IssueRequestBuilder(
                new IssueRequestParametersBuilder(
                        rendererManager,
                        priorityLinkManager,
                        useResponseObjectManager,
                        attachmentManager,
                        issueFileLinkManager,
                        pluginSettingsFactory,
                        statusesLinkManager),
                useResponseObjectManager
        );

        CommentRequestBuilder commentRequestBuilder = new CommentRequestBuilder(
                new CommentRequestParametersBuilder(
                        commentLinkManager,
                        useResponseObjectManager),
                commentLinkManager
        );

        ListenerActionFactory issueActionFactory = new IssueActionFactory(
                issueEvent.getIssue(),
                useResponseObjectManager,
                rendererManager,
                priorityLinkManager,
                pluginSettingsFactory,
                issueFileLinkManager,
                statusesLinkManager,
                issueRequestBuilder);

        ListenerActionFactory commentActionFactory = new CommentActionFactory(
                issueEvent.getComment(),
                useResponseObjectManager,
                pluginSettingsFactory,
                commentLinkManager,
                commentRequestBuilder);


        Action action = null;

        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            action = issueActionFactory.createAction(CreateIssueAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            action = commentActionFactory.createAction(CreateCommentAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            action = commentActionFactory.createAction(UpdateCommentAction.class);
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
            action = commentActionFactory.createAction(DeleteIssueAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            Integer deletedCommentId = CommentsService.getDeletedCommentId(issueEvent.getIssue(), commentLinkManager);
            if(deletedCommentId == null) {
                action = null;
            }
            commentActionFactory.setEntity(() -> (long) deletedCommentId);
            action = commentActionFactory.createAction(DeleteCommentAction.class);
        } else {
            action = issueActionFactory.createAction(UpdateIssueAction.class);
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        Future<String> future = null;
        if (action != null) {
            future = executor.submit(action);
        }
    }
}
