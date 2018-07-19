package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.ActionFactory;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.comment.CreateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.DeleteCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.UpdateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.CreateIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.DeleteIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.UpdateIssueAction;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import com.atlassian.activeobjects.external.ActiveObjects;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;


@Component
public class IssueListener implements InitializingBean, DisposableBean {

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

    private PluginSettings pluginSettings;

    @Autowired
    public IssueListener(EventPublisher eventPublisher, PluginSettingsFactory pluginSettingsFactory, ActiveObjects ao) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.ao = ao;
        this.pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
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
        if (!Boolean.parseBoolean(pluginSettings.getAutosendingFlag())) {
            return;
        }
        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) {
        Long typeId = issueEvent.getEventTypeId();

        ActionFactory actionFactory = new ListenerActionFactory(
                issueEvent.getIssue(),
                useResponseObjectManager,
                rendererManager,
                priorityLinkManager,
                pluginSettingsFactory,
                issueFileLinkManager,
                statusesLinkManager,
                commentLinkManager
        );

        Action action;

        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            action = actionFactory.createAction(CreateIssueAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            action = actionFactory.createAction(CreateCommentAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            action = actionFactory.createAction(UpdateCommentAction.class);
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
            action = actionFactory.createAction(DeleteIssueAction.class);
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            action = actionFactory.createAction(DeleteCommentAction.class);
        } else {
            action = actionFactory.createAction(UpdateIssueAction.class);
        }

        if (action != null)
            (new Thread(action, "Issue event Thread")).start();
    }


}
