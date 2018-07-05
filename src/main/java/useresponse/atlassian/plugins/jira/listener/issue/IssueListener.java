package useresponse.atlassian.plugins.jira.listener.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import useresponse.atlassian.plugins.jira.manager.impl.CommentLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.StatusesLinkManagerImpl;
import useresponse.atlassian.plugins.jira.manager.impl.UseResponseObjectManagerImpl;
import useresponse.atlassian.plugins.jira.model.StatusesLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import com.google.gson.internal.LinkedTreeMap;
import com.atlassian.jira.issue.status.Status;


import com.atlassian.jira.config.DefaultStatusManager;


import java.util.Collection;

@Component
public class IssueListener implements InitializingBean, DisposableBean {

    @JiraImport
    private final EventPublisher eventPublisher;

    @Autowired
    protected UseResponseObjectManagerImpl useResponseObjectManager;

    @Autowired
    private CommentLinkManagerImpl commentLinkManager;

    @Autowired
    private StatusesLinkManagerImpl statusesLinkManager;

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

        DefaultStatusManager statusManager = ComponentAccessor.getComponent(DefaultStatusManager.class);

        Collection<Status> statuses = statusManager.getStatuses();


        if (typeId.equals(EventType.ISSUE_CREATED_ID)) {
            createAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_UPDATED_ID)) {
            updateAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_COMMENTED_ID)) {
            createCommentAction(issueEvent.getComment());
        } else if (typeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
            updateCommentAction(issueEvent.getComment());
        } else if (typeId.equals(EventType.ISSUE_DELETED_ID)) {
            deleteAction(issueEvent.getIssue());
        } else if (typeId.equals(EventType.ISSUE_COMMENT_DELETED_ID)) {
            deleteCommentAction(issueEvent.getComment());
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

    private void updateAction(Issue issue) throws Exception {
        Request request = new PutRequest();

        request.addParameter("title", issue.getSummary());
        request.addParameter("content", issue.getDescription());
        request.addParameter("status", findUseResponseStatusFromJiraStatus(issue.getStatus().getSimpleStatus().getName() ));

        UseResponseObject object = useResponseObjectManager.findByJiraId(issue.getId().intValue());

        String response = request.sendRequest(createPutIssueRequestUrl(object.getUseResponseId()));
    }

    private void createCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();

        int id = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("object_id", String.valueOf(id));
        request.addParameter("content", comment.getBody());

        String response = request.sendRequest(createPostCommentRequestUrl());

        int useResponsecommentId = getIdFromResponse(response);
        commentLinkManager.findOrAdd(useResponsecommentId, comment.getId().intValue());
    }

    private void updateCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();
        int id = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        request.addParameter("content", comment.getBody());

        String response = request.sendRequest(createPutCommentRequestUrl(id));
    }

    private void deleteAction(Issue issue) throws Exception {
        Request request = new DeleteRequest();
        int id = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();

        String response = request.sendRequest(createDeleteIssueRequestUrl(id));
    }

    private void deleteCommentAction(Comment comment) throws Exception {
        Request request = new DeleteRequest();
        int id = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();

        String response = request.sendRequest(createPutCommentRequestUrl(id));
    }


    private String createPostIssueRequestUrl() {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/objects.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private String createPutIssueRequestUrl(int id) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/objects/" + id + ".json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private String createDeleteIssueRequestUrl(int id) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/objects/" + id + "/trash.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private String createPostCommentRequestUrl() {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/comments.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private String createPutCommentRequestUrl(int id) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/comments/" + id + "/edit.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private String createDeleteCommentRequestUrl(int id) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        String apiString = "api/4.0/comments/" + id + "/trash.json";
        return domain + apiString + "?apiKey=" + apiKey;
    }

    private int getIdFromResponse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(response);
        return ((Long) ((JSONObject) object.get("success")).get("id")).intValue();
    }

    private String findUseResponseStatusFromJiraStatus(String jiraStatus) {
        return statusesLinkManager.findByJiraStatusName(jiraStatus).getUseResponseStatusSlug();
    }
}
