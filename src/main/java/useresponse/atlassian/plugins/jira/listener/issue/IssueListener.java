package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.action.issue.*;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import com.google.gson.internal.LinkedTreeMap;


@Component
public class IssueListener implements InitializingBean, DisposableBean {

    @JiraImport
    private final EventPublisher eventPublisher;

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public IssueListener(EventPublisher eventPublisher, PluginSettingsFactory pluginSettingsFactory) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
     * Called when the plugin has been enabled.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     *
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }


    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        try {
            executeAction(issueEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeAction(IssueEvent issueEvent) throws Exception {
        Long typeId = issueEvent.getEventTypeId();

        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            createAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_UPDATED_ID)) {
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
        }
    }

    private void createAction(Issue issue) throws Exception {
        Request request = new PostRequest();
        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request.addParameter("content", issue.getDescription());
        request.addParameter("title", issue.getSummary());

        String response = request.sendRequest(createPostIssueRequestUrl());
        int useResponseId = getIdFromResponse(response);

        useResponseObjectManager.add(useResponseId, issue.getId().intValue());
    }

    private void updateAction() {
    }

    private void createCommentAction() {
    }

    private String createPostIssueRequestUrl() {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/objects.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private int getIdFromResponse(String response) throws ParseException {

        JSONParser parser = new JSONParser();

        JSONObject object = (JSONObject) parser.parse(response);

        return ((Long)((JSONObject) object.get("success")).get("id")).intValue();
    }
}
