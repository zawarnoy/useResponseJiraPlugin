package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.listener.Action;
import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;


public class IssueActionFactory extends AbsctractListenerActionFactory {

    private StatusesLinkManager statusesLinkManager;
    private  IssueFileLinkManager issueFileLinkManager;
    private  PriorityLinkManager priorityLinkManager;
    private IssueRequestBuilder issueRequestBuilder;

    public IssueActionFactory(WithId entity, UseResponseObjectManager useResponseObjectManager, RendererManager rendererManager, PriorityLinkManager priorityLinkManager, PluginSettingsFactory pluginSettingsFactory, IssueFileLinkManager issueFileLinkManager, StatusesLinkManager statusesLinkManager, IssueRequestBuilder issueRequestBuilder) {
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.issueFileLinkManager = issueFileLinkManager;
        this.statusesLinkManager = statusesLinkManager;
        this.issueRequestBuilder = issueRequestBuilder;

        this.entity = entity;
    }


    @Override
    public Action createAction(Class actionClass) {
        if (actionClass.getCanonicalName().equals(CreateIssueAction.class.getCanonicalName())) {
            return new CreateIssueAction((Issue) entity, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, ComponentAccessor.getComponent(DefaultAttachmentManager.class), issueFileLinkManager, issueRequestBuilder);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueAction.class.getCanonicalName())) {
            return new UpdateIssueAction((Issue) entity, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, ComponentAccessor.getComponent(DefaultAttachmentManager.class), issueFileLinkManager, statusesLinkManager, issueRequestBuilder);
        } else if (actionClass.getCanonicalName().equals(DeleteIssueAction.class.getCanonicalName())) {
            return new DeleteIssueAction((Issue) entity, useResponseObjectManager, pluginSettingsFactory);
        } else {
            return null;
        }
    }

    @Override
    public void setEntity(WithId entity) {
        this.entity = entity;
    }
}
