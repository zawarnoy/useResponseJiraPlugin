package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
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


        Request request = new PostRequest();

        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request.addParameter("title", "Hello from jira: "); //+ issueEvent.getIssue().getDescription());
        request.addParameter("content", "testing request from jira");

        try {
            request.sendRequest(createRequestUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new CreateIssueAction().execute(issueEvent.getIssue());
            this.useResponseObjectManager.add( 11111,issueEvent.getEventTypeId().intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Action action = selectAction(issueEvent.getEventTypeId());
        if (action != null) {
            try {
                action.execute(issueEvent.getIssue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Action selectAction(Long eventTypeId) {
        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            return new CreateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            return new UpdateIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            return new CommentCreateAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            return new UpdateCommentAction();
        } else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
            return new DeleteIssueAction();
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            return new DeleteCommentAction();
        } else {
            return null;
        }
    }

    private String createRequestUrl() throws Exception {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/objects.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }
}
