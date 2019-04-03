package useresponse.atlassian.plugins.jira.action.listener.comment;

import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.ListenerAction;
import useresponse.atlassian.plugins.jira.context.ApplicationContextProvider;

public class CommentActionFactory extends AbsctractListenerActionFactory {

    public CommentActionFactory() {
    }

    @Override
    public ListenerAction createAction(Class actionClass) {

        ListenerAction action;

        if (actionClass.getCanonicalName().equals(CreateCommentAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("createCommentAction", CreateCommentAction.class);
        } else if (actionClass.getCanonicalName().equals(UpdateCommentAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("updateCommentAction", UpdateCommentAction.class);
        } else if (actionClass.getCanonicalName().equals(DeleteCommentAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("deleteCommentAction", DeleteCommentAction.class);
        } else {
            return null;
        }

        action.setEntity(entity);

        return action;
    }
}
