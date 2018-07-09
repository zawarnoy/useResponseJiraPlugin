package useresponse.atlassian.plugins.jira.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.attachment.AttachmentStore;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.util.AttachmentUtils;
import com.atlassian.jira.util.PathUtils;
import com.atlassian.jira.util.io.InputStreamConsumer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.awt.AWTAccessor;
import useresponse.atlassian.plugins.jira.manager.*;
import useresponse.atlassian.plugins.jira.model.CommentLink;
import useresponse.atlassian.plugins.jira.model.IssueFileLink;
import useresponse.atlassian.plugins.jira.model.UseResponseObject;
import useresponse.atlassian.plugins.jira.request.DeleteRequest;
import useresponse.atlassian.plugins.jira.request.PostRequest;
import useresponse.atlassian.plugins.jira.request.PutRequest;
import useresponse.atlassian.plugins.jira.request.Request;
import useresponse.atlassian.plugins.jira.settings.PluginSettings;
import useresponse.atlassian.plugins.jira.settings.PluginSettingsImpl;
import useresponse.atlassian.plugins.jira.storage.ConstStorage;
import com.atlassian.jira.issue.AttachmentManager;

import org.apache.commons.io.IOUtils;


import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class IssueActionService {

    private CommentLinkManager commentLinkManager;
    private UseResponseObjectManager useResponseObjectManager;
    private StatusesLinkManager statusesLinkManager;
    private PluginSettingsFactory pluginSettingsFactory;
    private PriorityLinkManager priorityLinkManager;
    private AttachmentManager attachmentManager;
    private RendererManager rendererManager;
    private IssueFileLinkManager issueFileLinkManager;

    public IssueActionService(PluginSettingsFactory pluginSettingsFactory,
                              CommentLinkManager commentLinkManager,
                              UseResponseObjectManager useResponseObjectManager,
                              StatusesLinkManager statusesLinkManager,
                              PriorityLinkManager priorityLinkManager,
                              AttachmentManager attachmentManager,
                              RendererManager rendererManager,
                              IssueFileLinkManager issueFileLinkManager) {
        this.statusesLinkManager = statusesLinkManager;
        this.useResponseObjectManager = useResponseObjectManager;
        this.commentLinkManager = commentLinkManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.priorityLinkManager = priorityLinkManager;
        this.attachmentManager = attachmentManager;
        this.rendererManager = rendererManager;
        this.issueFileLinkManager = issueFileLinkManager;
    }

    public void createAction(Issue issue) throws Exception {
        Request request = new PostRequest();
        request.addParameter("ownership", "helpdesk");
        request.addParameter("object_type", "ticket");
        request = addChangeableParametersToRequest(request, issue);
        String response = request.sendRequest(createPostIssueRequestUrl());
        useResponseObjectManager.add(getIdFromResponse(response), issue.getId().intValue());
    }

    public void updateAction(Issue issue) throws Exception {
        Request request = new PutRequest();
        request = addChangeableParametersToRequest(request, issue);
        request.addParameter("status", findUseResponseStatusFromJiraStatus(issue.getStatus().getSimpleStatus().getName()));
        UseResponseObject object = useResponseObjectManager.findByJiraId(issue.getId().intValue());
        String response = request.sendRequest(createPutIssueRequestUrl(object.getUseResponseId()));
    }

    public void createCommentAction(Comment comment) throws Exception {
        CommentLink commentLink = commentLinkManager.findByJiraId(comment.getId().intValue());
        if (commentLink != null) {
            updateCommentAction(comment);
            return;
        }
        Request request = new PostRequest();
        request = prepareRequest(request);

        int id = useResponseObjectManager.findByJiraId(comment.getIssue().getId().intValue()).getUseResponseId();
        request.addParameter("object_id", String.valueOf(id));
        request.addParameter("content", comment.getBody());

        String response = request.sendRequest(createPostCommentRequestUrl());

        commentLinkManager.findOrAdd(getIdFromResponse(response), comment.getId().intValue());
    }

    public void updateCommentAction(Comment comment) throws Exception {
        Request request = new PostRequest();
        request = prepareRequest(request);
        int id = commentLinkManager.findByJiraId(comment.getId().intValue()).getUseResponseCommentId();
        request.addParameter("content", comment.getBody());
        String response = request.sendRequest(createPutCommentRequestUrl(id));
    }

    public void deleteAction(Issue issue) throws Exception {
        Request request = new DeleteRequest();
        request = prepareRequest(request);
        int id = useResponseObjectManager.findByJiraId(issue.getId().intValue()).getUseResponseId();
        String response = request.sendRequest(createDeleteIssueRequestUrl(id));
    }

    public void deleteCommentAction(IssueEvent issueEvent) {
        // DOESN'T WORK
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
        return pluginSettings.getUseResponseDomain() + ConstStorage.API_STRING + requestString + "?apiKey=" + pluginSettings.getUseResponseApiKey();
    }

    private int getIdFromResponse(String response) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(response);
        return ((Long) ((JSONObject) object.get("success")).get("id")).intValue();
    }

    private Request prepareRequest(Request request) {
        request.addParameter("from_jira", "1");
        request.addParameter("treat_as_html", "1");
        return request;
    }

    private String findUseResponseStatusFromJiraStatus(String jiraStatus) {
        return statusesLinkManager.findByJiraStatusName(jiraStatus).getUseResponseStatusSlug();
    }

    private Request addAttachmentsToRequest(Request request, Issue issue) throws Exception {
        Collection<Attachment> attachments = attachmentManager.getAttachments(issue);
        int issueId = issue.getId().intValue();
        ArrayList<Map> attachmentsData = new ArrayList<Map>();

        String filename;

        for (Attachment attachment : attachments) {
             filename = attachment.getFilename();
            if (checkNeedToSent(issueId, filename)) {
                attachmentsData.add(transformAttachmentForRequest(attachment));
                issueFileLinkManager.add(issueId, filename);
            }
        }
        request.addParameter("attachments", attachmentsData);
        return request;
    }

    private boolean checkNeedToSent(int issueId, String attachmentName) {
        return issueFileLinkManager.find(issueId, attachmentName) == null;
    }

    private Map<String, String> transformAttachmentForRequest(Attachment attachment) throws Exception {
        String body = null;
        Map<String, String> attachmentData = new HashMap<>();
        body = attachmentManager.streamAttachmentContent(attachment, inputStream -> {
            byte[] file = IOUtils.toByteArray(inputStream);
            return Base64.getEncoder().encodeToString(file);
        });
        attachmentData.put("name", attachment.getFilename());
        attachmentData.put("body", body);
        return attachmentData;
    }

    private Request addResponsibleToRequest(Request request, Issue issue) {
        try {
            request.addParameter("responsible_email", issue.getAssignee().getEmailAddress());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return request;
    }

    private Request addChangeableParametersToRequest(Request request, Issue issue) {

        try {
            IssueRenderContext renderContext = new IssueRenderContext(issue);
            JiraRendererPlugin renderer = rendererManager.getRendererForType("atlassian-wiki-renderer");
            String html = renderer.render(issue.getDescription(), renderContext);
            request.addParameter("content", html);
            request.addParameter("title", issue.getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            request.addParameter("force_author", issue.getReporterUser().getEmailAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            request.addParameter("tags", getTagsFromLabels(issue.getLabels()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            request.addParameter("priority", priorityLinkManager.findByJiraPriorityName(issue.getPriority().getName()).getUseResponsePriority().getUseResponsePrioritySlug());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            request = addResponsibleToRequest(request, issue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            request = addAttachmentsToRequest(request, issue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request = prepareRequest(request);
        return request;
    }

    private List<String> getTagsFromLabels(Set<Label> labels) {
        Iterator<Label> iterator = labels.iterator();
        List<String> result = new ArrayList<String>();
        while (iterator.hasNext()) {
            result.add(iterator.next().getLabel());
        }
        return result;
    }

}
