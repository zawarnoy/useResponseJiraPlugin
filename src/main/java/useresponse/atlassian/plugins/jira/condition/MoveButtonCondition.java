package useresponse.atlassian.plugins.jira.condition;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.inject.Inject;

public class MoveButtonCondition extends AbstractWebCondition {


    private PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public MoveButtonCondition(@ComponentImport PluginSettingsFactory pluginSettignsFactory) {
        this.pluginSettingsFactory = pluginSettignsFactory;
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        return !Boolean.parseBoolean(pluginSettings.getAutosendingFlag());
    }
}
