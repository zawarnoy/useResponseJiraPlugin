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
import useresponse.atlassian.plugins.jira.manager.impl.*;
import useresponse.atlassian.plugins.jira.service.IssueActionService;
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
        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) {
        Long typeId = issueEvent.getEventTypeId();
        IssueActionService issueActionService = new IssueActionService(
                pluginSettingsFactory,
                commentLinkManager,
                useResponseObjectManager,
                statusesLinkManager,
                priorityLinkManager,
                ComponentAccessor.getComponent(DefaultAttachmentManager.class),
                rendererManager,
                issueFileLinkManager
        );

        if (!Boolean.parseBoolean(pluginSettings.getAutosendingFlag())) {
            return;
        }

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
