package useresponse.atlassian.plugins.jira.action.listener.comment;

import com.atlassian.jira.issue.comments.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.service.request.parameters.builder.CommentRequestBuilder;

public class CommentActionFactory extends AbsctractListenerActionFactory {

    @Autowired
    CommentRequestBuilder commentRequestBuilder;

    public CommentActionFactory() {
    }

    @Override
    public Action createAction(Class actionClass) {
        if (actionClass.getCanonicalName().equals(CreateCommentAction.class.getCanonicalName())) {
            return new CreateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory, commentRequestBuilder);
        } else if (actionClass.getCanonicalName().equals(UpdateCommentAction.class.getCanonicalName())) {
            return new UpdateCommentAction((Comment) entity, commentLinkManager, useResponseObjectManager, pluginSettingsFactory, commentRequestBuilder);
        } else if (actionClass.getCanonicalName().equals(DeleteCommentAction.class.getCanonicalName())) {
            return new DeleteCommentAction(entity, commentLinkManager, pluginSettingsFactory);
        } else {
            return null;
        }
    }
}
