package useresponse.atlassian.plugins.jira.action.listener.issue;

import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.listener.ListenerActionType;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.Request;

@Component("deleteIssueAction")
public class DeleteIssueAction extends AbstractIssueAction {

    public DeleteIssueAction() {
        this.request = new DeleteRequest();
        this.actionType = ListenerActionType.DELETE_ISSUE_ID;
    }

    @Override
    protected Request addParameters(Request request) {
        return request;
    }

    @Override
    protected String createUrl() {
        int useResponseId = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        return collectUrl("objects/" + useResponseId + "/trash.json");
    }

    @Override
    protected void handleResponse(String response) {
    }
}
