package useresponse.atlassian.plugins.jira.action.listener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.ActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.comment.CreateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.DeleteCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.comment.UpdateCommentAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.CreateIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.DeleteIssueAction;
import useresponse.atlassian.plugins.jira.action.listener.issue.UpdateIssueAction;
import useresponse.atlassian.plugins.jira.manager.*;

public class ListenerActionFactory implements ActionFactory {

    private final UseResponseObjectManager useResponseObjectManager;
    private final RendererManager rendererManager;
    private final PriorityLinkManager priorityLinkManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final IssueFileLinkManager issueFileLinkManager;
    private final WithId entity;
    private final CommentLinkManager commentLinkManager;
    private final StatusesLinkManager statusesLinkManager;

    public ListenerActionFactory(WithId entity, UseResponseObjectManager useResponseObjectManager, RendererManager rendererManager, PriorityLinkManager priorityLinkManager, PluginSettingsFactory pluginSettingsFactory, IssueFileLinkManager issueFileLinkManager, StatusesLinkManager statusesLinkManager, CommentLinkManager commentLinkManager) {
        this.useResponseObjectManager = useResponseObjectManager;
        this.rendererManager = rendererManager;
        this.priorityLinkManager = priorityLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.issueFileLinkManager = issueFileLinkManager;
        this.statusesLinkManager = statusesLinkManager;
        this.commentLinkManager = commentLinkManager;
        this.entity = entity;
    }


    @Override
    public Action createAction(Class actionClass) {
        Action action;
        if (actionClass.getCanonicalName().equals(CreateIssueAction.class.getCanonicalName())) {
            action = new CreateIssueAction((Issue) entity, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, ComponentAccessor.getComponent(DefaultAttachmentManager.class), issueFileLinkManager);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueAction.class.getCanonicalName())) {
            action = new UpdateIssueAction((Issue) entity, useResponseObjectManager, rendererManager, priorityLinkManager, pluginSettingsFactory, ComponentAccessor.getComponent(DefaultAttachmentManager.class), issueFileLinkManager, statusesLinkManager);
        } else if (actionClass.getCanonicalName().equals(DeleteIssueAction.class.getCanonicalName())) {
            action = new DeleteIssueAction((Issue) entity, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(CreateCommentAction.class.getCanonicalName())) {
            action = new CreateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(UpdateCommentAction.class.getCanonicalName())) {
            action = new UpdateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(DeleteCommentAction.class.getCanonicalName())) {
            action = new DeleteCommentAction((Comment) entity, commentLinkManager, pluginSettingsFactory);
        } else {
            action = null;
        }

        return action;
    }
}
