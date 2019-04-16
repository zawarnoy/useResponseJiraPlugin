package useresponse.atlassian.plugins.jira.provider;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import com.atlassian.plugin.PluginParseException;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class UseResponseLinkButtonProvider extends AbstractJiraContextProvider {

    private Map params;
    private PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    private UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private PluginSettingsImpl pluginSettings;

    @Inject
    public UseResponseLinkButtonProvider(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void init(Map params) throws PluginParseException {
        this.params = params;
    }

    @Override
    public Map getContextMap(ApplicationUser applicationUser, JiraHelper jiraHelper) {

        Map<String, String> linkParameters = new HashMap<String, String>();

        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");

        if (currentIssue != null) {
            linkParameters.put("link", createUseresponseObjectLink(currentIssue.getId().intValue()));
            linkParameters.put("label", createLabelForButton(currentIssue.getId().intValue()));
        }
        return linkParameters;
    }

    private String createUseresponseObjectLink(int issueId) {
        UseResponseObject object = useResponseObjectManager.findByJiraId(issueId);
        if(object == null) {
            return null;
        }
        return pluginSettings.getUseResponseDomain() + "agent/object/" + object.getUseResponseId();
    }

    private String createLabelForButton(int issueId) {
        UseResponseObject object = useResponseObjectManager.findByJiraId(issueId);
        if(object == null) {
            return null;
        }
        return "UseResponse - " + object.getUseResponseId();
    }
}
