package useresponse.atlassian.plugins.jira.action.listener.issue;

import useresponse.atlassian.plugins.jira.action.listener.AbsctractListenerActionFactory;
import useresponse.atlassian.plugins.jira.action.listener.ListenerAction;
import useresponse.atlassian.plugins.jira.context.ApplicationContextProvider;

public class IssueActionFactory extends AbsctractListenerActionFactory {

    public IssueActionFactory() {
    }

    @Override
    public ListenerAction createAction(Class actionClass) {

        ListenerAction action;

        if (actionClass.getCanonicalName().equals(CreateIssueAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("createIssueAction", CreateIssueAction.class);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("updateIssueAction", UpdateIssueAction.class);
        } else if (actionClass.getCanonicalName().equals(DeleteIssueAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("deleteIssueAction", DeleteIssueAction.class);
        } else if (actionClass.getCanonicalName().equals(UpdateIssueLinkAction.class.getCanonicalName())) {
            action = ApplicationContextProvider.getApplicationContext().getBean("updateIssueLinkAction", UpdateIssueLinkAction.class);
        } else {
            return null;
        }

        action.setEntity(entity);

        return action;
    }
}
