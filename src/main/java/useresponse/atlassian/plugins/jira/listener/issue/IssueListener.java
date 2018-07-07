package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.impl.*;
import useresponse.atlassian.plugins.jira.model.*;
import useresponse.atlassian.plugins.jira.service.IssueActionService;
import com.atlassian.jira.config.DefaultStatusManager;

import com.atlassian.jira.issue.managers.DefaultAttachmentManager;


@Component
public class IssueListener implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(IssueListener.class);

    @JiraImport
    private final EventPublisher eventPublisher;

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private StatusesLinkManagerImpl statusesLinkManager;

    @Autowired
    private PriorityLinkManagerImpl priorityLinkManager;

    @Autowired
    private URPriorityManagerImpl urPriorityManager;

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public IssueListener(EventPublisher eventPublisher, PluginSettingsFactory pluginSettingsFactory) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
     * Called when the plugin has been enabled.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        migrateActiveObjects();
        createPriorities();
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
        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) throws Exception {
        Long typeId = issueEvent.getEventTypeId();
        IssueActionService issueActionService = new IssueActionService(
                pluginSettingsFactory,
                commentLinkManager,
                useResponseObjectManager,
                statusesLinkManager,
                priorityLinkManager,
                ComponentAccessor.getComponent(DefaultAttachmentManager.class)
        );

        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            issueActionService.createAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            issueActionService.createCommentAction(issueEvent.getComment());
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            issueActionService.updateCommentAction(issueEvent.getComment());
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
            issueActionService.deleteAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            issueActionService.deleteCommentAction(issueEvent);
        } else {
            issueActionService.updateAction(issueEvent.getIssue());
        }
    }

    private void migrateActiveObjects() {
        ao.migrate(StatusesLink.class);
        ao.migrate(CommentLink.class);
        ao.migrate(UseResponseObject.class);
        ao.migrate(URPriority.class);
        ao.migrate(PriorityLink.class);
    }

    private void createPriorities() {
        urPriorityManager.findOrAdd("low", "Low");
        urPriorityManager.findOrAdd("normal", "Normal");
        urPriorityManager.findOrAdd("high", "High");
        urPriorityManager.findOrAdd("urgent", "Urgent");
    }

}
