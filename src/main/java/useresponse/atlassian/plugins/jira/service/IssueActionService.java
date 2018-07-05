package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import useresponse.atlassian.plugins.jira.manager.CommentLinkManager;
import useresponse.atlassian.plugins.jira.manager.StatusesLinkManager;
import useresponse.atlassian.plugins.jira.manager.UseResponseObjectManager;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;

public class IssueActionService {

    private CommentLinkManager commentLinkManager;
    private UseResponseObjectManager useResponseObjectManager;
    private StatusesLinkManager statusesLinkManager;
    private PluginSettingsFactory pluginSettingsFactory;

    public IssueActionService(PluginSettingsFactory pluginSettingsFactory, CommentLinkManager commentLinkManager, UseResponseObjectManager useResponseObjectManager, StatusesLinkManager statusesLinkManager) {
        this.statusesLinkManager = statusesLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void createAction(Issue issue) throws Exception {
        Request request = new PostRequest();
        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request.addParameter("content", issue.getDescription());
        request.addParameter("title", issue.getSummary());
        String response = request.sendRequest(createPostIssueRequestUrl());

        useResponseObjectManager.add(getIdFromResponse(response), issue.getId().intValue());
    }

    public void updateAction(Issue issue) throws Exception {
        Request request = new PutRequest();

        request.addParameter("title", issue.getSummary());
        request.addParameter("content", issue.getDescription());
        request.addParameter("status", findUseResponseStatusFromJiraStatus(issue.getStatus().getSimpleStatus().getName()));

        UseResponseObject object = useResponseObjectManager.findByJiraId(issue.getId().intValue());

        String response = request.sendRequest(createPutIssueRequestUrl(object.getUseResponseId()));
    }

    public void createCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();

        int id = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("object_id", String.valueOf(id));
        request.addParameter("content", comment.getBody());

        String response = request.sendRequest(createPostCommentRequestUrl());

        commentLinkManager.findOrAdd( getIdFromResponse(response), comment.getId().intValue());
    }

    public void updateCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();
        int id = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        request.addParameter("content", comment.getBody());
        String response = request.sendRequest(createPutCommentRequestUrl(id));
    }

    public void deleteAction(Issue issue) throws Exception {
        Request request = new DeleteRequest();
        int id = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        String response = request.sendRequest(createDeleteIssueRequestUrl(id));
    }

    public void deleteCommentAction(IssueEvent issueEvent) throws Exception {
        /* TODO: */
//        Request request = new GetRequest();
//        String response = request.sendRequest(createDeleteCommentRequestUrl(id));
    }


    private String createPostIssueRequestUrl() {
        return collectUrl("objects.json");
    }

    private String createPutIssueRequestUrl(int id) {
        return collectUrl("objects/" + id + ".json");
    }

    private String createDeleteIssueRequestUrl(int id) {
        return collectUrl("objects/" + id + "/trash.json");
    }

    private String createPostCommentRequestUrl() {
        return collectUrl("comments.json");
    }

    private String createPutCommentRequestUrl(int id) {
        return collectUrl("comments/" + id + "/edit.json");
    }

    private String createDeleteCommentRequestUrl(int id) {
        return collectUrl("comments/" + id + "/trash.json");
    }

    private String collectUrl(String requestString) {
        PluginSettings pluginSettings = new PluginSettingsImpl(pluginSettingsFactory);
        String domain = pluginSettings.getUseResponseDomain();
        String apiKey = pluginSettings.getUseResponseApiKey();
        return domain + ConstStorage.API_STRING + requestString + "?apiKey=" + apiKey;
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
