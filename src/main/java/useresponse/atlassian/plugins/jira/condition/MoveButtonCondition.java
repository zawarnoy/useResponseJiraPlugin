package useresponse.atlassian.plugins.jira.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.service.SettingsService;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;

public class MoveButtonCondition extends AbstractWebCondition {

    @Autowired
    private UseResponseObjectManagerImpl objectManager;

    private PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public MoveButtonCondition(@ComponentImport PluginSettingsFactory pluginSettignsFactory) {
        this.pluginSettingsFactory = pluginSettignsFactory;
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
        return
                SettingsService.testURConnection(pluginSettingsFactory) &&
                objectManager.findByJiraId(currentIssue.getId().intValue()).getObjectType().equals("ticket") &&
                (!isLinkExists(currentIssue) || !Boolean.parseBoolean(pluginSettings.getAutosendingFlag()));
    }

    private boolean isLinkExists(Issue issue) {
        return objectManager.findByJiraId(issue.getId().intValue()) != null;
    }
}
