package useresponse.atlassian.plugins.jira.action.listener.issue;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.Request;

public class DeleteIssueAction extends AbstractIssueAction {

    public DeleteIssueAction(IssueEvent issueEvent, UseResponseObjectManager useResponseObjectManager, PluginSettingsFactory pluginSettingsFactory) {
        this.issueEvent = issueEvent;
        this.useResponseObjectManager = useResponseObjectManager;
        this.pluginSettingsFactory = pluginSettingsFactory;

        this.request = new DeleteRequest();
    }

    @Override
    protected Request addParameters(Request request) {
        return prepareRequest(request, issueEvent.getIssue().getId().intValue());
    }

    @Override
    protected String createUrl() {
        int useResponseId = useResponseObjectManager.findByJiraId(issueEvent.getIssue().getId().intValue()).getUseResponseId();
        return collectUrl("objects/" + useResponseId + "/trash.json");
    }

    @Override
    protected void handleResponse(String response) throws Exception {
    }
}
