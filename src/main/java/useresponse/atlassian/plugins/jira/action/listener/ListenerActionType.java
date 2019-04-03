package useresponse.atlassian.plugins.jira.action.listener;

public interface ListenerActionType {
    int CREATE_ISSUE_ID = 1;
    int UPDATE_ISSUE_ID = 2;
    int DELETE_ISSUE_ID = 3;
    int CREATE_COMMENT_ID = 4;
    int UPDATE_COMMENT_ID = 5;
    int DELETE_COMMENT_ID = 6;
    int UPDATE_ISSUE_LINK_ID = 7;
}
