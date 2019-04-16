package useresponse.atlassian.plugins.jira.action.servlet;

import useresponse.atlassian.plugins.jira.action.Action;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.storage.Storage;

import java.util.Map;

public class SettingsSendAction implements Action {

    private Map params;
    private PluginSettings pluginSettings;

    public SettingsSendAction(Map params, PluginSettings pluginSettings) {
        this.params = params;
        this.pluginSettings = pluginSettings;
    }

    @Override
    public String call() throws Exception {
        Request request = new PostRequest();
        request.addParameter(params);
        String url = createUrl();
        return request.sendRequest(url);
    }

    private String createUrl() {
        return pluginSettings.getUseResponseDomain() + Storage.API_STRING + Storage.JIRA_SETTINGS_ROUTE + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }
}