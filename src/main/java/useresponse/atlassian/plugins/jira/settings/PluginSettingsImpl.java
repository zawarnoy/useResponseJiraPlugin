package useresponse.atlassian.plugins.jira.settings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class PluginSettingsImpl implements PluginSettings {

    private static final String PLUGIN_PREFIX = "useresponse.atlassian.plugins.jira";

    private static final String USERESPONSE_API_KEY = PLUGIN_PREFIX + ".apiKey";
    private static final String USERESPONSE_DOMAIN = PLUGIN_PREFIX + ".domain";
    private static final String USERESPONSE_AUTOSENDING_FLAG = PLUGIN_PREFIX + ".autosendingFlag";
    private static final String USERESPONSE_SYNC_STATUSES = PLUGIN_PREFIX + ".sync_statuses";
    private static final String USERESPONSE_SYNC_COMMENTS = PLUGIN_PREFIX + ".sync_comments";
    private static final String USERESPONSE_SYNC_BASIC_FIELDS = PLUGIN_PREFIX + ".sync_basic_fields";
    private static final String USERESPONSE_SYNC_TICKETS_DATA = PLUGIN_PREFIX + ".sync_tickets_data";

    @Inject
    private PluginSettingsFactory pluginSettingsFactory;

    Logger logger = LoggerFactory.getLogger(PluginSettingsImpl.class);

    public PluginSettingsImpl() {

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

    @Override
    public Boolean getSyncStatuses() {
        return Boolean.valueOf((String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_SYNC_STATUSES));
    }

    @Override
    public void setSyncStatuses(boolean syncStatuses) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_SYNC_STATUSES, String.valueOf(syncStatuses));
    }

    @Override
    public Boolean getSyncComments() {
        return Boolean.valueOf((String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_SYNC_COMMENTS));
    }

    @Override
    public void setSyncComments(boolean syncComments) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_SYNC_COMMENTS, String.valueOf(syncComments));
    }

    @Override
    public Boolean getSyncBasicFields() {
        return Boolean.valueOf((String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_SYNC_BASIC_FIELDS));
    }

    @Override
    public void setSyncBasicFields(boolean basicFields) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_SYNC_BASIC_FIELDS, String.valueOf(basicFields));
    }

    @Override
    public Boolean getSyncTicketsData() {
        return Boolean.valueOf((String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_SYNC_TICKETS_DATA));
    }

    @Override
    public void setSyncTicketsData(boolean syncTicketsData) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_SYNC_TICKETS_DATA, String.valueOf(syncTicketsData));
    }
}
