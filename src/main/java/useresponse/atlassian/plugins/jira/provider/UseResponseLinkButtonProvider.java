package useresponse.atlassian.plugins.jira.provider;

import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

    public class UseResponseLinkButtonProvider extends AbstractJiraContextProvider {

//    private Map params;

    private final PluginSettingsFactory pluginSettingsFactory;
    private final PluginSettings pluginSettings;


    @Inject
    public UseResponseLinkButtonProvider(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
    }
//
//    @Override
//    public void init(Map params) throws PluginParseException {
//        this.params = params;
//    }

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        Map<String, Object> map = jiraHelper.getContextParams();

        return null;
    }
}
