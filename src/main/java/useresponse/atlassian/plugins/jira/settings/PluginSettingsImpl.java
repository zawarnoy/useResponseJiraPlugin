package useresponse.atlassian.plugins.jira.settings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class PluginSettingsImpl implements PluginSettings {

    private static final String PLUGIN_PREFIX = "useresponse.atlassian.plugins.jira";

    private static final String USERESPONSE_API_KEY = PLUGIN_PREFIX + ".apiKey";
    private static final String USERESPONSE_DOMAIN = PLUGIN_PREFIX + ".domain";
    private static final String USERESPONSE_AUTOSENDING_FLAG = PLUGIN_PREFIX + ".autosendingFlag";
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String getUseResponseDomain() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_DOMAIN);
    }

    @Override
    public void setUseResponseDomain(String useResponseDomain) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_DOMAIN, useResponseDomain);
    }

    @Override
    public String getUseResponseApiKey() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_API_KEY);
    }

    @Override
    public void setUseResponseApiKey(String apiKey) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_API_KEY, apiKey);
    }

    @Override
    public String getAutosendingFlag() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_AUTOSENDING_FLAG);
    }

    @Override
    public void setAutosendingFlag(String autosendingFlag) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_AUTOSENDING_FLAG, autosendingFlag);
    }


}
