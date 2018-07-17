package useresponse.atlassian.plugins.jira.provider;

import com.atlassian.jira.plugin.webfragment.DefaultWebFragmentContext;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
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

    private final PluginSettings pluginSettings;


    @Inject
    public UseResponseLinkButtonProvider(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
    }

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        Map<String, Object> contextParams = jiraHelper.getContextParams();
        Map<String, Object> result = new HashMap<>();

        Properties properties = new Properties();
        String testProp;

        try {
            properties.load(getClass().getResourceAsStream("/jira-plugin.properties"));
            testProp = (String) properties.get("useResponselinkbutton.label");
        } catch (Exception e) {
            testProp = "Can't load!";
            e.printStackTrace();
        }

        String labelValue = null;
        try {
            if (testURConnection(pluginSettings.getUseResponseDomain(), pluginSettings.getUseResponseApiKey())) {
                labelValue = "Move to UseResponse";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("test", testProp);

        return result;
    }
}
