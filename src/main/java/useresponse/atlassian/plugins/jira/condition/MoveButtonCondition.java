package useresponse.atlassian.plugins.jira.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

public class MoveButtonCondition extends AbstractWebCondition {

    @Autowired
    private UseResponseObjectManagerImpl objectManager;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private PluginSettingsImpl pluginSettings;

    public MoveButtonCondition() {
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
        return settingsService.testURConnection() &&
                linkNotExists(currentIssue) &&
                !Boolean.parseBoolean(pluginSettings.getAutosendingFlag());
    }

    private boolean linkNotExists(Issue issue) {
        return objectManager.findByJiraId(issue.getId().intValue()) == null;
    }
}
