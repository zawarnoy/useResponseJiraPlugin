package useresponse.atlassian.plugins.jira.action.listener;

import useresponse.atlassian.plugins.jira.action.listener.Action;

public interface ActionFactory {
    <T extends Action> T createAction(Class actionClass);
}
