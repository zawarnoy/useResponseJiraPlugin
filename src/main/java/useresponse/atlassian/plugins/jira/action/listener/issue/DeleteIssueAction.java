package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.action.listener.type.ActionType;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class DeleteIssueAction extends AbstractIssueAction {

    public DeleteIssueAction(Issue issue, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory) {
        this.issue = issue;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new DeleteRequest();
        this.actionType = ActionType.DELETE_ISSUE_ID;
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
