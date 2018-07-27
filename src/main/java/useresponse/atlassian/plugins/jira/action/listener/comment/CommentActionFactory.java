package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.entity.WithId;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.listener.Action;
import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;

public class CommentActionFactory extends AbsctractListenerActionFactory {

    public CommentActionFactory(WithId entity, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory, CommentLinkManager commentLinkManager) {
        this.entity = entity;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.commentLinkManager = commentLinkManager;
    }

    @Override
    public Action createAction(Class actionClass) {
        if (actionClass.getCanonicalName().equals(CreateCommentAction.class.getCanonicalName())) {
            return new CreateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(UpdateCommentAction.class.getCanonicalName())) {
            return new UpdateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory);
        } else if (actionClass.getCanonicalName().equals(DeleteCommentAction.class.getCanonicalName())) {
            return new DeleteCommentAction((Comment) entity, commentLinkManager, pluginSettingsFactory);
        } else {
            return null;
        }
    }

    @Override
    public void setEntity(WithId entity) {
        this.entity = entity;
    }
}
