package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.manager.IssueFileLinkManager;
import useresponse.atlassian.plugins.jira.manager.PriorityLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.IssueRequestBuilder;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

public class IssueActionFactory extends AbsctractListenerActionFactory {

    @Inject
    @Named("statusesLinkManager")
    private StatusesLinkManager statusesLinkManager;

    @Inject
    @Named("issueFileLinkManager")
    private IssueFileLinkManager issueFileLinkManager;

    @Inject
    @Named("priorityLinkManager")
    private PriorityLinkManager priorityLinkManager;

    @Autowired
    private IssueRequestBuilder issueRequestBuilder;

    public IssueActionFactory() {
    }

    @Override
    public Action createAction(Class actionClass) {
        if (actionClass.getCanonicalName().equals(CreateIssueAction.class.getCanonicalName())) {
            return new CreateIssueAction((Issue) entity);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueAction.class.getCanonicalName())) {
            return new UpdateIssueAction((Issue) entity, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, ComponentAccessor.getComponent(DefaultAttachmentManager.class), issueFileLinkManager, statusesLinkManager, issueRequestBuilder);
        } else if (actionClass.getCanonicalName().equals(DeleteIssueAction.class.getCanonicalName())) {
            return new DeleteIssueAction((Issue) entity, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueLinkAction.class.getCanonicalName())) {
            return new UpdateIssueLinkAction((Issue) entity, pluginSettingsFactory, issueRequestBuilder);
        } else {
            return null;
        }
    }
}
