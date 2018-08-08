package useresponse.atlassian.plugins.jira.provider;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.DefaultWebFragmentContext;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.sun.org.apache.xpath.internal.operations.Bool;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import static useresponse.atlassian.plugins.jira.service.SettingsService.testURConnection;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UseResponseLinkButtonProvider extends AbstractJiraContextProvider {

    private Map params;
    private PluginSettingsFactory pluginSettingsFactory;

    private PluginSettings pluginSettings;


    @Inject
    public UseResponseLinkButtonProvider(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {

        pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);

        boolean needToShow = Boolean.parseBoolean(pluginSettings.getAutosendingFlag());

        if(needToShow) {
            //show
        } else {
            // don't show
        }

        return new HashMap();
    }
}
