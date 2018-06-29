package useresponse.atlassian.plugins.jira.settings;

public interface PluginSettings {
    String getUseResponseDomain();
    void setUseResponseDomain(String domain);

    String getUseResponseApiKey();
    void setUseResponseApiKey(String apiKey);

}
