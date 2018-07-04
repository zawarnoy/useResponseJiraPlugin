package useresponse.atlassian.plugins.jira.settings;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import javax.inject.Inject;

public class PluginSettingsImpl implements PluginSettings {

    private static final String PLUGIN_PREFIX = "useresponse.atlassian.plugins.jira";

    private static final String USERESPONSE_API_KEY = PLUGIN_PREFIX + ".apiKey";
    private static final String USERESPONSE_DOMAIN = PLUGIN_PREFIX + ".domain";
    private static final String USERESPONSE_OPEN_STATUS = PLUGIN_PREFIX + ".open_status";
    private static final String USERESPONSE_IN_PROGRESS_STATUS = PLUGIN_PREFIX + ".in_progress_status";
    private static final String USERESPONSE_REOPENED_STATUS = PLUGIN_PREFIX + ".reopened_status";
    private static final String USERESPONSE_RESOLVED_STATUS = PLUGIN_PREFIX + ".resolved_status";
    private static final String USERESPONSE_CLOSED_STATUS = PLUGIN_PREFIX + ".closed_status";
    private static final String USERESPONSE_TODO_STATUS = PLUGIN_PREFIX + ".todo_status";
    private static final String USERESPONSE_DONE_STATUS = PLUGIN_PREFIX + ".done_status";

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
    public String getUseResponseOpenStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_OPEN_STATUS);
    }

    @Override
    public void setUseResponseOpenStatus(String openStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_OPEN_STATUS, openStatus);

    }

    @Override
    public String getUseResponseInProgressStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_IN_PROGRESS_STATUS);
    }

    @Override
    public void setUseResponseInProgressStatus(String inProgresStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_IN_PROGRESS_STATUS, inProgresStatus);

    }

    @Override
    public String getUseResponseReopenedStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_REOPENED_STATUS);
    }

    @Override
    public void setUseResponseReopenedStatus(String reopenedStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_REOPENED_STATUS, reopenedStatus);

    }

    @Override
    public String getUseResponseResolvedStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_RESOLVED_STATUS);
    }

    @Override
    public void setUseResponseResolvedStatus(String resolvedStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_RESOLVED_STATUS, resolvedStatus);
    }

    @Override
    public void setUseResponseClosedStatus(String closedStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_CLOSED_STATUS, closedStatus);
    }

    @Override
    public String getUseResponseClosedStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_CLOSED_STATUS);
    }

    @Override
    public String getUseResponseToDoStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_TODO_STATUS);
    }

    @Override
    public void setUseResponseToDoStatus(String toDoStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_TODO_STATUS, toDoStatus);
    }

    @Override
    public String getUseResponseDoneStatus() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(USERESPONSE_DONE_STATUS);
    }

    @Override
    public void setUseResponseDoneStatus(String doneStatus) {
        pluginSettingsFactory.createGlobalSettings().put(USERESPONSE_DONE_STATUS, doneStatus);
    }


}
