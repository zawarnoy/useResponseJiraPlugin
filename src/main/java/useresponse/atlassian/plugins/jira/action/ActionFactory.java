package useresponse.atlassian.plugins.jira.action;

public interface ActionFactory {
    <T extends Action> T createAction(Class actionClass);
}
